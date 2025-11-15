package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.model.Post;

@Entity
@Table(name = "personnel_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id", unique = true)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(unique = true, nullable = false, length = 50)
    private String matricule;

    @Column(length = 50)
    private String cin;

    private LocalDate cinDateDelivery;

    @Column(length = 255)
    private String cinPlaceDelivery;

    @Column(length = 10)
    private String sexe;

    @Column(length = 50)
    private String situationFamiliale;

    private Integer nombreEnfants = 0;

    @Column(length = 100)
    private String nationalite = "Malagasy";

    @Column(length = 255)
    private String lieuNaissance;

    @Column(length = 255)
    private String personneUrgenceNom;

    @Column(length = 100)
    private String personneUrgenceContact;

    @Column(length = 100)
    private String personneUrgenceLien;

    @Column(length = 100)
    private String rib;

    @Column(length = 255)
    private String banque;

    @Column(length = 50)
    private String numeroCnaps;

    @Column(length = 50)
    private String numeroOstie;

    @Column(nullable = false)
    private LocalDate dateEmbauche;

    private LocalDate dateSortie;

    @Column(columnDefinition = "TEXT")
    private String motifSortie;

    @Column(length = 50)
    private String statut = "actif";

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

  
}