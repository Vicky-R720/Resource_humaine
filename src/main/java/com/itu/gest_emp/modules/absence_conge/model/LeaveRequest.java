package com.itu.gest_emp.modules.absence_conge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Utilisateur;

@Entity
@Table(name = "leave_requests_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(name = "date_soumission")
    private LocalDate dateSoumission = LocalDate.now();

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "nombre_jours", nullable = false, precision = 5, scale = 2)
    private BigDecimal nombreJours;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @Column(name = "justificatif_path", length = 500)
    private String justificatifPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private LeaveStatus statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Utilisateur validatedBy;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "validation_comment", columnDefinition = "TEXT")
    private String validationComment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "date_retour_prevue")
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;

    @Column(name = "retour_retard_jours")
    private Integer retourRetardJours = 0;

    @Column(name = "justification_retard", length = 500)
    private String justificationRetard;

    @Column(name = "is_absence_non_autorisee")
    private Boolean isAbsenceNonAutorisee = false;

    @Column(name = "generated_pv")
    private Boolean generatedPv = false;

    @Column(name = "demande_explanation_sent")
    private Boolean demandeExplanationSent = false;

    @Column(name = "absence_repetee_count")
    private Integer absenceRepeteeCount = 0;

    @Column(name = "is_conge_paye")
    private Boolean isCongePaye = true;

    @Column(name = "is_deducted")
    private Boolean isDeducted = false;

    @Column(name = "taux_journalier", precision = 10, scale = 2)
    private BigDecimal tauxJournalier;

    // Méthodes pour gérer les spécificités
    public void calculerTauxJournalier(BigDecimal salaireMensuel) {
        if (salaireMensuel != null) {
            this.tauxJournalier = salaireMensuel.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        }
    }

    public void enregistrerRetourEffectif(LocalDate dateRetour, String justification) {
        this.dateRetourEffective = dateRetour;
        this.justificationRetard = justification;

        if (dateRetourPrevue != null && dateRetour.isAfter(dateRetourPrevue)) {
            this.retourRetardJours = (int) ChronoUnit.DAYS.between(dateRetourPrevue, dateRetour);
        }
    }

    public boolean isRetourEnRetard() {
        return dateRetourEffective != null &&
                dateRetourPrevue != null &&
                dateRetourEffective.isAfter(dateRetourPrevue);
    }

    public boolean isDeducted() {
        return this.isDeducted != null && this.isDeducted;
    }

    public void setDeducted(boolean deducted) {
        this.isDeducted = deducted;
    }

    public enum LeaveStatus {
        EN_ATTENTE_MANAGER,
        EN_ATTENTE_RH,
        APPROUVE,
        REFUSE,
        ABSENCE_NON_AUTORISEE, // Nouveau statut
        EN_RETARD, EN_ATTENTE_MANAGER_SOLDE_INSUFFISANT
    }

    // Constructeurs, getters et setters...
}
