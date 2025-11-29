package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "avance_action")
public class AvanceAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avance_id")
    @JsonBackReference
    private Avance avance;

    @Enumerated(EnumType.STRING)
    private AvanceStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private AvanceStatus toStatus;

    @ManyToOne
    @JoinColumn(name = "actor_utilisateur_id")
    private com.itu.gest_emp.modules.shared.model.Utilisateur actor;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private java.time.LocalDateTime actionDate = java.time.LocalDateTime.now();

    public AvanceAction() {}

    public AvanceAction(Avance avance, AvanceStatus fromStatus, AvanceStatus toStatus, com.itu.gest_emp.modules.shared.model.Utilisateur actor, String commentaire) {
        this.avance = avance;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actor = actor;
        this.commentaire = commentaire;
        this.actionDate = java.time.LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Avance getAvance() { return avance; }
    public AvanceStatus getFromStatus() { return fromStatus; }
    public AvanceStatus getToStatus() { return toStatus; }
    public com.itu.gest_emp.modules.shared.model.Utilisateur getActor() { return actor; }
    public String getCommentaire() { return commentaire; }
    public java.time.LocalDateTime getActionDate() { return actionDate; }
    public void setId(Long id) { this.id = id; }
    public void setAvance(Avance avance) { this.avance = avance; }
    public void setFromStatus(AvanceStatus fromStatus) { this.fromStatus = fromStatus; }
    public void setToStatus(AvanceStatus toStatus) { this.toStatus = toStatus; }
    public void setActor(com.itu.gest_emp.modules.shared.model.Utilisateur actor) { this.actor = actor; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public void setActionDate(java.time.LocalDateTime actionDate) { this.actionDate = actionDate; }
}
