package com.itu.gest_emp.model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
@Entity
@Table(name = "leave_balance_rh")
public class LeaveBalanceRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRH personnel;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypesRH leaveType;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "solde_initial")
    private Double soldeInitial = 0.0;

    @Column(name = "solde_acquis")
    private Double soldeAcquis = 0.0;

    @Column(name = "solde_pris")
    private Double soldePris = 0.0;

    @Column(name = "solde_restant")
    private Double soldeRestant = 0.0;

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
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public Double getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(Double soldeInitial) { this.soldeInitial = soldeInitial; }
    public Double getSoldeAcquis() { return soldeAcquis; }
    public void setSoldeAcquis(Double soldeAcquis) { this.soldeAcquis = soldeAcquis; }
    public Double getSoldePris() { return soldePris; }
    public void setSoldePris(Double soldePris) { this.soldePris = soldePris; }
    public Double getSoldeRestant() { return soldeRestant; }
    public void setSoldeRestant(Double soldeRestant) { this.soldeRestant = soldeRestant; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}