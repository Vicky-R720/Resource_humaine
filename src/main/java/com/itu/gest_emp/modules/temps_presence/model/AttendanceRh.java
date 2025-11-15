package com.itu.gest_emp.modules.temps_presence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Person;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "attendance_rh", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "personnel_id", "date_pointage" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AttendanceRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private PersonnelRh personnel;

    @Column(name = "date_pointage", nullable = false)
    private LocalDate datePointage;

    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;

    @Column(name = "heure_depart")
    private LocalTime heureDepart;

    @Column(name = "heure_pause_debut")
    private LocalTime heurePauseDebut;

    @Column(name = "heure_pause_fin")
    private LocalTime heurePauseFin;

    @Column(name = "duree_travail_minutes")
    private Integer dureeTravailMinutes;

    @Column(name = "statut", length = 50)
    private String statut = "present"; // present, absent, retard, conge, maladie

    @Column(name = "type_pointage", length = 50)
    private String typePointage = "manuel"; // manuel, badgeuse, mobile

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Person validatedBy;

    @Column(name = "is_validated")
    private Boolean isValidated = false;

    @Column(name = "retard_minutes")
    private Integer retardMinutes = 0;

    @Column(name = "duree_pause_minutes")
    private Integer dureePauseMinutes = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculerDurees();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculerDurees();
    }

    public void calculerDurees() {
        // Calcul durée pause
        if (heurePauseDebut != null && heurePauseFin != null) {
            Duration pauseDuration = Duration.between(heurePauseDebut, heurePauseFin);
            this.dureePauseMinutes = (int) pauseDuration.toMinutes();
        }

        // Calcul durée travail
        if (heureArrivee != null && heureDepart != null) {
            Duration travailDuration = Duration.between(heureArrivee, heureDepart);
            if (dureePauseMinutes > 0) {
                this.dureeTravailMinutes = (int) travailDuration.toMinutes() - dureePauseMinutes;
            } else {
                this.dureeTravailMinutes = (int) travailDuration.toMinutes();
            }
        }

        // Détection retard (supposant heure d'arrivée normale à 8h)
        if (heureArrivee != null) {
            LocalTime heureNormaleArrivee = LocalTime.of(8, 0);
            if (heureArrivee.isAfter(heureNormaleArrivee)) {
                Duration retardDuration = Duration.between(heureNormaleArrivee, heureArrivee);
                this.retardMinutes = (int) retardDuration.toMinutes();
                if (this.statut.equals("present")) {
                    this.statut = "retard";
                }
            }
        }
    }
}