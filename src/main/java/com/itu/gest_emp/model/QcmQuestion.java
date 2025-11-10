package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "qcm_questions")
public class QcmQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Column(name = "categorie")
    private String categorie; // "entreprise_alcool" ou "culture_generale"

    // CORRECTION: EAGER fetch pour charger les réponses automatiquement
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Reponse> reponses = new ArrayList<>();

    // Constructeurs
    public QcmQuestion() {}

    public QcmQuestion(String texte, String categorie) {
        this.texte = texte;
        this.categorie = categorie;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }
}