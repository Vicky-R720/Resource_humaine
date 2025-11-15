package com.itu.gest_emp.modules.shared.repository;

import com.itu.gest_emp.modules.shared.model.NotificationRh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRhRepository extends JpaRepository<NotificationRh, Long> {

    List<NotificationRh> findByRecipient_IdOrderByCreatedAtDesc(Long personId);

    List<NotificationRh> findByRecipient_IdAndIsReadFalseOrderByCreatedAtDesc(Long personId);

    List<NotificationRh> findByRecipient_IdAndTypeOrderByCreatedAtDesc(Long personId, String type);

    @Query("SELECT n FROM NotificationRh n WHERE n.recipient.id = :personId AND n.isRead = false AND (n.expiresAt IS NULL OR n.expiresAt > :now)")
    List<NotificationRh> findActiveUnreadByPersonId(@Param("personId") Long personId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE NotificationRh n SET n.isRead = true WHERE n.recipient.id = :personId AND n.isRead = false")
    int markAllAsReadByPersonId(@Param("personId") Long personId);

    @Modifying
    @Query("DELETE FROM NotificationRh n WHERE n.expiresAt < :now")
    int deleteExpiredNotifications(@Param("now") LocalDateTime now);

    long countByRecipient_IdAndIsReadFalse(Long personId);
}