package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.absence_conge.service.AbsencePVService;
import com.itu.gest_emp.modules.paie.model.PersonnelSalarySnapshot;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.model.RetardDeduction;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.temps_presence.repository.OvertimeRepository;
import com.itu.gest_emp.modules.temps_presence.repository.RetardDeductionRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PaieIntegrationService {

        private final AttendanceRepository attendanceRepository;
        private final OvertimeRepository overtimeRepository;
        private final LeaveRequestRepository leaveRequestRepository;
        private final LeaveBalanceRepository leaveBalanceRepository;
        private final RetardDeductionRepository retardDeductionRepository;
        private final AbsencePVService absencePVService;

        private static final int SCALE = 6;
        private static final RoundingMode RM = RoundingMode.HALF_UP;

        public Map<String, Object> exportDataForPaie(int month, int year, PersonnelRh personnel,
                        PersonnelSalarySnapshot snapshot) {

                Long personnelId = personnel.getId();
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();
                List<LeaveBalance> soldes = leaveBalanceRepository.findByPersonnel_IdAndAnneeAndLeaveType_Name(
                                personnel.getId(), year, "Conge paye");

                // Salaire journalier sécurisé
                BigDecimal salaireJournalier = snapshot.getTauxJournalier();

                // Récupérer les présences
                List<AttendanceRh> attendances = attendanceRepository
                                .findByPersonnel_IdAndDatePointageBetween(personnelId, startDate, endDate);

                // Récupérer les heures supplémentaires approuvées
                List<OvertimeRh> overtimes = overtimeRepository
                                .findByPersonnel_IdAndDateHsBetween(personnelId, startDate, endDate);

                // Traitement des absences non autorisées détectées sur attendance
                attendances.stream()
                                .filter(a -> "absent".equals(a.getStatut()))
                                .forEach(a -> absencePVService.traiterAbsenceNonAutorisee(personnel,
                                                a.getDatePointage()));

                // Récupérer les absences
                List<LeaveRequest> absences = leaveRequestRepository
                                .findByPersonnel_IdAndDateDebutBetween(personnelId, startDate, endDate);

                // Calcul des jours présents
                long joursPresents = attendances.stream()
                                .filter(a -> "present".equals(a.getStatut()) || "retard".equals(a.getStatut()))
                                .filter(a -> absences.stream().noneMatch(lr -> lr.getDateDebut()
                                                .equals(a.getDatePointage()) &&
                                                (!Boolean.TRUE.equals(lr.getIsCongePaye())
                                                                || Boolean.TRUE.equals(lr.getIsAbsenceNonAutorisee()))))
                                .count();

                // Calcul absences déduites
                long joursAbsencesDeduites = absences.stream()
                                .filter(lr -> Boolean.TRUE.equals(lr.isDeducted()))
                                .count();

                BigDecimal totalMontantRetard = BigDecimal.ZERO;
                for (AttendanceRh a : attendances) {
                        if (a.getRetardMinutes() != null && a.getRetardMinutes() > 0) {
                                totalMontantRetard = totalMontantRetard.add(
                                                appliquerRetard(personnel, a.getDatePointage(), a.getRetardMinutes(),
                                                                salaireJournalier, soldes));
                        }
                }

                // Heures supplémentaires approuvées
                BigDecimal totalHeuresSupplementaires = overtimes.stream()
                                .filter(o -> "approuve".equals(o.getStatut()))
                                .map(OvertimeRh::getNombreHeures)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalMontantHs = overtimes.stream()
                                .filter(o -> "approuve".equals(o.getStatut()))
                                .map(OvertimeRh::getMontantHs)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Montant des absences déduites
                BigDecimal totalMontantAbsencesDeduites = absences.stream()
                                .filter(lr -> Boolean.FALSE.equals(lr.getIsCongePaye())
                                                && Boolean.TRUE.equals(lr.isDeducted()))
                                .map(lr -> salaireJournalier.multiply(lr.getNombreJours()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal soldeTotalCongeRestant = soldes.stream()
                                .map(LeaveBalance::getSoldeRestant)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                System.out.println("Solde total conge restant: " + soldeTotalCongeRestant);
                BigDecimal valeurDroitConge = soldeTotalCongeRestant.multiply(salaireJournalier);

                // Construction du map de retour
                Map<String, Object> data = new HashMap<>();
                data.put("personnelId", personnelId);
                data.put("mois", month);
                data.put("annee", year);
                data.put("joursPresents", joursPresents);
                data.put("joursAbsencesDeduites", joursAbsencesDeduites);
                data.put("totalMontantRetard", totalMontantRetard);
                data.put("totalHeuresSupplementaires", totalHeuresSupplementaires);
                data.put("totalMontantHs", totalMontantHs);
                data.put("totalMontantAbsencesDeduites", totalMontantAbsencesDeduites);
                data.put("valeurDroitConge", valeurDroitConge);
                data.put("attendances", attendances);
                data.put("overtimes", overtimes);
                data.put("absences", absences);

                return data;
        }

        private BigDecimal appliquerRetard(PersonnelRh personnel, LocalDate datePointage, int retardMinutes,
                        BigDecimal salaireJournalier, List<LeaveBalance> soldes) {
                // Vérifier si le retard a déjà été traité
                RetardDeduction existing = retardDeductionRepository.findByPersonnelAndDatePointage(personnel,
                                datePointage);
                if (existing != null) {
                        return existing.getMontantDeduit();
                }

                // Conversion minutes en jours
                BigDecimal joursRetard = BigDecimal.valueOf(retardMinutes)
                                .divide(BigDecimal.valueOf(480), 10, RoundingMode.HALF_UP);

                // Récupérer les soldes de congés

                BigDecimal retardRestant = joursRetard;
                BigDecimal joursCouverts = BigDecimal.ZERO;

                for (LeaveBalance solde : soldes) {
                        BigDecimal soldeRestant = solde.getSoldeRestant();
                        if (soldeRestant.compareTo(BigDecimal.ZERO) > 0) {
                                BigDecimal aDeducter = retardRestant.min(soldeRestant);
                                solde.setSoldePris(solde.getSoldePris().add(aDeducter));
                                leaveBalanceRepository.save(solde);
                                retardRestant = retardRestant.subtract(aDeducter);
                                joursCouverts = joursCouverts.add(aDeducter);
                                if (retardRestant.compareTo(BigDecimal.ZERO) <= 0)
                                        break;
                        }
                }

                BigDecimal montantDeduit = retardRestant.multiply(salaireJournalier);

                // Historisation
                RetardDeduction rd = new RetardDeduction();
                rd.setPersonnel(personnel);
                rd.setDatePointage(datePointage);
                rd.setMinutesRetard(retardMinutes);
                rd.setJoursCouverts(joursCouverts);
                rd.setMontantDeduit(montantDeduit);
                retardDeductionRepository.save(rd);

                return montantDeduit;
        }

        public String generateFormatCompatibleSIRH(Map<String, Object> data) {
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                sb.append("<donnees_presence>\n");
                sb.append("  <personnel_id>").append(data.get("personnelId")).append("</personnel_id>\n");
                sb.append("  <periode>\n");
                sb.append("    <mois>").append(data.get("mois")).append("</mois>\n");
                sb.append("    <annee>").append(data.get("annee")).append("</annee>\n");
                sb.append("  </periode>\n");
                sb.append("  <jours_presents>").append(data.get("joursPresents")).append("</jours_presents>\n");
                sb.append("  <jours_absences_deduites>").append(data.get("joursAbsencesDeduites"))
                                .append("</jours_absences_deduites>\n");
                sb.append("  <heures_supplementaires>").append(data.get("totalHeuresSupplementaires"))
                                .append("</heures_supplementaires>\n");
                sb.append("  <montant_heures_supplementaires>").append(data.get("totalMontantHs"))
                                .append("</montant_heures_supplementaires>\n");
                sb.append("  <montant_absences_deduites>").append(data.get("totalMontantAbsencesDeduites"))
                                .append("</montant_absences_deduites>\n");
                sb.append("  <montant_retards>").append(data.get("totalMontantRetard")).append("</montant_retards>\n");
                sb.append("</donnees_presence>");
                return sb.toString();
        }
}
