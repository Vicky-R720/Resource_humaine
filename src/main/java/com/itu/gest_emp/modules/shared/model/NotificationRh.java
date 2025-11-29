package com.itu.gest_emp.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "notification_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NotificationRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Destinataire de la notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Utilisateur recipient;

    // Expéditeur / source de la notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Utilisateur sender;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "type", length = 50)
    private String type; // ex: leave_request, leave_approval, alert, etc.

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "related_entity_type", length = 100)
    private String relatedEntityType; // LeaveRequest, LeaveBalance, etc.

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Marquer comme lu
    public void markAsRead() {
        this.isRead = true;
        this.updatedAt = LocalDateTime.now();
    }

    // Vérifier si expirée
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // Constructeur pratique
    public NotificationRh(Utilisateur recipient, Utilisateur sender, String title, String message, String type, String relatedEntityType, Long relatedEntityId) {
        this.recipient = recipient;
        this.sender = sender;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
