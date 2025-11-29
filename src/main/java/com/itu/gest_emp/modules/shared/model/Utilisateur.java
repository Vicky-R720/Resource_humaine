package com.itu.gest_emp.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation 1:1 avec Person
    @OneToOne
    @JoinColumn(name = "person_id", unique = true, nullable = false)
    private Person person;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Column(nullable = false, length = 50)
    private String role = "EMPLOYE"; // EMPLOYE, MANAGER, RH, ADMIN

    @Column(nullable = false)
    private boolean actif = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
