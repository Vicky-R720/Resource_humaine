package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.itu.gest_emp.modules.personnel.service.StatutDemande;
import com.itu.gest_emp.modules.shared.model.Utilisateur;

@Table(name = "demande_rupture_contrat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DemandeRuptureContrat {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ContractsRh contrat;

    @ManyToOne
    private PersonnelRh demandeur; // Qui fait la demande

    private String motif; // licenciement, démission, etc.
    private String justification; // Texte libre
    private LocalDate dateDemande;
    private LocalDate dateEffetSouhaitee;
    private boolean preavisEffectue;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut; // EN_ATTENTE, VALIDEE, REFUSEE

    @ManyToOne
    private Utilisateur validateur;
    private LocalDate dateValidation;
    private String commentaireValidation;
}