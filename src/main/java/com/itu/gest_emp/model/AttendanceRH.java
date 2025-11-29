package com.itu.gest_emp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "attendance_rh")
public class AttendanceRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @Column(nullable = false)
    private LocalDate datePointage;

    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;
    private Integer dureeTravailMinutes;

    private String statut = "present"; // present, absent, retard, conge, maladie
    private String typePointage = "manuel"; // manuel, badgeuse, mobile

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "validated_by")
    private Person validatedBy;

    private Boolean isValidated = false;

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

    public LocalDate getDatePointage() {
        return datePointage;
    }

    public void setDatePointage(LocalDate datePointage) {
        this.datePointage = datePointage;
    }

    public LocalTime getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(LocalTime heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public LocalTime getHeurePauseDebut() {
        return heurePauseDebut;
    }

    public void setHeurePauseDebut(LocalTime heurePauseDebut) {
        this.heurePauseDebut = heurePauseDebut;
    }

    public LocalTime getHeurePauseFin() {
        return heurePauseFin;
    }

    public void setHeurePauseFin(LocalTime heurePauseFin) {
        this.heurePauseFin = heurePauseFin;
    }

    public Integer getDureeTravailMinutes() {
        return dureeTravailMinutes;
    }

    public void setDureeTravailMinutes(Integer dureeTravailMinutes) {
        this.dureeTravailMinutes = dureeTravailMinutes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getTypePointage() {
        return typePointage;
    }

    public void setTypePointage(String typePointage) {
        this.typePointage = typePointage;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Person getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(Person validatedBy) {
        this.validatedBy = validatedBy;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
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
