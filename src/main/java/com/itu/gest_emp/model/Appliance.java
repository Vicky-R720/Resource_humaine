package com.itu.gest_emp.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.itu.gest_emp.modules.shared.model.Person;

@Entity
@Table(name = "appliance")
public class Appliance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = " date_entretien")
    private LocalDateTime Planning;
    
    public LocalDateTime getPlanning() {
        return Planning;
    }

    public void setPlanning() {
        LocalDateTime baseDate= LocalDateTime.now().plusDays(1);
         while (baseDate.getDayOfWeek() == DayOfWeek.SATURDAY || 
               baseDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            baseDate = baseDate.plusDays(1);
        }
        Planning = baseDate;
    }
    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
    
    @Column(name = "experiencelevel")
    private String experienceLevel;

    @Column(name = "traitement", columnDefinition = "TEXT")
    private String traitement;

    @Column(name = "cv_link", columnDefinition = "TEXT")
    private String cvLink;
    
    @Column(name = "skills", columnDefinition = "INT")
    private String skills;

    @Column(name = "noteqcm")
    private Integer noteQCM;

    public Integer getNoteQCM() {
        return noteQCM;
    }

    public void setNoteQCM(Integer noteQCM) {
        this.noteQCM = noteQCM;
    }
    @Column(name = "noteeval")
    private Integer noteEval;
    
    public Integer getNoteEval() {
        return noteEval;
    }

    public void setNoteEval(Integer noteEval) {
        this.noteEval = noteEval;
    }

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    
    @OneToMany(mappedBy = "appliance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AcademicalQualification> academicalQualifications;
    
    // Constructeurs
    public Appliance() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Appliance(Offer offer, String cvLink, String skills, Person person) {
        this();
        this.offer = offer;
        this.cvLink = cvLink;
        this.skills = skills;
        this.person = person;
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
    
    public Offer getOffer() { 
        return offer; 
    }
    
    public void setOffer(Offer offer) { 
        this.offer = offer; 
    }
    
    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getCvLink() { 
        return cvLink; 
    }
    
    public void setCvLink(String cvLink) { 
        this.cvLink = cvLink; 
    }
    
    public String getSkills() { 
        return skills; 
    }
    
    public void setSkills(String skills) { 
        this.skills = skills; 
    }
    
    public Person getPerson() { 
        return person; 
    }
    
    public void setPerson(Person person) { 
        this.person = person; 
    }
    
    public List<AcademicalQualification> getAcademicalQualifications() { 
        return academicalQualifications; 
    }
    
    public void setAcademicalQualifications(List<AcademicalQualification> academicalQualifications) { 
        this.academicalQualifications = academicalQualifications; 
    }
    
    @PreUpdate
    public void preUpdate() {
        // Pas de mise à jour automatique de createdAt car c'est une date de création
    }
    
    // ============ FONCTION MATCHING CV ============
    
    /**
     * Calcule le score de matching pour cette candidature
     * Même logique que dans la classe Person
     */
    public double matchingCV() {
        double result = 0;
        
        if (person == null || offer == null) {
            return 0.0;
        }
        
        int age = person.getAge();
        int min = 18;
        int max = 55;
        
        System.out.println("\n=== CALCUL MATCHING CV (Appliance) ===");
        System.out.println("Candidat: " + person.getFullName());
        System.out.println("Âge candidat: " + age);
        System.out.println("Offre: " + offer.getPost().getName());
        
        // Vérification de l'âge (3 points max)
        if (age >= min && age <= max) {
            result = result + 3;
            System.out.println("✓ Âge valide (+3 points)");
        } else {
            System.out.println("✗ Âge non valide");
        }
        
        // Vérification si la candidature est active
        if ("en_cours".equals(traitement)) {
            System.out.println("✓ Candidature active");
            
            String diploma = offer.getDiploma();
            String experience = offer.getExperienceLevel();
            String filiere = offer.getFiliere();
            
            System.out.println("Diplôme requis: " + diploma);
            System.out.println("Expérience requise: " + experience);
            System.out.println("Filière requise: " + filiere);
            
            // FLAG pour une seule qualification
            boolean qualificationEvaluee = false;
            
            if (academicalQualifications != null && !academicalQualifications.isEmpty()) {
                
                System.out.println("Qualifications académiques trouvées: " + 
                                  academicalQualifications.size());
                
                for (AcademicalQualification aq : academicalQualifications) {
                    
                    // ⚠️ NE ÉVALUER QU'UNE SEULE QUALIFICATION
                    if (qualificationEvaluee) {
                        System.out.println("  ⚠️  Une qualification déjà évaluée - skip");
                        continue;
                    }
                    
                    System.out.println("\n  Analyse qualification: " + aq.getId());
                    
                    // VÉRIFICATION NULL
                    if (aq.getFiliere() == null) {
                        System.out.println("  ✗ Filiere NULL - skip");
                        continue;
                    }
                    
                    Integer niveauFiliere = aq.getFiliere().getNiveau();
                    if (niveauFiliere == null) {
                        System.out.println("  ✗ Niveau NULL - skip");
                        continue;
                    }
                    
                    System.out.println("  Filière candidat: " + aq.getFiliere().getName() + 
                                      " (niveau " + niveauFiliere + ")");
                    
                    // === DIPLÔME (2 points max) ===
                    if (diploma != null && aq.getDiploma() != null && 
                        diploma.equals(aq.getDiploma().getName())) {
                        result = result + 2;
                        System.out.println("  ✓ Diplôme match (+2 points) "+ aq.getDiploma().getName());
                    }
                    
                    // === FILIÈRE (7 points max) ===
                    if (filiere != null) {
                        int noteRequise = 0;
                        if (filiere.equals("BACC")) noteRequise = 1;
                        else if (filiere.equals("BACC+1")) noteRequise = 2;
                        else if (filiere.equals("BACC+2")) noteRequise = 3;
                        else if (filiere.equals("Licence")) noteRequise = 4;
                        else if (filiere.equals("BACC+5")) noteRequise = 6;
                        else if (filiere.equals("MASTER")) noteRequise = 7;
                        
                        if (noteRequise > 0) {
                            int ecart = noteRequise - niveauFiliere;
                            
                            if (ecart < 0) { // Candidat surqualifié
                                result = result + 7;
                                System.out.println("  ✓ Surqualifié (+7 points) "+aq.getFiliere().getName());
                            }
                            else if (ecart == 0) { // Niveau exact
                                result = result + 6;
                                System.out.println("  ✓ Niveau exact (+6 points) "+aq.getFiliere().getName());
                            }
                            else if (ecart > 0) { // Candidat sous-qualifié
                                result = result + 4;
                                System.out.println("  ✓ Débutant (+4 points) "+aq.getFiliere().getName());
                            }
                        }
                    }
                    
                    // === EXPÉRIENCE (5 points max) ===
                    if (experience != null && experienceLevel != null) {
                        try {
                            int expRequise = Integer.parseInt(experience);
                            int expCandidat = Integer.parseInt(experienceLevel);
                            
                            System.out.println("  Expérience requise: " + expRequise + " ans, candidat: " + expCandidat + " ans");
                            
                            if (expCandidat >= expRequise) {
                                result += 5;
                                System.out.println("  ✓ Expérience suffisante (+5 points)");
                            } 
                            else {
                                result += 2;
                                System.out.println("  ✓ Expérience insuffisante (+2 points)");
                            }
                            
                        } catch (NumberFormatException e) {
                            System.out.println("  ✗ Erreur conversion expérience");
                        }
                    }
                    
                    // MARQUER COMME ÉVALUÉE
                    qualificationEvaluee = true;
                    break; // ⚠️ SORTIR DE LA BOUCLE APRÈS UNE QUALIFICATION
                }
            } else {
                System.out.println("  Aucune qualification académique");
            }
        } else {
            System.out.println("✗ Candidature non active (traitement: " + traitement + ")");
        }
        
        // ⚠️ LIMITER À 17 POINTS MAXIMUM
        result = Math.min(result, 17.0);
        
        System.out.println("\n=== RÉSULTAT FINAL ===");
        System.out.println("Points totaux: " + result + "/17");
        System.out.println("Score: " + (result / 17.0) * 100 + "%");
        System.out.println("=====================\n");
        
        return (10 / 17.0) * 100;
    }
    
    /**
     * Évaluation qualitative du matching
     */
    public String getMatchingEvaluation() {
        double score = matchingCV();
        
        if (score >= 80) {
            return "EXCELLENT - Très bien adapté (" + String.format("%.1f", score) + "%)";
        } else if (score >= 60) {
            return "BON - Bien adapté (" + String.format("%.1f", score) + "%)";
        } else if (score >= 40) {
            return "MOYEN - Acceptable (" + String.format("%.1f", score) + "%)";
        } else if (score >= 20) {
            return "FAIBLE - Peu adapté (" + String.format("%.1f", score) + "%)";
        } else {
            return "INSUFFISANT - Non adapté (" + String.format("%.1f", score) + "%)";
        }
    }
    
    /**
     * Génère un rapport détaillé de matching
     */
    public Boolean Etat(String Etat){
        return traitement.equals(Etat);
    }
    public static List<Appliance> getApplianceswithTraitement(List<Appliance> applianceList,String Etat)
    {
        List<Appliance> result=new ArrayList<Appliance>();
        for (Appliance ap : applianceList) {
            if(ap.Etat(Etat)){
                result.add(ap);
            }
        }
        return result;
    }
}