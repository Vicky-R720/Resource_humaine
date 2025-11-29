package com.itu.gest_emp.modules.shared.model;

import jakarta.persistence.*;
import com.itu.gest_emp.modules.personnel.model.CategoriePersonnel;
import com.itu.gest_emp.modules.shared.model.Equipe;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "missions", columnDefinition = "TEXT")
    private String missions;

    /* service relation removed as requested */


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private CategoriePersonnel categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    public Post() {
    }

    public Post(String name, String description, String missions) {
        this.name = name;
        this.description = description;
        this.missions = missions;
    }

    public Post(String name, String description, String missions, CategoriePersonnel categorie, Equipe equipe) {
        this.name = name;
        this.description = description;
        this.missions = missions;
        this.categorie = categorie;
        this.equipe = equipe;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMissions() {
        return missions;
    }

    public void setMissions(String missions) {
        this.missions = missions;
    }

    // service getters/setters removed

    public CategoriePersonnel getCategorie() {
        return categorie;
    }

    public void setCategorie(CategoriePersonnel categorie) {
        this.categorie = categorie;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", missions='" + missions + '\'' +
                ", categorie=" + (categorie != null ? categorie.getNom() : "null") +
                ", equipe=" + (equipe != null ? equipe.getNom() : "null") +
                '}';
    }
}
