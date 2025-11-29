package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorie_personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriePersonnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false)
    private Integer niveau; 
    // 1: Ouvrier
    // 2: Employé
    // 3: TAM (Technicien/Agent de Maîtrise)
    // 4: Cadre
    // 5: Dirigeant
}
