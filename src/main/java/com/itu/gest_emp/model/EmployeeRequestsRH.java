package com.itu.gest_emp.model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Entity
@Table(name = "employee_requests_rh")
public class EmployeeRequestsRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @Column(name = "type_demande", nullable = false)
    private String typeDemande;

    private String description;

    @Column(name = "montant_demande")
    private Double montantDemande;

    @Column(name = "justificatif_path")
    private String justificatifPath;

    private String statut = "en_attente";

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private Person processedBy;

    @Column(name = "processing_date")
    private LocalDateTime processingDate;

    @Column(name = "processing_comment")
    private String processingComment;

    @Column(name = "document_genere_path")
    private String documentGenerePath;

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
    public String getTypeDemande() { return typeDemande; }
    public void setTypeDemande(String typeDemande) { this.typeDemande = typeDemande; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getMontantDemande() { return montantDemande; }
    public void setMontantDemande(Double montantDemande) { this.montantDemande = montantDemande; }
    public String getJustificatifPath() { return justificatifPath; }
    public void setJustificatifPath(String justificatifPath) { this.justificatifPath = justificatifPath; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Person getProcessedBy() { return processedBy; }
    public void setProcessedBy(Person processedBy) { this.processedBy = processedBy; }
    public LocalDateTime getProcessingDate() { return processingDate; }
    public void setProcessingDate(LocalDateTime processingDate) { this.processingDate = processingDate; }
    public String getProcessingComment() { return processingComment; }
    public void setProcessingComment(String processingComment) { this.processingComment = processingComment; }
    public String getDocumentGenerePath() { return documentGenerePath; }
    public void setDocumentGenerePath(String documentGenerePath) { this.documentGenerePath = documentGenerePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}