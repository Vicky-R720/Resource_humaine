package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "academical_qualification")
public class AcademicalQualification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "diploma_id", nullable = false)
    private Diploma diploma;

    @ManyToOne
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    
    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;
    
    @ManyToOne
    @JoinColumn(name = "appliance_id", nullable = false)
    private Appliance appliance;
    
    // Constructeurs
    public AcademicalQualification() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AcademicalQualification(Diploma diploma, Sector sector, Appliance appliance,Filiere filiere) {
        this();
        this.diploma = diploma;
        this.sector = sector;
        this.appliance = appliance;
        this.filiere=filiere;
    }
    
    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public Diploma getDiploma() { 
        return diploma; 
    }
    
    public void setDiploma(Diploma diploma) { 
        this.diploma = diploma; 
    }
    
    public Sector getSector() { 
        return sector; 
    }
    
    public void setSector(Sector sector) { 
        this.sector = sector; 
    }
    
    public Appliance getAppliance() { 
        return appliance; 
    }
    
    public void setAppliance(Appliance appliance) { 
        this.appliance = appliance; 
    }
    
    @PreUpdate
    public void preUpdate() {
        // Pas de mise à jour automatique de createdAt car c'est une date de création
    }
    
    @Override
    public String toString() {
        return "AcademicalQualification{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", diploma=" + (diploma != null ? diploma.getName() : null) +
                ", sector=" + (sector != null ? sector.getName() : null) +
                ", appliance=" + (appliance != null ? appliance.getId() : null) +
                '}';
    }
}