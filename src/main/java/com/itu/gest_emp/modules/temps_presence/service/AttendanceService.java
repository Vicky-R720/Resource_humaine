package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import com.itu.gest_emp.modules.temps_presence.dto.attendance.AttendanceDto;
import com.itu.gest_emp.modules.temps_presence.dto.attendance.PointageRequest;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.absence_conge.service.AbsencePVService;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.repository.PersonnelRhRepository;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PersonnelRhRepository personnelRepository;
    private final AbsencePVService absencePVService;
    private final UtilisateurService utilisateurService;

    @Transactional
    public AttendanceRh pointage(PointageRequest request) {
        LocalDate today = LocalDate.now();
        PersonnelRh personnel = personnelRepository.findById(request.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        AttendanceRh attendance = attendanceRepository.findByPersonnel_IdAndDatePointage(
                request.getPersonnelId(), today)
                .orElse(new AttendanceRh());

        if (attendance.getId() == null) {
            attendance.setPersonnel(personnel);
            attendance.setDatePointage(today);
            attendance.setTypePointage("mobile");
        }

        switch (request.getTypePointage()) {
            case "arrivee":
                attendance.setHeureArrivee(request.getHeure());
                break;
            case "depart":
                attendance.setHeureDepart(request.getHeure());
                break;
            case "pause_debut":
                attendance.setHeurePauseDebut(request.getHeure());
                break;
            case "pause_fin":
                attendance.setHeurePauseFin(request.getHeure());
                break;
        }

        if (request.getCommentaire() != null) {
            attendance.setCommentaire(request.getCommentaire());
        }

        attendance.calculerDurees();
        Utilisateur u = utilisateurService.findByPersonId(personnel.getPerson().getId());
        // Logique de notification retard intégrée directement
        if ("arrivee".equals(request.getTypePointage()) && attendance.getRetardMinutes() > 0) {

        }

        // Logique de notification dépassement pause
        if ("pause_fin".equals(request.getTypePointage()) && attendance.getDureePauseMinutes() > 60) {

        }

        return attendanceRepository.save(attendance);
    }

    // === AUTRES METHODES EXISTANTES ===

    public List<AttendanceRh> getAttendanceByPersonnel(Long personnelId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByPersonnel_IdAndDatePointageBetween(personnelId, startDate, endDate);
    }

    @Transactional
    public AttendanceRh createAttendance(AttendanceDto dto) {
        PersonnelRh personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        Optional<AttendanceRh> existing = attendanceRepository.findByPersonnel_IdAndDatePointage(
                dto.getPersonnelId(), dto.getDatePointage());

        if (existing.isPresent()) {
            throw new RuntimeException("Pointage déjà existant pour cette date");
        }

        AttendanceRh attendance = new AttendanceRh();
        attendance.setPersonnel(personnel);
        attendance.setDatePointage(dto.getDatePointage());
        attendance.setHeureArrivee(dto.getHeureArrivee());
        attendance.setHeureDepart(dto.getHeureDepart());
        attendance.setHeurePauseDebut(dto.getHeurePauseDebut());
        attendance.setHeurePauseFin(dto.getHeurePauseFin());
        attendance.setTypePointage(dto.getTypePointage());
        attendance.setCommentaire(dto.getCommentaire());
        attendance.setStatut(dto.getStatut());

        attendance.calculerDurees();
        

        AttendanceRh saved = attendanceRepository.save(attendance);
        return saved;
    }

    @Transactional
    public void importBadgeuseCSV(MultipartFile file) {
        try {
            log.info("Import badgeuse CSV: {}", file.getOriginalFilename());
            // Implémentation de l'import CSV...

        } catch (Exception e) {
            log.error("Erreur lors de l'import CSV", e);
            throw new RuntimeException("Erreur import CSV: " + e.getMessage());
        }
    }

    public List<AttendanceRh> getRetards(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findRetardsBetweenDates(startDate, endDate);
    }

    public StatistiquesPresence getStatistiquesPresence(Long personnelId, LocalDate startDate, LocalDate endDate) {
        Long presents = attendanceRepository.countByPersonnel_IdAndStatutAndDatePointageBetween(
                personnelId, "present", startDate, endDate);
        Long retards = attendanceRepository.countByPersonnel_IdAndStatutAndDatePointageBetween(
                personnelId, "retard", startDate, endDate);
        Long absences = attendanceRepository.countByPersonnel_IdAndStatutAndDatePointageBetween(
                personnelId, "absent", startDate, endDate);

        return new StatistiquesPresence(presents, retards, absences);
    }

    // Classe interne pour les statistiques
    public static class StatistiquesPresence {
        public Long presents;
        public Long retards;
        public Long absences;

        public StatistiquesPresence(Long presents, Long retards, Long absences) {
            this.presents = presents;
            this.retards = retards;
            this.absences = absences;
        }
    }

    public List<AttendanceRh> getAllAttendanceBetweenDates(LocalDate start, LocalDate end) {
        return attendanceRepository.findByDatePointageBetween(start, end);
    }

    // === METHODES DE NOTIFICATION INTEGREES ===

}