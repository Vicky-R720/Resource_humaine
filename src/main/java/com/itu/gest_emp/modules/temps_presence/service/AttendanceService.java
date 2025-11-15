package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import com.itu.gest_emp.modules.temps_presence.dto.attendance.AttendanceDto;
import com.itu.gest_emp.modules.temps_presence.dto.attendance.PointageRequest;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.repository.PersonnelRhRepository;
import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.repository.NotificationRhRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PersonnelRhRepository personnelRepository;
    private final NotificationRhRepository notificationRhRepository;

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

        // Logique de notification retard intégrée directement
        if ("arrivee".equals(request.getTypePointage()) && attendance.getRetardMinutes() > 0) {
            createRetardNotification(personnel.getPerson(), attendance.getRetardMinutes(), today);
        }

        // Logique de notification dépassement pause
        if ("pause_fin".equals(request.getTypePointage()) && attendance.getDureePauseMinutes() > 60) {
            createPauseDepasseeNotification(
                    personnel.getPerson(),
                    attendance.getDureePauseMinutes(),
                    60,
                    today);
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

        attendance.calculerDurees();

        return attendanceRepository.save(attendance);
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

    /**
     * Crée une notification de retard directement dans le service
     */
    private void createRetardNotification(Person person,
            Integer retardMinutes, LocalDate date) {
        try {
            String title = "Retard enregistré";
            String message = String.format(
                    "Vous avez été enregistré(e) en retard le %s. Durée du retard: %d minutes.",
                    date.toString(), retardMinutes);

            NotificationRh notification = new NotificationRh(
                    person, title, message, "attendance_retard");
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));

            notificationRhRepository.save(notification);
            log.info("Notification retard créée pour {}: {} minutes",
                    person.getNom(), retardMinutes);

        } catch (Exception e) {
            log.error("Erreur création notification retard: {}", e.getMessage());
            // Ne pas propager l'exception pour ne pas interrompre le pointage
        }
    }

    /**
     * Crée une notification pour dépassement du temps de pause
     */
    private void createPauseDepasseeNotification(Person person,
            Integer dureeReelle, Integer dureeAutorisee, LocalDate date) {
        try {
            String title = "Dépassement du temps de pause";
            String message = String.format(
                    "Le temps de pause du %s a été dépassé. Durée: %d minutes (autorisé: %d minutes)",
                    date.toString(), dureeReelle, dureeAutorisee);

            NotificationRh notification = new NotificationRh(
                    person, title, message, "attendance_pause_exceeded");
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));

            notificationRhRepository.save(notification);
            log.info("Notification pause dépassée créée pour {}: {} minutes",
                    person.getNom(), dureeReelle);

        } catch (Exception e) {
            log.error("Erreur création notification pause: {}", e.getMessage());
        }
    }

    /**
     * Notification pour pointage oublié (appelé par un job planifié)
     */
    @Transactional
    public void checkPointagesManquants() {
        LocalDate today = LocalDate.now();
        List<PersonnelRh> personnels = personnelRepository.findByStatut("actif");

        for (PersonnelRh personnel : personnels) {
            Optional<AttendanceRh> pointage = attendanceRepository.findByPersonnel_IdAndDatePointage(
                    personnel.getId(), today);

            if (pointage.isEmpty() || pointage.get().getHeureArrivee() == null) {
                createPointageOublieNotification(personnel.getPerson(), today);
            }
        }
    }

    /**
     * Crée une notification pour pointage oublié
     */
    private void createPointageOublieNotification(Person person, LocalDate date) {
        try {
            String title = "Pointage manquant";
            String message = String.format(
                    "Aucun pointage n'a été enregistré pour le %s. Veuillez contacter votre responsable.",
                    date.toString());

            NotificationRh notification = new NotificationRh(
                    person, title, message, "attendance_missing");
            notification.setExpiresAt(LocalDateTime.now().plusDays(3));

            notificationRhRepository.save(notification);
            log.info("Notification pointage manquant créée pour {}", person.getNom());

        } catch (Exception e) {
            log.error("Erreur création notification pointage manquant: {}", e.getMessage());
        }
    }

    /**
     * Notification pour absence non justifiée
     */
    @Transactional
    public void createAbsenceNonJustifieeNotification(Long personnelId, LocalDate date) {
        try {
            PersonnelRh personnel = personnelRepository.findById(personnelId)
                    .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

            String title = "Absence non justifiée";
            String message = String.format(
                    "Une absence non justifiée a été enregistrée pour le %s.",
                    date.toString());

            NotificationRh notification = new NotificationRh(
                    personnel.getPerson(), title, message, "attendance_unjustified_absence");
            notification.setExpiresAt(LocalDateTime.now().plusDays(7));

            notificationRhRepository.save(notification);
            log.info("Notification absence non justifiée créée pour {}", personnel.getPerson().getNom());

        } catch (Exception e) {
            log.error("Erreur création notification absence: {}", e.getMessage());
        }
    }

    /**
     * Notification pour le manager - retard d'un employé
     */
    @Transactional
    public void createRetardManagerNotification(Long managerPersonId, Long employePersonId,
            Integer retardMinutes, LocalDate date) {
        try {
            com.itu.gest_emp.modules.shared.model.Person manager = personnelRepository.findById(managerPersonId)
                    .map(PersonnelRh::getPerson)
                    .orElseThrow(() -> new RuntimeException("Manager non trouvé"));

            com.itu.gest_emp.modules.shared.model.Person employe = personnelRepository.findById(employePersonId)
                    .map(PersonnelRh::getPerson)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

            String title = "Retard d'un employé";
            String message = String.format(
                    "%s %s est arrivé(e) en retard le %s. Durée du retard: %d minutes.",
                    employe.getPrenom(), employe.getNom(),
                    date.toString(), retardMinutes);

            NotificationRh notification = new NotificationRh(
                    manager, title, message, "attendance_retard_manager");

            notificationRhRepository.save(notification);
            log.info("Notification retard manager créée pour {}", manager.getNom());

        } catch (Exception e) {
            log.error("Erreur création notification retard manager: {}", e.getMessage());
        }
    }

}