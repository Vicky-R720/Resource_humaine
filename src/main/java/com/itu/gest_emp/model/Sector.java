package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "sector")
public class Sector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @OneToMany(mappedBy = "sector")
    private List<AcademicalQualification> academicalQualifications;
    
    // Constructeurs
    public Sector() {}
    
    public Sector(String name) {
        this.name = name;
    }
    
    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public List<AcademicalQualification> getAcademicalQualifications() { 
        return academicalQualifications; 
    }
    
    public void setAcademicalQualifications(List<AcademicalQualification> academicalQualifications) { 
        this.academicalQualifications = academicalQualifications; 
    }
    
    @Override
    public String toString() {
        return "Sector{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}