package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ostie")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ostie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "taux_employeur", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxEmployeur;

    @Column(name = "taux_employe", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxEmploye;

    @Column(name = "date_debut_validite", nullable = false)
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    @Column(name = "reference_legale", columnDefinition = "TEXT")
    private String referenceLegale;

    @Column(name = "actif")
    private Boolean actif = true;

    // ---------------------------
    // ATTRIBUT NON PERSISTÉ
    // ---------------------------
    @Transient
    private BigDecimal salaireBrut;

    // =====================================================
    //          LOGIQUE MÉTIER (Rich Domain Model)
    // =====================================================

    public boolean estActif() {
        return Boolean.TRUE.equals(actif);
    }

    public boolean estValideAujourdhui() {
        return estValideADate(LocalDate.now());
    }

    public boolean estValideADate(LocalDate date) {
        if (!estActif()) return false;
        if (dateDebutValidite != null && date.isBefore(dateDebutValidite)) return false;
        if (dateFinValidite != null && date.isAfter(dateFinValidite)) return false;
        return true;
    }

    // Cotisation OSTIE employé = salaireBrut * tauxEmploye / 100
    public BigDecimal cotisationEmploye() {
        if (salaireBrut == null) throw new IllegalStateException("salaireBrut non défini !");
        return pourcentage(salaireBrut, tauxEmploye);
    }

    // Cotisation OSTIE employeur = salaireBrut * tauxEmployeur / 100
    public BigDecimal cotisationEmployeur() {
        if (salaireBrut == null) throw new IllegalStateException("salaireBrut non défini !");
        return pourcentage(salaireBrut, tauxEmployeur);
    }

    // Total cotisation = employé + employeur
    public BigDecimal cotisationTotale() {
        return cotisationEmploye().add(cotisationEmployeur());
    }

    // Méthode utilitaire interne pour calculer le pourcentage
    private BigDecimal pourcentage(BigDecimal base, BigDecimal taux) {
        return base
                .multiply(taux)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }
}
