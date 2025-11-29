package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;

@Entity
@Table(name = "cnaps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cnaps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "secteur_activite", nullable = false)
    private SecteurActiviteEnum secteurActivite = SecteurActiviteEnum.non_agricole;

    @Column(name = "heures_mensuel", nullable = false, precision = 7, scale = 4)
    private BigDecimal heuresMensuel;

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
    private BigDecimal salaireBrut; // utilisé pour les calculs mais ignoré par JPA

    // =====================================================
    // LOGIQUE MÉTIER (Rich Domain Model)
    // =====================================================
    public boolean estValidePour(LocalDate date) {
        if (!Boolean.TRUE.equals(actif))
            return false;
        if (dateDebutValidite != null && date.isBefore(dateDebutValidite))
            return false;
        if (dateFinValidite != null && date.isAfter(dateFinValidite))
            return false;
        return true;
    }

    public BigDecimal cotisationEmploye() {
        if (salaireBrut == null)
            throw new IllegalStateException("salaireBrut non défini !");
        return pourcentage(salaireBrut, tauxEmploye);
    }

    public BigDecimal cotisationEmployeur() {
        if (salaireBrut == null)
            throw new IllegalStateException("salaireBrut non défini !");
        return pourcentage(salaireBrut, tauxEmployeur);
    }

    public BigDecimal cotisationTotale() {
        return cotisationEmploye().add(cotisationEmployeur());
    }

    private BigDecimal pourcentage(BigDecimal base, BigDecimal taux) {
        return base.multiply(taux).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }
}
