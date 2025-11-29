package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payslips_rh")
public class PayslipsRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation avec le personnel
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    private Integer mois;
    private Integer annee;

    // Montants
    @Column(name = "salaire_base", precision = 15, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "total_primes", precision = 15, scale = 2)
    private BigDecimal totalPrimes = BigDecimal.ZERO;

    @Column(name = "total_indemnites", precision = 15, scale = 2)
    private BigDecimal totalIndemnites = BigDecimal.ZERO;

    @Column(name = "heures_supplementaires", precision = 15, scale = 2)
    private BigDecimal heuresSupplementaires = BigDecimal.ZERO;

    @Column(name = "absences", precision = 5, scale = 2)
    private BigDecimal absences = BigDecimal.ZERO;

    @Column(name = "total_brut", precision = 15, scale = 2)
    private BigDecimal totalBrut;

    // Cotisations employé
    @Column(name = "cnaps_employee", precision = 15, scale = 2)
    private BigDecimal cnapsEmployee = BigDecimal.ZERO;

    @Column(name = "ostie_employee", precision = 15, scale = 2)
    private BigDecimal ostieEmployee = BigDecimal.ZERO;

    @Column(name = "irsa", precision = 15, scale = 2)
    private BigDecimal irsa = BigDecimal.ZERO;

    @Column(name = "avances", precision = 15, scale = 2)
    private BigDecimal avances = BigDecimal.ZERO;

    @Column(name = "autres_retenues", precision = 15, scale = 2)
    private BigDecimal autresRetenues = BigDecimal.ZERO;

    @Column(name = "total_retenues", precision = 15, scale = 2)
    private BigDecimal totalRetenues;

    @Column(name = "net_a_payer", precision = 15, scale = 2)
    private BigDecimal netAPayer;

    // Cotisations employeur
    @Column(name = "cnaps_employer", precision = 15, scale = 2)
    private BigDecimal cnapsEmployer = BigDecimal.ZERO;

    @Column(name = "ostie_employer", precision = 15, scale = 2)
    private BigDecimal ostieEmployer = BigDecimal.ZERO;

    // Autres champs de calcul
    @Column(name = "montant_imposable", precision = 15, scale = 2)
    private BigDecimal montantImposable = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal indemnitePreavis = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal retenuePreavis = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal valeurDroitConge = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalMontantRetard = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalMontantAbsence = BigDecimal.ZERO;

    @Column(name = "salaire_net", precision = 15, scale = 2)
    private BigDecimal salaireNet = BigDecimal.ZERO;

    @Column(name = "autres_indemnites", precision = 15, scale = 2)
    private BigDecimal autresIndemnites = BigDecimal.ZERO;

    private String statut = "brouillon";

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relation OneToMany vers les lignes
    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayslipLinesRh> lignes = new ArrayList<>();
}
