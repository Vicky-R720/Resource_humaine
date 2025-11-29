package com.itu.gest_emp.model;

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
@Table(name = "anomalies_detection_rh")
public class AnomalieDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @Column(nullable = false)
    private String typeAnomalie;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String donneesDetectees;

    private String severite = "medium"; // low, medium, high, critical

    private String statut = "detecte"; // detecte, investigate, resolu, faux_positif

    @ManyToOne
    @JoinColumn(name = "investigated_by")
    private Person investigatedBy;

    private LocalDateTime investigationDate;

    @Column(columnDefinition = "TEXT")
    private String resolutionComment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
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

    public String getTypeAnomalie() {
        return typeAnomalie;
    }

    public void setTypeAnomalie(String typeAnomalie) {
        this.typeAnomalie = typeAnomalie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDonneesDetectees() {
        return donneesDetectees;
    }

    public void setDonneesDetectees(String donneesDetectees) {
        this.donneesDetectees = donneesDetectees;
    }

    public String getSeverite() {
        return severite;
    }

    public void setSeverite(String severite) {
        this.severite = severite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Person getInvestigatedBy() {
        return investigatedBy;
    }

    public void setInvestigatedBy(Person investigatedBy) {
        this.investigatedBy = investigatedBy;
    }

    public LocalDateTime getInvestigationDate() {
        return investigationDate;
    }

    public void setInvestigationDate(LocalDateTime investigationDate) {
        this.investigationDate = investigationDate;
    }

    public String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
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
}