package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "contracts_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractsRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id", nullable = false)
    private PersonnelRh personnel;

    @ManyToOne
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column
    private LocalDate dateFin; // null pour CDI, obligatoire pour CDD/STAGE/etc.

    @Column
    @Builder.Default
    private Integer dureeEssaiMois = 0;

    @Column
    private LocalDate dateFinEssai;

    @Column
    private LocalDate dateValidationEssai;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEssaiValide = false;

    @Column(precision = 15, scale = 2)
    private BigDecimal salaireBase;

    @Column(length = 50, nullable = false)
    @Builder.Default
    private String statut = "actif"; // actif, termine, suspendu, resilie

    @Column(columnDefinition = "TEXT")
    private String motifFin;

    @Column(length = 500)
    private String documentPath;

    @Column
    private Integer dureePreavis;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal indemnitePreavis = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal retenuePreavis = BigDecimal.ZERO;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validateContract();
        calculateTrialPeriodEnd();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        validateContract();
    }

    /**
     * Valide la cohérence du contrat selon les règles du type de contrat
     */
    private void validateContract() {
        if (contractType == null) {
            throw new IllegalStateException("Le type de contrat est obligatoire");
        }

        // Validation de la date de fin selon le type de contrat
        if (contractType.getRequiresEndDate() && dateFin == null) {
            throw new IllegalStateException(
                    "La date de fin est obligatoire pour un contrat de type " + contractType.getLibelle());
        }

        if (!contractType.getRequiresEndDate() && dateFin != null) {
            throw new IllegalStateException(
                    "Un " + contractType.getLibelle() + " ne doit pas avoir de date de fin");
        }

        // Validation de la durée maximale pour le CDD
        if ("CDD".equalsIgnoreCase(contractType.getCode()) && dateFin != null) {
            long moisTotal = ChronoUnit.MONTHS.between(dateDebut, dateFin);
            System.out.println(moisTotal);
            if (moisTotal > 24) {
                throw new IllegalStateException("La durée totale d'un CDD ne peut dépasser 24 mois");
            }
        }

        // Validation de la période d'essai
        if (dureeEssaiMois != null && dureeEssaiMois > 0) {
            if (!contractType.getHasTrialPeriod()) {
                throw new IllegalStateException(
                        "Le type de contrat " + contractType.getLibelle() + " n'autorise pas de période d'essai");
            }

            if (contractType.getDureeEssaiMaxMois() != null &&
                    dureeEssaiMois > contractType.getDureeEssaiMaxMois()) {
                throw new IllegalStateException(
                        String.format("La durée d'essai maximale pour un %s est de %d mois",
                                contractType.getLibelle(),
                                contractType.getDureeEssaiMaxMois()));
            }
        }

        // Validation des dates
        if (dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin ne peut pas être antérieure à la date de début");
        }

        if (dateFinEssai != null && dateFinEssai.isBefore(dateDebut)) {
            throw new IllegalStateException("La date de fin d'essai ne peut pas être antérieure à la date de début");
        }
    }

    /**
     * Calcule automatiquement la date de fin de la période d'essai
     */
    private void calculateTrialPeriodEnd() {
        if (dureeEssaiMois != null && dureeEssaiMois > 0 && dateFinEssai == null) {
            dateFinEssai = dateDebut.plusMonths(dureeEssaiMois);
        }
    }

    /**
     * Vérifie si le contrat est actuellement actif
     */
    @Transient
    public boolean isActive() {
        return "actif".equalsIgnoreCase(statut);
    }

    /**
     * Vérifie si le contrat est en période d'essai
     */
    @Transient
    public boolean isInTrialPeriod() {
        if (dateFinEssai == null || isEssaiValide) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isAfter(dateFinEssai) && !isEssaiValide;
    }

    /**
     * Vérifie si le contrat est expiré
     */
    @Transient
    public boolean isExpired() {
        if (dateFin == null) {
            return false; // CDI ne peut pas expirer
        }
        return LocalDate.now().isAfter(dateFin);
    }

    /**
     * Retourne la durée du contrat en mois
     */
    @Transient
    public Long getDureeEnMois() {
        if (dateFin == null) {
            return null; // CDI = durée indéterminée
        }
        return ChronoUnit.MONTHS.between(dateDebut, dateFin);
    }

    /**
     * Renouvelle un CDD avant son expiration
     */

}
