package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // CDI, CDD, STAGE, INTERIM, etc.

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresEndDate = true; // false pour CDI uniquement

    @Column
    private Integer dureeMaxMois; // null pour CDI, 18 pour CDD, 6 pour STAGE, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasTrialPeriod = true;

    @Column
    private Integer dureeEssaiMaxMois; // Durée d'essai maximale autorisée

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}