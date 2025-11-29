package com.itu.gest_emp.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_requests_rh")
public class LeaveRequestsRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypesRH leaveType;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "nombre_jours", nullable = false)
    private Double nombreJours;

    private String motif;

    @Column(name = "justificatif_path")
    private String justificatifPath;

    private String statut = "en_attente";

    @ManyToOne
    @JoinColumn(name = "validated_by")
    private Person validatedBy;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "validation_comment")
    private String validationComment;

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
    public LeaveTypesRH getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveTypesRH leaveType) { this.leaveType = leaveType; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Double getNombreJours() { return nombreJours; }
    public void setNombreJours(Double nombreJours) { this.nombreJours = nombreJours; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getJustificatifPath() { return justificatifPath; }
    public void setJustificatifPath(String justificatifPath) { this.justificatifPath = justificatifPath; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Person getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Person validatedBy) { this.validatedBy = validatedBy; }
    public LocalDateTime getValidationDate() { return validationDate; }
    public void setValidationDate(LocalDateTime validationDate) { this.validationDate = validationDate; }
    public String getValidationComment() { return validationComment; }
    public void setValidationComment(String validationComment) { this.validationComment = validationComment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}