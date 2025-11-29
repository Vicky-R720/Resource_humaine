package com.itu.gest_emp.modules.absence_conge.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "planning_conges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanningConge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "periode_recommandee_debut")
    private LocalDate periodeRecommandeeDebut;

    @Column(name = "periode_recommandee_fin")
    private LocalDate periodeRecommandeeFin;

    @Column(name = "is_periode_validee")
    private Boolean isPeriodeValidee = false;

    @Column(length = 500)
    private String commentaires;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}