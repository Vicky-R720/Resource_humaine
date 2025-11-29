package com.itu.gest_emp.modules.shared.service;

import com.itu.gest_emp.modules.shared.model.NotificationRh;
import com.itu.gest_emp.modules.shared.repository.NotificationRhRepository;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.repository.PersonRepository;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationRhService {

    @Autowired
    private NotificationRhRepository notificationRhRepository;

    @Autowired
    private PersonRepository personRepository;

    public NotificationRh createNotification(NotificationRh notification) {
        return notificationRhRepository.save(notification);
    }

    @Transactional
    public void createNotification(
            Utilisateur recipient,
            Utilisateur sender,
            String title,
            String message,
            String type,
            String relatedEntityType,
            Long relatedEntityId) {

        try {
            NotificationRh notification = new NotificationRh(
                    recipient,
                    sender,
                    title,
                    message,
                    type,
                    relatedEntityType,
                    relatedEntityId);

            notificationRhRepository.save(notification);
            log.info("Notification '{}' de type '{}' créée pour {}", title, type, recipient.getId());

        } catch (Exception e) {
            log.error("Erreur création notification '{}' pour {}: {}", title, recipient.getId(), e.getMessage());
        }
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

}