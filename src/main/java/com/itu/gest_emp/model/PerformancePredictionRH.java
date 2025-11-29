package com.itu.gest_emp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "performance_predictions_rh")
public class PerformancePredictionRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;
    
    @Column(nullable = false)
    private String typePrediction;
    
    private BigDecimal scorePrediction;
    private BigDecimal confidenceLevel;
    
    @Column(columnDefinition = "TEXT")
    private String facteursCles;
    
    @Column(columnDefinition = "TEXT")
    private String recommandations;
    
    @Column(nullable = false)
    private LocalDate datePrediction;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    public PerformancePredictionRH() {
    }

    public PerformancePredictionRH(PersonnelRH personnel, String typePrediction, BigDecimal scorePrediction,
            BigDecimal confidenceLevel, String facteursCles, String recommandations, LocalDate datePrediction,
            LocalDateTime createdAt) {
        this.personnel = personnel;
        this.typePrediction = typePrediction;
        this.scorePrediction = scorePrediction;
        this.confidenceLevel = confidenceLevel;
        this.facteursCles = facteursCles;
        this.recommandations = recommandations;
        this.datePrediction = datePrediction;
        this.createdAt = createdAt;
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

    public String getTypePrediction() {
        return typePrediction;
    }

    public void setTypePrediction(String typePrediction) {
        this.typePrediction = typePrediction;
    }

    public BigDecimal getScorePrediction() {
        return scorePrediction;
    }

    public void setScorePrediction(BigDecimal scorePrediction) {
        this.scorePrediction = scorePrediction;
    }

    public BigDecimal getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(BigDecimal confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getFacteursCles() {
        return facteursCles;
    }

    public void setFacteursCles(String facteursCles) {
        this.facteursCles = facteursCles;
    }

    public String getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(String recommandations) {
        this.recommandations = recommandations;
    }

    public LocalDate getDatePrediction() {
        return datePrediction;
    }

    public void setDatePrediction(LocalDate datePrediction) {
        this.datePrediction = datePrediction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
}
