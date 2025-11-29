package com.itu.gest_emp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contracts_rh")
public class ContractRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;
    
    @Column(nullable = false)
    private String typeContrat;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    private Integer dureeEssaiMois = 0;
    private LocalDate dateFinEssai;
    private Boolean isEssaiValide = false;
    private BigDecimal salaireBase;
    private String statut = "actif";
    private String motifFin;
    private String documentPath;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ContractRH(PersonnelRH personnel, String typeContrat, LocalDate dateDebut, LocalDate dateFin,
            Integer dureeEssaiMois, LocalDate dateFinEssai, Boolean isEssaiValide, BigDecimal salaireBase,
            String statut, String motifFin, String documentPath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.personnel = personnel;
        this.typeContrat = typeContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dureeEssaiMois = dureeEssaiMois;
        this.dateFinEssai = dateFinEssai;
        this.isEssaiValide = isEssaiValide;
        this.salaireBase = salaireBase;
        this.statut = statut;
        this.motifFin = motifFin;
        this.documentPath = documentPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonnelRH getPersonnel() {
        return personnel;
    }

    public void setPersonnel(PersonnelRH personnel) {
        this.personnel = personnel;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getDureeEssaiMois() {
        return dureeEssaiMois;
    }

    public void setDureeEssaiMois(Integer dureeEssaiMois) {
        this.dureeEssaiMois = dureeEssaiMois;
    }

    public LocalDate getDateFinEssai() {
        return dateFinEssai;
    }

    public void setDateFinEssai(LocalDate dateFinEssai) {
        this.dateFinEssai = dateFinEssai;
    }

    public Boolean getIsEssaiValide() {
        return isEssaiValide;
    }

    public void setIsEssaiValide(Boolean isEssaiValide) {
        this.isEssaiValide = isEssaiValide;
    }

    public BigDecimal getSalaireBase() {
        return salaireBase;
    }

    public void setSalaireBase(BigDecimal salaireBase) {
        this.salaireBase = salaireBase;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotifFin() {
        return motifFin;
    }

    public void setMotifFin(String motifFin) {
        this.motifFin = motifFin;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
}