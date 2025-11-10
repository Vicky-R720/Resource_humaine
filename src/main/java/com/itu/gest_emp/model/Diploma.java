package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "diploma")
public class Diploma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    
    @Column(name = "niveau")
    private Integer niveau;
    
    @OneToMany(mappedBy = "diploma")
    private List<AcademicalQualification> academicalQualifications;
    
    // Constructeurs
    public Diploma() {}
    
    public Diploma(String name) {
        this.name = name;
    }
    
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    
    public List<AcademicalQualification> getAcademicalQualifications() { 
        return academicalQualifications; 
    }
    public void setAcademicalQualifications(List<AcademicalQualification> academicalQualifications) { 
        this.academicalQualifications = academicalQualifications; 
    }
    
    @Override
    public String toString() {
        return "Diploma{" +
                "id=" + id +
                ", name='" + name + '\'';
    }
}