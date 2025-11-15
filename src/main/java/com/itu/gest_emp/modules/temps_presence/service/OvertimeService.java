package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.dto.overtime.*;
import com.itu.gest_emp.modules.temps_presence.repository.OvertimeRepository;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.personnel.repository.PersonnelRhRepository;
import com.itu.gest_emp.modules.shared.repository.PersonRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.repository.NotificationRhRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PersonnelRhRepository personnelRepository;
    private final PersonRepository personRepository;
    private final NotificationRhRepository notificationRhRepository;

    public List<OvertimeRh> getOvertimeByPersonnel(Long personnelId) {
        return overtimeRepository.findByPersonnel_Id(personnelId);
    }

    public List<OvertimeRh> getOvertimeEnAttente() {
        return overtimeRepository.findByStatut("en_attente");
    }

    @Transactional
    public OvertimeRh createOvertime(OvertimeCreateDto dto) {
        PersonnelRh personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        OvertimeRh overtime = new OvertimeRh();
        overtime.setPersonnel(personnel);

        if (dto.getAttendanceId() != null) {
            attendanceRepository.findById(dto.getAttendanceId()).ifPresent(overtime::setAttendance);
        }

        overtime.setDateHs(dto.getDateHs());
        overtime.setNombreHeures(dto.getNombreHeures());
        overtime.setTypeHs(dto.getTypeHs());
        overtime.setSalaireHoraireBase(dto.getSalaireHoraireBase());
        overtime.setTauxMajorationFromType(); // Définit le taux selon le type

        OvertimeRh savedOvertime = overtimeRepository.save(overtime);

        // Notification intégrée pour demande d'heures supplémentaires
        createOvertimePendingNotification(personnel, savedOvertime);

        return savedOvertime;
    }

    @Transactional
    public OvertimeRh validerOvertime(Long overtimeId, OvertimeValidationDto validationDto, Long validatorId) {
        OvertimeRh overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("Heures supplémentaires non trouvées"));

        Person validator = personRepository.findById(validatorId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        if (validationDto.getApprouve()) {
            overtime.setStatut("approuve");
            // Notification d'approbation intégrée
            createOvertimeApprovalNotification(overtime.getPersonnel().getPerson(), overtime);
        } else {
            overtime.setStatut("refuse");
            // Notification de refus intégrée
            createOvertimeRejectionNotification(overtime.getPersonnel().getPerson(), overtime,
                    validationDto.getCommentaire());
        }

        overtime.setValidatedBy(validator);
        overtime.setValidationDate(LocalDateTime.now());

        return overtimeRepository.save(overtime);
    }

    public List<OvertimeRh> getOvertimeForPaiement(LocalDate startDate, LocalDate endDate) {
        return overtimeRepository.findByDateHsBetweenAndStatut(startDate, endDate, "approuve");
    }

    public List<OvertimeRh> getAllOvertime() {
        return overtimeRepository.findAll();
    }

    // === METHODES DE NOTIFICATION INTEGREES ===

    /**
     * Crée une notification pour demande d'heures supplémentaires en attente
     */
    private void createOvertimePendingNotification(PersonnelRh personnel, OvertimeRh overtime) {
        try {
            // Récupérer le manager du personnel (à adapter selon votre structure)
            Optional<Person> manager = findManagerOfPersonnel(personnel);

            if (manager.isPresent()) {
                String title = "Demande d'heures supplémentaires en attente";
                String message = String.format(
                        "%s %s a soumis une demande d'heures supplémentaires pour le %s (%s heures)",
                        personnel.getPerson().getPrenom(),
                        personnel.getPerson().getNom(),
                        overtime.getDateHs().toString(),
                        overtime.getNombreHeures().toString());

                NotificationRh notification = new NotificationRh(
                        manager.get(),
                        title,
                        message,
                        "overtime_pending",
                        "OvertimeRh",
                        overtime.getId());

                notificationRhRepository.save(notification);
                log.info("Notification demande HS créée pour le manager {}", manager.get().getNom());
            }

        } catch (Exception e) {
            log.error("Erreur création notification demande HS: {}", e.getMessage());
            // Ne pas propager l'exception pour ne pas interrompre la création
        }
    }

    /**
     * Crée une notification d'approbation d'heures supplémentaires
     */
    private void createOvertimeApprovalNotification(Person person, OvertimeRh overtime) {
        try {
            String title = "Heures supplémentaires approuvées";
            String message = String.format(
                    "Vos heures supplémentaires du %s (%s heures) ont été approuvées. Montant: %s €",
                    overtime.getDateHs().toString(),
                    overtime.getNombreHeures().toString(),
                    overtime.getMontantHs() != null ? overtime.getMontantHs().toString() : "0");

            NotificationRh notification = new NotificationRh(
                    person,
                    title,
                    message,
                    "overtime_approval",
                    "OvertimeRh",
                    overtime.getId());

            notificationRhRepository.save(notification);
            log.info("Notification approbation HS créée pour {}", person.getNom());

        } catch (Exception e) {
            log.error("Erreur création notification approbation HS: {}", e.getMessage());
        }
    }

    /**
     * Crée une notification de refus d'heures supplémentaires
     */
    private void createOvertimeRejectionNotification(Person person, OvertimeRh overtime, String commentaire) {
        try {
            String title = "Heures supplémentaires refusées";
            String message = String.format(
                    "Vos heures supplémentaires du %s (%s heures) ont été refusées.",
                    overtime.getDateHs().toString(),
                    overtime.getNombreHeures().toString());

            if (commentaire != null && !commentaire.trim().isEmpty()) {
                message += " Motif: " + commentaire;
            }

            NotificationRh notification = new NotificationRh(
                    person,
                    title,
                    message,
                    "overtime_rejection",
                    "OvertimeRh",
                    overtime.getId());

            notificationRhRepository.save(notification);
            log.info("Notification refus HS créée pour {}", person.getNom());

        } catch (Exception e) {
            log.error("Erreur création notification refus HS: {}", e.getMessage());
        }
    }

    /**
     * Notification pour alerte d'heures supplémentaires excessives
     */
    @Transactional
    public void createAlerteHsExcessivesNotification(Long personnelId, Double heuresMensuelles, Double limite) {
        try {
            PersonnelRh personnel = personnelRepository.findById(personnelId)
                    .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

            String title = "Alerte heures supplémentaires";
            String message = String.format(
                    "Vous avez dépassé le seuil d'heures supplémentaires mensuelles. Total: %.1f heures (limite: %.1f heures)",
                    heuresMensuelles, limite);

            NotificationRh notification = new NotificationRh(
                    personnel.getPerson(),
                    title,
                    message,
                    "overtime_excessive");

            notificationRhRepository.save(notification);
            log.info("Notification HS excessives créée pour {}", personnel.getPerson().getNom());

        } catch (Exception e) {
            log.error("Erreur création notification HS excessives: {}", e.getMessage());
        }
    }

    /**
     * Notification pour intégration paie réussie des heures supplémentaires
     */
    @Transactional
    public void createIntegrationPaieHsReussieNotification(Long personnelId, int mois, int annee) {
        try {
            PersonnelRh personnel = personnelRepository.findById(personnelId)
                    .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

            String title = "Heures supplémentaires intégrées à la paie";
            String message = String.format(
                    "Vos heures supplémentaires pour %d/%d ont été intégrées avec succès au module de paie.",
                    mois, annee);

            NotificationRh notification = new NotificationRh(
                    personnel.getPerson(),
                    title,
                    message,
                    "payroll_overtime_integration_success");

            notificationRhRepository.save(notification);
            log.info("Notification intégration paie HS créée pour {}", personnel.getPerson().getNom());

        } catch (Exception e) {
            log.error("Erreur création notification intégration paie HS: {}", e.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour trouver le manager d'un personnel
     * À adapter selon votre structure organisationnelle
     */
    private Optional<Person> findManagerOfPersonnel(PersonnelRh personnel) {
        // Implémentation basique - à adapter
        // Exemple: récupérer le manager du service du personnel
        try {

            return personRepository.findById(13L);

        } catch (Exception e) {
            log.warn("Impossible de trouver un manager pour le personnel {}", personnel.getId());
            return Optional.empty();
        }
    }

    /**
     * Méthode pour vérifier et notifier les heures supplémentaires excessives
     */
    @Transactional
    public void checkAndNotifyExcessiveOvertime(int mois, int annee, Double limiteHeures) {
        LocalDate startDate = LocalDate.of(annee, mois, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Object[]> resultats = new ArrayList<>();
        // overtimeRepository.findHeuresMensuellesParPersonnel(startDate, endDate);

        for (Object[] resultat : resultats) {
            Long personnelId = (Long) resultat[0];
            Double totalHeures = (Double) resultat[1];

            if (totalHeures > limiteHeures) {
                createAlerteHsExcessivesNotification(personnelId, totalHeures, limiteHeures);

                // Notification également au manager
                Optional<Person> manager = findManagerOfPersonnel(
                        personnelRepository.findById(personnelId).orElse(null));
                if (manager.isPresent()) {
                    createAlerteHsExcessivesManagerNotification(
                            manager.get(), personnelId, totalHeures, limiteHeures);
                }
            }
        }
    }

    /**
     * Notification pour le manager - heures supplémentaires excessives d'un employé
     */
    private void createAlerteHsExcessivesManagerNotification(Person manager, Long personnelId,
            Double heuresMensuelles, Double limite) {
        try {
            PersonnelRh personnel = personnelRepository.findById(personnelId).orElse(null);
            if (personnel == null)
                return;

            String title = "Alerte heures supplémentaires employé";
            String message = String.format(
                    "%s %s a dépassé le seuil d'heures supplémentaires mensuelles. Total: %.1f heures (limite: %.1f heures)",
                    personnel.getPerson().getPrenom(),
                    personnel.getPerson().getNom(),
                    heuresMensuelles,
                    limite);

            NotificationRh notification = new NotificationRh(
                    manager,
                    title,
                    message,
                    "overtime_excessive_manager");

            notificationRhRepository.save(notification);
            log.info("Notification HS excessives manager créée pour {}", manager.getNom());

        } catch (Exception e) {
            log.error("Erreur création notification HS excessives manager: {}", e.getMessage());
        }
    }
}