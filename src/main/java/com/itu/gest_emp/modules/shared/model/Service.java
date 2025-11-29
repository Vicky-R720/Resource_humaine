package com.itu.gest_emp.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 255)
    private String nom;

    // 🔥 Correction : ajouter ManyToOne pour la relation manager
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Utilisateur manager;

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", manager=" + (manager != null ? manager.getId() : "null") +
                '}';
    }
}
