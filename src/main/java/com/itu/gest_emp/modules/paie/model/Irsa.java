package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "irsa", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "numero_tranche", "date_debut_validite" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Irsa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_tranche", nullable = false)
    private Integer numeroTranche;

    /**
     * Les seuils sont des montants en Ariary (pas décimaux)
     * Exemple : 350000, 4000000
     */
    @Column(name = "seuil_min", nullable = false, precision = 15, scale = 0)
    private BigDecimal seuilMin;

    @Column(name = "seuil_max", precision = 15, scale = 0)
    private BigDecimal seuilMax;

    /**
     * Limite = montant maximum imposable dans la tranche
     * Exemple : 100000
     */
    @Column(name = "limite", precision = 15, scale = 0)
    private BigDecimal limite;

    /**
     * Taux d’imposition en pourcentage (e.g 5.00, 10.00, 20.00)
     */
    @Column(name = "taux", nullable = false, precision = 5, scale = 2)
    private BigDecimal taux;

    @Column(name = "date_debut_validite", nullable = false)
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    @Column(name = "reference_legale", columnDefinition = "TEXT")
    private String referenceLegale;

    @Column(name = "actif")
    private Boolean actif = true;

    public boolean isActif() {
        return actif != null && actif;
    }

    public boolean isValideAujourdhui() {
        LocalDate today = LocalDate.now();
        return isActif()
                && !dateDebutValidite.isAfter(today)
                && (dateFinValidite == null || !dateFinValidite.isBefore(today));
    }

    public boolean isValideADate(LocalDate date) {
        return isActif()
                && !dateDebutValidite.isAfter(date)
                && (dateFinValidite == null || !dateFinValidite.isBefore(date));
    }

    public boolean estDansLaTranche(BigDecimal montant) {
        if (montant == null) return false;

        boolean supMin = montant.compareTo(seuilMin) >= 0;
        boolean infMax = (seuilMax == null) || montant.compareTo(seuilMax) <= 0;

        return supMin && infMax;
    }

    public BigDecimal calculerImpotTranche(BigDecimal montantImposable) {
        if (!estDansLaTranche(montantImposable)) {
            return BigDecimal.ZERO;
        }

        BigDecimal montantDansLaTranche;

        if (seuilMax == null) {
            montantDansLaTranche = montantImposable.subtract(seuilMin).add(BigDecimal.ONE);
        } else {
            montantDansLaTranche = montantImposable.subtract(seuilMin).add(BigDecimal.ONE);
            BigDecimal maxTranche = seuilMax.subtract(seuilMin).add(BigDecimal.ONE);
            montantDansLaTranche = montantDansLaTranche.min(maxTranche);
        }

        // taux sur 100
        return montantDansLaTranche.multiply(taux).divide(new BigDecimal("100"));
    }
}
