package com.itu.gest_emp.modules.absence_conge.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itu.gest_emp.modules.absence_conge.model.AbsencePV;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.repository.AbsencePVRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveTypeRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AbsencePVService {

    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AbsencePVRepository absencePVRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final NotificationRhService notificationService;
    private final UtilisateurService utilisateurService;
    private final LeaveTypeRepository leaveTypeRepository;

    /**
     * Traite une absence non autorisée et génère PV si répétée
     */
    public void traiterAbsenceNonAutorisee(PersonnelRh personnel, LocalDate dateAbsence) {
        LeaveRequest lr = leaveRequestRepository.findByDateDebut(dateAbsence);
        if (lr != null && lr.getIsAbsenceNonAutorisee() != null && lr.getIsAbsenceNonAutorisee()) {
            return;
        }
        // Vérifier le nombre d'absences dans le mois
        YearMonth mois = YearMonth.from(dateAbsence);
        LocalDate start = mois.atDay(1);
        LocalDate end = mois.atEndOfMonth();

        long absencesThisMonth = attendanceRepository
                .findByPersonnel_IdAndDatePointageBetween(personnel.getId(), start, end)
                .stream()
                .filter(a -> "absent".equals(a.getStatut()))
                .count();

        // Créer LeaveRequest spéciale pour absence non autorisée
        LeaveRequest absence = new LeaveRequest();
        absence.setLeaveType(
                leaveTypeRepository.findById(9L).orElseThrow(
                        () -> new IllegalStateException("Le type de congé 'Absence non autorisée' est introuvable")));
        absence.setPersonnel(personnel);
        absence.setDateDebut(dateAbsence);
        absence.setDateFin(dateAbsence);
        absence.setIsAbsenceNonAutorisee(true);
        absence.setStatut(LeaveRequest.LeaveStatus.ABSENCE_NON_AUTORISEE);
        absence.setDateSoumission(LocalDate.now());
        absence.setNombreJours(BigDecimal.ONE);
        absence.setAbsenceRepeteeCount((int) absencesThisMonth + 1);

        leaveRequestRepository.save(absence);

        // Appliquer déduction salariale si pas de congés disponibles
        appliquerDeductionSalaire(personnel, dateAbsence);

        // Générer PV si 2+ absences dans le mois
        if (absencesThisMonth >= 1) {
            genererPVAbsenceRepetee(personnel, dateAbsence, (int) (absencesThisMonth + 1));
        }

        log.info("Absence non autorisée traitée pour {} le {}", personnel.getPerson().getFullName(), dateAbsence);
    }

    private void appliquerDeductionSalaire(PersonnelRh personnel, LocalDate dateAbsence) {
        List<LeaveBalance> soldes = leaveBalanceRepository.findByPersonnel_IdAndAnneeAndLeaveType_Name(personnel.getId(), dateAbsence.getYear(),"Conge paye");
        boolean soldeUtilise = false;

        for (LeaveBalance solde : soldes) {
            if (solde.getSoldeRestant().compareTo(BigDecimal.ONE) >= 0) {
                solde.setSoldePris(solde.getSoldePris().add(BigDecimal.ONE));
                leaveBalanceRepository.save(solde);
                soldeUtilise = true;

                break;
            }
        }

        if (!soldeUtilise) {
            // Aucun solde suffisant
            LeaveRequest lr = leaveRequestRepository.findByPersonnelAndDateDebut(personnel, dateAbsence);
            if (lr != null) {
                lr.setDeducted(true);
                leaveRequestRepository.save(lr);
            }
            envoyerNotificationDeduction(personnel, dateAbsence);
        }
    }

    private void envoyerNotificationDeduction(PersonnelRh personnel, LocalDate dateAbsence) {
        NotificationRh notif = new NotificationRh();
        notif.setTitle("Déduction salariale pour absence non autorisée");
        notif.setMessage(String.format("Votre absence le %s entraîne une déduction salariale.",
                dateAbsence));
        notif.setType("deduction_salaire");
        notif.setRecipient(utilisateurService.findByPersonId(personnel.getPerson().getId()));
        notificationService.createNotification(notif);
    }

    private void genererPVAbsenceRepetee(PersonnelRh personnel, LocalDate dateAbsence, int nombreAbsences) {
        AbsencePV pv = new AbsencePV();
        pv.setPersonnel(personnel);
        pv.setDateAbsence(dateAbsence);
        pv.setMotifAbsence("Absence répétée non autorisée - " + nombreAbsences + " occurrence(s) ce mois");
        pv.setNombreAbsencesRepetees(nombreAbsences);
        pv.setStatut(AbsencePV.PVStatut.EN_ATTENTE);
        absencePVRepository.save(pv);

        // Notification RH et employé
        notifierPVGeneration(personnel, nombreAbsences);
        demanderExplicationEmploye(personnel, pv);
    }

    private void notifierPVGeneration(PersonnelRh personnel, int nombreAbsences) {
        Utilisateur manager = personnel.getPost().getEquipe().getService().getManager();
        if (manager != null) {
            NotificationRh notif = new NotificationRh();
            notif.setTitle("PV généré pour absence répétée");
            notif.setMessage(String.format("%s a %d absence(s) ce mois", personnel.getPerson().getFullName(),
                    nombreAbsences));
            notif.setType("pv_absence");
            notif.setRecipient(manager);
            notificationService.createNotification(notif);
        }
    }

    private void demanderExplicationEmploye(PersonnelRh personnel, AbsencePV pv) {
        NotificationRh notif = new NotificationRh();
        notif.setTitle("Demande d'explication pour absence répétée");
        notif.setMessage(
                String.format("Vous avez %d absence(s) non autorisée(s) ce mois. Veuillez fournir une explication.",
                        pv.getNombreAbsencesRepetees()));
        notif.setType("demande_explication");
        notif.setRecipient(utilisateurService.findByPersonId(personnel.getPerson().getId()));
        notificationService.createNotification(notif);
    }
}
