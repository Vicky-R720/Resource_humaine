package com.itu.gest_emp.statistique.model;

/**
 * DTO pour la matrice compétences par poste
 */
public class CompetenceMatrixDTO {
    private String poste;
    private String competence;
    private String categorie;
    private Double niveauMoyen;
    private Integer nbPersonnes;
    
    public CompetenceMatrixDTO() {
    }
    
    public CompetenceMatrixDTO(String poste, String competence, String categorie, 
                              Double niveauMoyen, Integer nbPersonnes) {
        this.poste = poste;
        this.competence = competence;
        this.categorie = categorie;
        this.niveauMoyen = niveauMoyen;
        this.nbPersonnes = nbPersonnes;
    }

    // Getters et Setters
    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Double getNiveauMoyen() {
        return niveauMoyen;
    }

    public void setNiveauMoyen(Double niveauMoyen) {
        this.niveauMoyen = niveauMoyen;
    }

    public Integer getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(Integer nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }
    
    @Override
    public String toString() {
        return "CompetenceMatrixDTO{" +
                "poste='" + poste + '\'' +
                ", competence='" + competence + '\'' +
                ", categorie='" + categorie + '\'' +
                ", niveauMoyen=" + niveauMoyen +
                ", nbPersonnes=" + nbPersonnes +
                '}';
    }
}