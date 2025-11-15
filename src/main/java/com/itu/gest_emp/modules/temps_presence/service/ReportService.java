package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;

import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.temps_presence.repository.OvertimeRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AttendanceRepository attendanceRepository;
    private final OvertimeRepository overtimeRepository;

    public RelevePresence generateRelevePresenceMensuel(Long personnelId, int month, int year) {
        List<AttendanceRh> attendances = attendanceRepository.findByPersonnelIdAndMonthYear(personnelId, month, year);
        List<OvertimeRh> overtimes = overtimeRepository.findByPersonnelIdAndMonthYear(personnelId, month, year);

        RelevePresence releve = new RelevePresence();
        releve.setPersonnelId(personnelId);
        releve.setMois(month);
        releve.setAnnee(year);
        releve.setAttendances(attendances);
        releve.setOvertimes(overtimes);

        // Calcul des totaux
        long totalPresents = attendances.stream()
                .filter(a -> "present".equals(a.getStatut()) || "retard".equals(a.getStatut()))
                .count();
        long totalRetards = attendances.stream()
                .filter(a -> "retard".equals(a.getStatut()))
                .count();
        long totalAbsences = attendances.stream()
                .filter(a -> "absent".equals(a.getStatut()))
                .count();

        int totalHeuresTravail = attendances.stream()
                .mapToInt(a -> a.getDureeTravailMinutes() != null ? a.getDureeTravailMinutes() : 0)
                .sum() / 60;

        BigDecimal totalMontantHs = overtimes.stream()
                .filter(o -> "approuve".equals(o.getStatut()))
                .map(OvertimeRh::getMontantHs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        releve.setTotalPresents(totalPresents);
        releve.setTotalRetards(totalRetards);
        releve.setTotalAbsences(totalAbsences);
        releve.setTotalHeuresTravail(totalHeuresTravail);
        releve.setTotalMontantHs(totalMontantHs);

        return releve;
    }

    public Map<String, Object> getStatistiquesService(LocalDate startDate, LocalDate endDate) {
        List<AttendanceRh> allAttendances = attendanceRepository.findByDatePointageBetween(startDate, endDate);

        // Regroupement par service
        Map<Object, Long> presentsParService = allAttendances.stream()
                .filter(a -> a.getPersonnel().getPost() != null && a.getPersonnel().getPost().getService() != null)
                .filter(a -> "present".equals(a.getStatut()) || "retard".equals(a.getStatut()))
                .collect(Collectors.groupingBy(
                        a -> a.getPersonnel().getPost().getService().getId(),
                        Collectors.counting()));

        Map<Object, Long> retardsParService = allAttendances.stream()
                .filter(a -> a.getPersonnel().getPost() != null && a.getPersonnel().getPost().getService() != null)
                .filter(a -> "retard".equals(a.getStatut()))
                .collect(Collectors.groupingBy(
                        a -> a.getPersonnel().getPost().getService().getId(),
                        Collectors.counting()));

        return Map.of(
                "presentsParService", presentsParService,
                "retardsParService", retardsParService,
                "periode", startDate + " - " + endDate);
    }

    // Classe pour le relevé de présence
    public static class RelevePresence {
        private Long personnelId;
        private int mois;
        private int annee;
        private List<AttendanceRh> attendances;
        private List<OvertimeRh> overtimes;
        private Long totalPresents;
        private Long totalRetards;
        private Long totalAbsences;
        private Integer totalHeuresTravail;
        private BigDecimal totalMontantHs;

        public Long getPersonnelId() {
            return personnelId;
        }

        public void setPersonnelId(Long personnelId) {
            this.personnelId = personnelId;
        }

        public int getMois() {
            return mois;
        }

        public void setMois(int mois) {
            this.mois = mois;
        }

        public int getAnnee() {
            return annee;
        }

        public void setAnnee(int annee) {
            this.annee = annee;
        }

        public List<AttendanceRh> getAttendances() {
            return attendances;
        }

        public void setAttendances(List<AttendanceRh> attendances) {
            this.attendances = attendances;
        }

        public List<OvertimeRh> getOvertimes() {
            return overtimes;
        }

        public void setOvertimes(List<OvertimeRh> overtimes) {
            this.overtimes = overtimes;
        }

        public Long getTotalPresents() {
            return totalPresents;
        }

        public void setTotalPresents(Long totalPresents) {
            this.totalPresents = totalPresents;
        }

        public Long getTotalRetards() {
            return totalRetards;
        }

        public void setTotalRetards(Long totalRetards) {
            this.totalRetards = totalRetards;
        }

        public Long getTotalAbsences() {
            return totalAbsences;
        }

        public void setTotalAbsences(Long totalAbsences) {
            this.totalAbsences = totalAbsences;
        }

        public Integer getTotalHeuresTravail() {
            return totalHeuresTravail;
        }

        public void setTotalHeuresTravail(Integer totalHeuresTravail) {
            this.totalHeuresTravail = totalHeuresTravail;
        }

        public BigDecimal getTotalMontantHs() {
            return totalMontantHs;
        }

        public void setTotalMontantHs(BigDecimal totalMontantHs) {
            this.totalMontantHs = totalMontantHs;
        }

        // Getters et Setters
    }
}