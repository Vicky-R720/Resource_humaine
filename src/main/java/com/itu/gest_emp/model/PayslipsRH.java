package com.itu.gest_emp.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payslips_rh")
public class PayslipsRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "salaire_base", nullable = false)
    private Double salaireBase;

    @Column(name = "total_primes")
    private Double totalPrimes = 0.0;

    @Column(name = "total_indemnites")
    private Double totalIndemnites = 0.0;

    @Column(name = "heures_supplementaires")
    private Double heuresSupplementaires = 0.0;

    @Column(name = "total_brut", nullable = false)
    private Double totalBrut;

    @Column(name = "cnaps_employee")
    private Double cnapsEmployee = 0.0;

    @Column(name = "ostie_employee")
    private Double ostieEmployee = 0.0;

    private Double irsa = 0.0;

    private Double avances = 0.0;

    @Column(name = "autres_retenues")
    private Double autresRetenues = 0.0;

    @Column(name = "total_retenues", nullable = false)
    private Double totalRetenues;

    @Column(name = "net_a_payer", nullable = false)
    private Double netAPayer;

    @Column(name = "cnaps_employer")
    private Double cnapsEmployer = 0.0;

    @Column(name = "ostie_employer")
    private Double ostieEmployer = 0.0;

    private String statut = "brouillon";

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "pdf_path")
    private String pdfPath;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Person createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PersonnelRH getPersonnel() { return personnel; }
    public void setPersonnel(PersonnelRH personnel) { this.personnel = personnel; }
    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }
    public Double getTotalPrimes() { return totalPrimes; }
    public void setTotalPrimes(Double totalPrimes) { this.totalPrimes = totalPrimes; }
    public Double getTotalIndemnites() { return totalIndemnites; }
    public void setTotalIndemnites(Double totalIndemnites) { this.totalIndemnites = totalIndemnites; }
    public Double getHeuresSupplementaires() { return heuresSupplementaires; }
    public void setHeuresSupplementaires(Double heuresSupplementaires) { this.heuresSupplementaires = heuresSupplementaires; }
    public Double getTotalBrut() { return totalBrut; }
    public void setTotalBrut(Double totalBrut) { this.totalBrut = totalBrut; }
    public Double getCnapsEmployee() { return cnapsEmployee; }
    public void setCnapsEmployee(Double cnapsEmployee) { this.cnapsEmployee = cnapsEmployee; }
    public Double getOstieEmployee() { return ostieEmployee; }
    public void setOstieEmployee(Double ostieEmployee) { this.ostieEmployee = ostieEmployee; }
    public Double getIrsa() { return irsa; }
    public void setIrsa(Double irsa) { this.irsa = irsa; }
    public Double getAvances() { return avances; }
    public void setAvances(Double avances) { this.avances = avances; }
    public Double getAutresRetenues() { return autresRetenues; }
    public void setAutresRetenues(Double autresRetenues) { this.autresRetenues = autresRetenues; }
    public Double getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(Double totalRetenues) { this.totalRetenues = totalRetenues; }
    public Double getNetAPayer() { return netAPayer; }
    public void setNetAPayer(Double netAPayer) { this.netAPayer = netAPayer; }
    public Double getCnapsEmployer() { return cnapsEmployer; }
    public void setCnapsEmployer(Double cnapsEmployer) { this.cnapsEmployer = cnapsEmployer; }
    public Double getOstieEmployer() { return ostieEmployer; }
    public void setOstieEmployer(Double ostieEmployer) { this.ostieEmployer = ostieEmployer; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public Person getCreatedBy() { return createdBy; }
    public void setCreatedBy(Person createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
