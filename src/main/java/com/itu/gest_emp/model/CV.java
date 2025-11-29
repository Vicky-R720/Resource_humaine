package com.itu.gest_emp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cv")
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private String posteRecherche;
    private Integer experienceAnnees = 0;
    private String niveauEtude;
    private String diplomePrincipal;

    @Column(columnDefinition = "TEXT")
    private String competencesCles;

    private String langues;
    private BigDecimal pretentionsSalariales;
    private String disponibilite;
    private String localisation;
    private String telephone;
    private String email;
    private String statut = "actif";
    private Integer scoreCompetences = 0;
    private BigDecimal noteGlobale = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPosteRecherche() {
        return posteRecherche;
    }

    public void setPosteRecherche(String posteRecherche) {
        this.posteRecherche = posteRecherche;
    }

    public Integer getExperienceAnnees() {
        return experienceAnnees;
    }

    public void setExperienceAnnees(Integer experienceAnnees) {
        this.experienceAnnees = experienceAnnees;
    }

    public String getNiveauEtude() {
        return niveauEtude;
    }

    public void setNiveauEtude(String niveauEtude) {
        this.niveauEtude = niveauEtude;
    }

    public String getDiplomePrincipal() {
        return diplomePrincipal;
    }

    public void setDiplomePrincipal(String diplomePrincipal) {
        this.diplomePrincipal = diplomePrincipal;
    }

    public String getCompetencesCles() {
        return competencesCles;
    }

    public void setCompetencesCles(String competencesCles) {
        this.competencesCles = competencesCles;
    }

    public String getLangues() {
        return langues;
    }

    public void setLangues(String langues) {
        this.langues = langues;
    }

    public BigDecimal getPretentionsSalariales() {
        return pretentionsSalariales;
    }

    public void setPretentionsSalariales(BigDecimal pretentionsSalariales) {
        this.pretentionsSalariales = pretentionsSalariales;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getScoreCompetences() {
        return scoreCompetences;
    }

    public void setScoreCompetences(Integer scoreCompetences) {
        this.scoreCompetences = scoreCompetences;
    }

    public BigDecimal getNoteGlobale() {
        return noteGlobale;
    }

    public void setNoteGlobale(BigDecimal noteGlobale) {
        this.noteGlobale = noteGlobale;
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
