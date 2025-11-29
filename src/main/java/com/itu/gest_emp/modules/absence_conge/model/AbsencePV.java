package com.itu.gest_emp.modules.absence_conge.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "absence_pv")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsencePV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    @Column(nullable = false)
    private LocalDate dateAbsence;

    @Column(length = 1000)
    private String motifAbsence;

    @Column(length = 1000)
    private String explicationEmployee;

    @Column
    private LocalDate dateExplication;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PVStatut statut = PVStatut.EN_ATTENTE;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private Integer nombreAbsencesRepetees;

    @Column(length = 1000)
    private String decisionRh;

    public enum PVStatut {
        EN_ATTENTE,
        ACCEPTE,
        REFUSE,
        SANCTIONNE
    }
}