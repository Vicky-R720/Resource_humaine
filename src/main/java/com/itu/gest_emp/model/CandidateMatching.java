package com.itu.gest_emp.model;

import java.math.BigDecimal;
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
@Table(name = "candidate_matching")
public class CandidateMatching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private CV cv;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

    private BigDecimal scoreCompatibilite = BigDecimal.ZERO;
    private BigDecimal scoreCompetences = BigDecimal.ZERO;
    private BigDecimal scoreExperience = BigDecimal.ZERO;
    private BigDecimal scoreFormation = BigDecimal.ZERO;
    private BigDecimal scoreTotal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String pointsForts;

    @Column(columnDefinition = "TEXT")
    private String pointsFaibles;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    private String statut = "en_attente";

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CV getCv() {
        return cv;
    }

    public void setCv(CV cv) {
        this.cv = cv;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public BigDecimal getScoreCompatibilite() {
        return scoreCompatibilite;
    }

    public void setScoreCompatibilite(BigDecimal scoreCompatibilite) {
        this.scoreCompatibilite = scoreCompatibilite;
    }

    public BigDecimal getScoreCompetences() {
        return scoreCompetences;
    }

    public void setScoreCompetences(BigDecimal scoreCompetences) {
        this.scoreCompetences = scoreCompetences;
    }

    public BigDecimal getScoreExperience() {
        return scoreExperience;
    }

    public void setScoreExperience(BigDecimal scoreExperience) {
        this.scoreExperience = scoreExperience;
    }

    public BigDecimal getScoreFormation() {
        return scoreFormation;
    }

    public void setScoreFormation(BigDecimal scoreFormation) {
        this.scoreFormation = scoreFormation;
    }

    public BigDecimal getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(BigDecimal scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public String getPointsForts() {
        return pointsForts;
    }

    public void setPointsForts(String pointsForts) {
        this.pointsForts = pointsForts;
    }

    public String getPointsFaibles() {
        return pointsFaibles;
    }

    public void setPointsFaibles(String pointsFaibles) {
        this.pointsFaibles = pointsFaibles;
    }

    public String getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(String recommandations) {
        this.recommandations = recommandations;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

   
}
