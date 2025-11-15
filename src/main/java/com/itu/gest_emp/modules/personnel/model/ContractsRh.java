package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractsRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;
    
    @Column(nullable = false, length = 50)
    private String typeContrat;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    
    private Integer dureeEssaiMois = 0;
    
    private LocalDate dateFinEssai;
    
    private Boolean isEssaiValide = false;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal salaireBase;
    
    @Column(length = 50)
    private String statut = "actif";
    
    @Column(columnDefinition = "TEXT")
    private String motifFin;
    
    @Column(length = 500)
    private String documentPath;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}