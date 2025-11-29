package com.itu.gest_emp.modules.absence_conge.service;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest.LeaveStatus;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveWorkflowService {

        @Autowired
        private LeaveRequestRepository leaveRequestRepository;

        @Autowired
        private NotificationRhService notificationService;

        @Autowired
        private LeaveBalanceService leaveBalanceService;

        @Autowired
        private UtilisateurService utilisateurService;

        @Autowired
        private NotificationRhService notificationRhService;

        @Transactional
        public void enregistrerRetour(LeaveRequest leaveRequest, LocalDate dateRetour, String justification) {
                leaveRequest.enregistrerRetourEffectif(dateRetour, justification);

                if (leaveRequest.isRetourEnRetard()) {
                        String titre = "Retour de congé en retard";
                        String message = String.format(
                                        "L'employé %s est revenu en retard de son congé prévu le %s. Justification : %s",
                                        leaveRequest.getPersonnel().getPerson().getFullName(),
                                        leaveRequest.getDateFin(),
                                        justification != null ? justification : "Aucune");

                        notificationRhService.createNotification(
                                        leaveRequest.getPersonnel().getPost().getEquipe().getService().getManager(),
                                        null,
                                        titre,
                                        message,
                                        null, null, null);
                }

                leaveRequestRepository.save(leaveRequest);
        }

        /** Soumission d’une demande de congé */
        @Transactional
        public LeaveRequest submitLeaveRequest(LeaveRequest leaveRequest) {
                // 1. Vérifier le solde actuel
                LeaveBalance balance = leaveBalanceService.findCurrentBalance(leaveRequest);

                boolean soldeInsuffisant = balance.getSoldeRestant().compareTo(leaveRequest.getNombreJours()) < 0;

                if (soldeInsuffisant) {
                        // Notification au manager pour solde insuffisant
                        notificationRhService.createNotification(
                                        leaveRequest.getPersonnel().getPost().getEquipe().getService().getManager(),
                                        utilisateurService.findByPersonId(leaveRequest.getPersonnel().getId()),
                                        "Alerte solde insuffisant",
                                        String.format(
                                                        "L'employé %s a demandé %s jours alors qu'il ne reste que %s jours",
                                                        leaveRequest.getPersonnel().getPerson().getFullName(),
                                                        leaveRequest.getNombreJours(),
                                                        balance.getSoldeRestant()),
                                        null, null, null);

                        // Optionnel : définir un statut spécial
                        leaveRequest.setStatut(LeaveStatus.EN_ATTENTE_MANAGER_SOLDE_INSUFFISANT);
                } else {
                        // Statut normal
                        leaveRequest.setStatut(LeaveStatus.EN_ATTENTE_MANAGER);
                }

                // 2. Définir la date de soumission si non définie
                if (leaveRequest.getDateSoumission() == null) {
                        leaveRequest.setDateSoumission(LocalDate.now());
                }

                // 3. Vérifier délai de soumission (au moins 15 jours avant)
                long daysBetween = ChronoUnit.DAYS.between(leaveRequest.getDateSoumission(),
                                leaveRequest.getDateDebut());
                // if (daysBetween < 15) {
                //         throw new RuntimeException(
                //                         "La date de soumission doit être au moins 15 jours avant le début du congé");
                // }

                // 4. Enregistrer la demande
                LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

                // 5. Notification au manager pour toute nouvelle demande
                Utilisateur managerUser = leaveRequest.getPersonnel().getPost().getEquipe().getService().getManager();
                NotificationRh notif = new NotificationRh();
                notif.setTitle("Nouvelle demande de congé");
                notif.setMessage(String.format(
                                "Nouvelle demande de congé : %s du %s au %s",
                                leaveRequest.getPersonnel().getPerson().getFullName(),
                                leaveRequest.getDateDebut(),
                                leaveRequest.getDateFin()));
                notif.setRecipient(managerUser);
                notificationService.createNotification(notif);

                return saved;
        }

        /** Détermine quel statut doit être validé selon le rôle du validateur */
        public LeaveStatus getValidationStatusByRole(String role) {
                switch (role) {
                        case "MANAGER":
                                return LeaveStatus.EN_ATTENTE_MANAGER;

                        case "RH":
                                return LeaveStatus.EN_ATTENTE_RH;

                        case "ADMIN":
                                return LeaveStatus.APPROUVE; // ADMIN valide directement

                        default:
                                return LeaveStatus.APPROUVE;
                }
        }

        /** Approbation d'une demande */
        @Transactional
        public LeaveRequest approveLeaveRequest(Long requestId, Long validatorId, String comment) {

                LeaveRequest request = leaveRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

                Utilisateur validator = utilisateurService.findById(validatorId);

                /** 1 ▸ Vérification workflow par rôle */
                LeaveStatus expectedStatus = getValidationStatusByRole(validator.getRole());

                if (!request.getStatut().equals(expectedStatus)) {
                        throw new RuntimeException("Cet utilisateur ne peut pas valider cette étape.");
                }

                /** 2 ▸ Vérifier congés simultanés dans le service */
                Long serviceId = request.getPersonnel().getPost().getEquipe().getService().getId();
                LocalDate d1 = request.getDateDebut();
                LocalDate d2 = request.getDateFin();

                List<LeaveRequest> overlapping = leaveRequestRepository
                                .findApprovedLeavesForServiceDuringPeriod(serviceId, d1, d2);

                if (!overlapping.isEmpty()) {
                        NotificationRh warn = new NotificationRh();
                        warn.setTitle("warning_overlapping_leave");

                        StringBuilder msg = new StringBuilder("Attention : Absences simultanées détectées :\n");
                        overlapping.forEach(lr -> msg.append("- ")
                                        .append(lr.getPersonnel().getPerson().getFullName())
                                        .append(" du ").append(lr.getDateDebut())
                                        .append(" au ").append(lr.getDateFin())
                                        .append("\n"));

                        warn.setMessage(msg.toString());
                        warn.setRecipient(validator);

                        notificationService.createNotification(warn);
                }

                /** 3 ▸ Mettre à jour le statut selon l’étape */
                if (validator.getRole().equals("MANAGER")) {
                        request.setStatut(LeaveStatus.EN_ATTENTE_RH);
                } else if (validator.getRole().equals("RH") || validator.getRole().equals("ADMIN")) {
                        request.setStatut(LeaveStatus.APPROUVE);
                }

                request.setValidatedBy(validator);
                request.setValidationDate(LocalDateTime.now());
                request.setValidationComment(comment);

                /** 4 ▸ Mise à jour du solde seulement quand APPROUVÉ */
                if (request.getStatut() == LeaveStatus.APPROUVE) {
                        leaveBalanceService.updateBalanceAfterApproval(request);
                }

                /** 5 ▸ Notification à l’employé */
                NotificationRh notif = new NotificationRh();
                notif.setTitle("leave_approval");
                notif.setMessage("Votre demande de congé a été approuvée.");
                notif.setRecipient(
                                utilisateurService.findByPersonId(request.getPersonnel().getPerson().getId()));

                notificationService.createNotification(notif);

                return leaveRequestRepository.save(request);
        }

        /** Rejet d’une demande */
        @Transactional
        public LeaveRequest rejectLeaveRequest(Long requestId, Long validatorId, String comment) {

                LeaveRequest request = leaveRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

                Utilisateur validator = utilisateurService.findById(validatorId);

                request.setStatut(LeaveStatus.REFUSE);
                request.setValidatedBy(validator);
                request.setValidationDate(LocalDateTime.now());
                request.setValidationComment(comment);

                /** Notification employé */
                NotificationRh notif = new NotificationRh();
                notif.setTitle("leave_rejection");
                notif.setMessage("Votre demande de congé a été refusée : " + comment);
                notif.setRecipient(
                                utilisateurService.findByPersonId(request.getPersonnel().getPerson().getId()));

                notificationService.createNotification(notif);

                return leaveRequestRepository.save(request);
        }
}
