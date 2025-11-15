package com.itu.gest_emp.modules.temps_presence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Person;
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

    @Column(name = "type_hs", length = 50)
    private String typeHs; // jour_normal, dimanche, jours_ferie, nuit

    @Column(name = "taux_majoration", precision = 5, scale = 2)
    private BigDecimal tauxMajoration = BigDecimal.ONE;

    @Column(name = "montant_hs", precision = 15, scale = 2)
    private BigDecimal montantHs;

    @Column(name = "statut", length = 50)
    private String statut = "en_attente"; // en_attente, approuve, refuse, paye

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Person validatedBy;

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

    public void calculerMontantHs() {
        if (salaireHoraireBase != null && nombreHeures != null && tauxMajoration != null) {
            BigDecimal salaireMajoré = salaireHoraireBase.multiply(tauxMajoration);
            this.montantHs = salaireMajoré.multiply(nombreHeures);
        }
    }

    public void setTauxMajorationFromType() {
        switch (typeHs) {
            case "jour_normal":
                this.tauxMajoration = new BigDecimal("1.3");
                break;
            case "dimanche":
                this.tauxMajoration = new BigDecimal("1.5");
                break;
            case "jours_ferie":
                this.tauxMajoration = new BigDecimal("2.0");
                break;
            case "nuit":
                this.tauxMajoration = new BigDecimal("1.5");
                break;
            default:
                this.tauxMajoration = BigDecimal.ONE;
        }
        calculerMontantHs();
    }
}