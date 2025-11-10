package com.itu.gest_emp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "qcm_reponses")
public class Reponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Column(name = "est_correcte")
    private boolean estCorrecte;

    // CORRECTION: Suppression de l'import incorrect
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QcmQuestion question;

    // Constructeurs
    public Reponse() {}

    public Reponse(String texte, boolean estCorrecte, QcmQuestion question) {
        this.texte = texte;
        this.estCorrecte = estCorrecte;
        this.question = question;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public boolean isEstCorrecte() { return estCorrecte; }
    public void setEstCorrecte(boolean estCorrecte) { this.estCorrecte = estCorrecte; }

    public QcmQuestion getQuestion() { return question; }
    public void setQuestion(QcmQuestion question) { this.question = question; }
}