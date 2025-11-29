package com.itu.gest_emp.modules.temps_presence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "overtime_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OvertimeRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private PersonnelRh personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private AttendanceRh attendance;

    @Column(name = "date_hs", nullable = false)
    private LocalDate dateHs;

    @Column(name = "nombre_heures", nullable = false, precision = 5, scale = 2)
    private BigDecimal nombreHeures;

    @ManyToOne
    @JoinColumn(name = "type_hs_id")
    private TypeHs typeHs;

    @Column(name = "montant_hs", precision = 15, scale = 2)
    private BigDecimal montantHs;

    @Column(name = "statut", length = 50)
    private String statut = "en_attente"; // en_attente, approuve, refuse, paye

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Utilisateur validatedBy;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "salaire_horaire_base", precision = 10, scale = 2)
    private BigDecimal salaireHoraireBase;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculerMontantHs();
    }

    @PreUpdate
    protected void onUpdate() {
        calculerMontantHs();
    }

    /**
     * Calcule le montant des heures supplémentaires en utilisant
     * le salaire horaire de base et le taux de majoration du type d'HS
     */
    public void calculerMontantHs() {
        
    }

}
