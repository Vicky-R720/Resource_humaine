package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "filiere")
public class Filiere {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "niveau")
    private Integer niveau;
    
    @OneToMany(mappedBy = "filiere")
    private List<AcademicalQualification> academicalQualifications;
    
    // Constructeurs
    public Filiere() {}
    
    public Filiere(String name) {
        this.name = name;
    }
    
    public Filiere(String name, Integer niveau) {
        this.name = name;
        this.niveau = niveau;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }
    
    public List<AcademicalQualification> getAcademicalQualifications() { 
        return academicalQualifications; 
    }
    
    public void setAcademicalQualifications(List<AcademicalQualification> academicalQualifications) { 
        this.academicalQualifications = academicalQualifications; 
    }
    
    @Override
    public String toString() {
        return "Filiere{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", niveau=" + niveau +
                '}';
    }
}