package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_parameters_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryParametersRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_parametre", nullable = false, unique = true)
    private String nomParametre;

    private String description;

    @Column(name = "valeur", precision = 10, scale = 4)
    private BigDecimal valeur;

    private String type; // pourcentage, montant_fixe
    private String categorie; // cnaps, ostie, irsa, prime, indemnite

    @Column(name = "date_debut_validite")
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "seuil_min", precision = 15, scale = 2)
    private BigDecimal seuilMin = BigDecimal.ZERO;

    @Column(name = "seuil_max", precision = 15, scale = 2)
    private BigDecimal seuilMax; // peut être NULL

    // Getters and Setters
}