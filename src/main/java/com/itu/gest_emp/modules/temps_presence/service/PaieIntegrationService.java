package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;

import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.temps_presence.repository.OvertimeRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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

        public Map<String, Object> exportDataForPaie(Long personnelId, int month, int year) {
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                List<AttendanceRh> attendances = attendanceRepository.findByPersonnel_IdAndDatePointageBetween(
                                personnelId, startDate, endDate);
                List<OvertimeRh> overtimes = overtimeRepository.findByPersonnel_IdAndDateHsBetween(
                                personnelId, startDate, endDate);

                // Calcul des données pour la paie
                long joursPresents = attendances.stream()
                                .filter(a -> "present".equals(a.getStatut()) || "retard".equals(a.getStatut()))
                                .count();

                long joursAbsences = attendances.stream()
                                .filter(a -> "absent".equals(a.getStatut()))
                                .count();

                int totalRetardsMinutes = attendances.stream()
                                .mapToInt(a -> a.getRetardMinutes() != null ? a.getRetardMinutes() : 0)
                                .sum();

                BigDecimal totalHeuresSupplementaires = overtimes.stream()
                                .filter(o -> "approuve".equals(o.getStatut()))
                                .map(OvertimeRh::getNombreHeures)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalMontantHs = overtimes.stream()
                                .filter(o -> "approuve".equals(o.getStatut()))
                                .map(OvertimeRh::getMontantHs)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                Map<String, Object> data = new HashMap<>();
                data.put("personnelId", personnelId);
                data.put("mois", month);
                data.put("annee", year);
                data.put("joursPresents", joursPresents);
                data.put("joursAbsences", joursAbsences);
                data.put("totalRetardsMinutes", totalRetardsMinutes);
                data.put("totalHeuresSupplementaires", totalHeuresSupplementaires);
                data.put("totalMontantHs", totalMontantHs);
                data.put("attendances", attendances);
                data.put("overtimes", overtimes);

                return data;
        }

        public String generateFormatCompatibleSIRH(Map<String, Object> data) {
                // Génération format XML/JSON compatible avec SIRH externe
                StringBuilder sb = new StringBuilder();

                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                sb.append("<donnees_presence>\n");
                sb.append("  <personnel_id>").append(data.get("personnelId")).append("</personnel_id>\n");
                sb.append("  <periode>\n");
                sb.append("    <mois>").append(data.get("mois")).append("</mois>\n");
                sb.append("    <annee>").append(data.get("annee")).append("</annee>\n");
                sb.append("  </periode>\n");
                sb.append("  <jours_presents>").append(data.get("joursPresents")).append("</jours_presents>\n");
                sb.append("  <jours_absences>").append(data.get("joursAbsences")).append("</jours_absences>\n");
                sb.append("  <heures_supplementaires>").append(data.get("totalHeuresSupplementaires"))
                                .append("</heures_supplementaires>\n");
                sb.append("  <montant_heures_supplementaires>").append(data.get("totalMontantHs"))
                                .append("</montant_heures_supplementaires>\n");
                sb.append("</donnees_presence>");

                return sb.toString();
        }
}