package com.itu.gest_emp.modules.absence_conge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.shared.model.Person;

@Entity
@Table(name = "leave_requests_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "nombre_jours", nullable = false, precision = 5, scale = 2)
    private BigDecimal nombreJours;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @Column(name = "justificatif_path", length = 500)
    private String justificatifPath;

    @Column(name = "statut", length = 50)
    private String statut = "en_attente"; // en_attente, approuve, refuse, annule

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private Person validatedBy;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @Column(name = "validation_comment", columnDefinition = "TEXT")
    private String validationComment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructeurs, getters et setters...
}