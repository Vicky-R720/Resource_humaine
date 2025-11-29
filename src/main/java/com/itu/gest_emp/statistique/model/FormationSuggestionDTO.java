package com.itu.gest_emp.statistique.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour les suggestions de formation basées sur les gaps
 */
public class FormationSuggestionDTO {
    
    // Informations employé
    private Long personnelId;
    private String employeNom;
    private String matricule;
    private String poste;
    
    // Informations gap
    private Long competenceId;
    private String competenceNom;
    private String categorie;
    private Integer niveauActuel;
    private Integer niveauRequis;
    private Integer gapLevel;
    
    // Priorité
    private Integer priorityScore; // 1-4
    private String priorityLabel; // Faible, Moyen, Élevé, Critique
    private String businessImpact; // bas, moyen, élevé, critique
    private String urgence; // faible, moyenne, haute, urgente
    
    // Formation recommandée
    private Long formationId;
    private String formationTitre;
    private String organisme;
    private String typeFormation;
    private Integer dureeHeures;
    private BigDecimal cout;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statutFormation;
    
    // Métriques
    private Integer matchScore; // 0-100
    private String recommendation; // "Fortement recommandé", "Recommandé", etc.
    
    public FormationSuggestionDTO() {
    }
    
    // Constructeur complet
    public FormationSuggestionDTO(Long personnelId, String employeNom, String matricule, 
                                 String poste, Long competenceId, String competenceNom,
                                 String categorie, Integer niveauActuel, Integer niveauRequis,
                                 Integer gapLevel, Integer priorityScore) {
        this.personnelId = personnelId;
        this.employeNom = employeNom;
        this.matricule = matricule;
        this.poste = poste;
        this.competenceId = competenceId;
        this.competenceNom = competenceNom;
        this.categorie = categorie;
        this.niveauActuel = niveauActuel;
        this.niveauRequis = niveauRequis;
        this.gapLevel = gapLevel;
        this.priorityScore = priorityScore;
        
        // Calculer le label de priorité
        this.priorityLabel = getPriorityLabelFromScore(priorityScore);
    }
    
    // Helpers
    private String getPriorityLabelFromScore(Integer score) {
        if (score == null) return "Non défini";
        switch (score) {
            case 4: return "Critique";
            case 3: return "Élevé";
            case 2: return "Moyen";
            case 1: return "Faible";
            default: return "Non défini";
        }
    }
    
    public String getPriorityColor() {
        if (priorityScore == null) return "#718096";
        switch (priorityScore) {
            case 4: return "#ef4444"; // Rouge
            case 3: return "#f59e0b"; // Orange
            case 2: return "#eab308"; // Jaune
            case 1: return "#10b981"; // Vert
            default: return "#718096"; // Gris
        }
    }
    
    public String getUrgenceColor() {
        if (urgence == null) return "#718096";
        switch (urgence.toLowerCase()) {
            case "urgente": return "#ef4444";
            case "haute": return "#f59e0b";
            case "moyenne": return "#eab308";
            case "faible": return "#10b981";
            default: return "#718096";
        }
    }

    // Getters et Setters
    public Long getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Long getCompetenceId() {
        return competenceId;
    }

    public void setCompetenceId(Long competenceId) {
        this.competenceId = competenceId;
    }

    public String getCompetenceNom() {
        return competenceNom;
    }

    public void setCompetenceNom(String competenceNom) {
        this.competenceNom = competenceNom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getNiveauActuel() {
        return niveauActuel;
    }

    public void setNiveauActuel(Integer niveauActuel) {
        this.niveauActuel = niveauActuel;
    }

    public Integer getNiveauRequis() {
        return niveauRequis;
    }

    public void setNiveauRequis(Integer niveauRequis) {
        this.niveauRequis = niveauRequis;
    }

    public Integer getGapLevel() {
        return gapLevel;
    }

    public void setGapLevel(Integer gapLevel) {
        this.gapLevel = gapLevel;
    }

    public Integer getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Integer priorityScore) {
        this.priorityScore = priorityScore;
        this.priorityLabel = getPriorityLabelFromScore(priorityScore);
    }

    public String getPriorityLabel() {
        return priorityLabel;
    }

    public void setPriorityLabel(String priorityLabel) {
        this.priorityLabel = priorityLabel;
    }

    public String getBusinessImpact() {
        return businessImpact;
    }

    public void setBusinessImpact(String businessImpact) {
        this.businessImpact = businessImpact;
    }

    public String getUrgence() {
        return urgence;
    }

    public void setUrgence(String urgence) {
        this.urgence = urgence;
    }

    public Long getFormationId() {
        return formationId;
    }

    public void setFormationId(Long formationId) {
        this.formationId = formationId;
    }

    public String getFormationTitre() {
        return formationTitre;
    }

    public void setFormationTitre(String formationTitre) {
        this.formationTitre = formationTitre;
    }

    public String getOrganisme() {
        return organisme;
    }

    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }

    public String getTypeFormation() {
        return typeFormation;
    }

    public void setTypeFormation(String typeFormation) {
        this.typeFormation = typeFormation;
    }

    public Integer getDureeHeures() {
        return dureeHeures;
    }

    public void setDureeHeures(Integer dureeHeures) {
        this.dureeHeures = dureeHeures;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatutFormation() {
        return statutFormation;
    }

    public void setStatutFormation(String statutFormation) {
        this.statutFormation = statutFormation;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}