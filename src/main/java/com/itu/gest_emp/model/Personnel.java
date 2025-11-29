package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personnel")
public class Personnel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personnel")
    private Long idPersonnel;
    
    @ManyToOne
    @JoinColumn(name = "id_personne", nullable = false)
    private Person personne;
    
    @Column(name = "poste", nullable = false, length = 255)
    private String poste;
    
    @Column(name = "date_embauche")
    private LocalDateTime dateEmbauche;
    
    // Constructeurs
    public Personnel() {
        this.dateEmbauche = LocalDateTime.now();
    }
    
    public Personnel(Person personne, String poste) {
        this();
        this.personne = personne;
        this.poste = poste;
    }
    
    // Getters et Setters
    public Long getIdPersonnel() {
        return idPersonnel;
    }
    
    public void setIdPersonnel(Long idPersonnel) {
        this.idPersonnel = idPersonnel;
    }
    
    public Person getPersonne() {
        return personne;
    }
    
    public void setPersonne(Person personne) {
        this.personne = personne;
    }
    
    public String getPoste() {
        return poste;
    }
    
    public void setPoste(String poste) {
        this.poste = poste;
    }
    
    public LocalDateTime getDateEmbauche() {
        return dateEmbauche;
    }
    
    public void setDateEmbauche(LocalDateTime dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }
    
    @PreUpdate
    public void preUpdate() {
        // La date d'embauche ne change pas après la création
    }
    
    @Override
    public String toString() {
        return "Personnel{" +
                "idPersonnel=" + idPersonnel +
                ", personne=" + personne +
                ", poste='" + poste + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                '}';
    }
}