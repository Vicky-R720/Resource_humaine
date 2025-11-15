package com.itu.gest_emp.modules.shared.service;

import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.repository.NotificationRhRepository;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.repository.PersonRepository;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationRhService {

    @Autowired
    private NotificationRhRepository notificationRhRepository;

    @Autowired
    private PersonRepository personRepository;

    public NotificationRh createNotification(NotificationRh notification) {
        return notificationRhRepository.save(notification);
    }

    public NotificationRh createNotification(Long personId, String title, String message, String type) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + personId));

        NotificationRh notification = new NotificationRh(person, title, message, type);
        return notificationRhRepository.save(notification);
    }

    public NotificationRh createNotification(Long personId, String title, String message, String type,
            String relatedEntityType, Long relatedEntityId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + personId));

        NotificationRh notification = new NotificationRh(person, title, message, type, relatedEntityType,
                relatedEntityId);
        return notificationRhRepository.save(notification);
    }

    public List<NotificationRh> getUserNotifications(Long personId) {
        return notificationRhRepository.findByRecipient_IdOrderByCreatedAtDesc(personId);
    }

    public List<NotificationRh> getUnreadNotifications(Long personId) {
        return notificationRhRepository.findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(personId);
    }

    public List<NotificationRh> getActiveUnreadNotifications(Long personId) {
        return notificationRhRepository.findActiveUnreadByPersonId(personId, LocalDateTime.now());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRhRepository.findById(notificationId).ifPresent(notification -> {
            notification.markAsRead();
            notificationRhRepository.save(notification);
        });
    }

    @Transactional
    public int markAllAsRead(Long personId) {
        return notificationRhRepository.markAllAsReadByPersonId(personId);
    }

    public long getUnreadCount(Long personId) {
        return notificationRhRepository.countByRecipient_IdAndIsReadFalse(personId);
    }

    @Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h du matin
    @Transactional
    public void cleanupExpiredNotifications() {
        int deletedCount = notificationRhRepository.deleteExpiredNotifications(LocalDateTime.now());
        System.out.println("Notifications expirées supprimées: " + deletedCount);
    }

    // Méthode utilitaire pour les congés
    public void sendLeaveRequestNotification(Long managerPersonId, String employeeName,
            LocalDate startDate, LocalDate endDate, Long leaveRequestId) {
        String title = "Nouvelle demande de congé";
        String message = String.format(
                "L'employé %s a soumis une demande de congé du %s au %s",
                employeeName, startDate, endDate);

        createNotification(managerPersonId, title, message, "leave_request", "LeaveRequest", leaveRequestId);
    }

    public void sendLeaveApprovalNotification(Long employeePersonId, String comment, Long leaveRequestId) {
        String title = "Demande de congé approuvée";
        String message = "Votre demande de congé a été approuvée" +
                (comment != null ? ". Commentaire: " + comment : "");

        createNotification(employeePersonId, title, message, "leave_approval", "LeaveRequest", leaveRequestId);
    }

    public void sendLeaveRejectionNotification(Long employeePersonId, String comment, Long leaveRequestId) {
        String title = "Demande de congé refusée";
        String message = "Votre demande de congé a été refusée" +
                (comment != null ? ". Motif: " + comment : "");

        createNotification(employeePersonId, title, message, "leave_rejection", "LeaveRequest", leaveRequestId);
    }

    public void sendBalanceAlertNotification(Long personId, String leaveTypeName, BigDecimal remainingBalance) {
        String title = "Alerte solde de congé";
        String message = String.format(
                "Votre solde de %s est faible: %s jours restants",
                leaveTypeName, remainingBalance);

        createNotification(personId, title, message, "balance_alert");
    }


}