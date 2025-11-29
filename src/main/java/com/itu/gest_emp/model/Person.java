package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "person")
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;
    
    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;
    
    @Column(name = "naissance")
    private LocalDate naissance;
    
    @Column(name = "contact", length = 100)
    private String contact;

    @Column(name = "mdp", length = 300)
    private String mdp;

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
    @Column(name = "pdp", length = 300)
    private String pdp;
    
    public String getPdp() {
        return pdp;
    }

    public void setPdp(String pdp) {
        this.pdp = pdp;
    }
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appliance> appliances;
    
    // Constructeurs
    public Person() {}
    
    public Person(String nom, String prenom, String adresse, LocalDate naissance, String contact,String mdp,String pdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.naissance = naissance;
        this.contact = contact;
        this.mdp=mdp;
        this.pdp=pdp;
    }
    
    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getPrenom() { 
        return prenom; 
    }
    
    public void setPrenom(String prenom) { 
        this.prenom = prenom; 
    }
    
    public String getAdresse() { 
        return adresse; 
    }
    
    public void setAdresse(String adresse) { 
        this.adresse = adresse; 
    }
    
    public LocalDate getNaissance() { 
        return naissance; 
    }
    
    public void setNaissance(LocalDate naissance) { 
        this.naissance = naissance; 
    }
    
    public String getContact() { 
        return contact; 
    }
    
    public void setContact(String contact) { 
        this.contact = contact; 
    }
    
    public List<Appliance> getAppliances() { 
        return appliances; 
    }
    
    public void setAppliances(List<Appliance> appliances) { 
        this.appliances = appliances; 
    }
    
    // Méthode utilitaire pour calculer l'âge
    public int getAge() {
        if (naissance == null) {
            return 0;
        }
        return Period.between(naissance, LocalDate.now()).getYears();
    }
    
    // Méthode utilitaire pour le nom complet
    public String getFullName() {
        return prenom + " " + nom;
    }
    
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                " prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", naissance=" + naissance +
                ", contact='" + contact + '\'' +
                '}';
    }
    public String getFullInformation() {
    StringBuilder sb = new StringBuilder();
    
    
    sb.append("Date de naissance: ").append(naissance != null ? naissance : "Non renseignée").append("\n");
    
    // Compétences (récupérées depuis les appliances)
    sb.append("\n=== COMPÉTENCES ===\n");
    boolean hasSkills = false;
    if (appliances != null && !appliances.isEmpty()) {
        for (Appliance appliance : appliances) {
            if (appliance.getSkills() != null && !appliance.getSkills().isEmpty()) {
                sb.append(appliance.getSkills()).append("\n");
                hasSkills = true;
                break; // On prend les compétences de la première appliance
            }
        }
    }
    if (!hasSkills) {
        sb.append("Aucune compétence renseignée\n");
    }
    
    // Diplômes académiques
    sb.append("\n=== DIPLÔMES ET FORMATIONS ===\n");
    boolean hasDiplomas = false;
    if (appliances != null && !appliances.isEmpty()) {
        for (Appliance appliance : appliances) {
            if (appliance.getAcademicalQualifications() != null && 
                !appliance.getAcademicalQualifications().isEmpty()) {
                for (AcademicalQualification aq : appliance.getAcademicalQualifications()) {
                    sb.append("• ").append(aq.getDiploma() != null ? aq.getDiploma().getName() : "Diplôme non spécifié");
                    sb.append(" - ").append(aq.getSector() != null ? aq.getSector().getName() : "Secteur non spécifié");
                    sb.append("\n");
                    hasDiplomas = true;
                }
            }
        }
    }
    if (!hasDiplomas) {
        sb.append("Aucun diplôme renseigné\n");
    }
    
    // Demandes d'offres
    sb.append("\n=== DEMANDES D'OFFRES ===\n");
    if (appliances != null && !appliances.isEmpty()) {
        int count = 1;
        for (Appliance appliance : appliances) {
            if (appliance.getOffer() != null) {
                Offer offer = appliance.getOffer();
                sb.append("Demande #").append(count++).append(":\n");
                sb.append("  Poste: ").append(offer.getPost() != null ? offer.getPost().getName() : "Non spécifié").append("\n");
                sb.append("  Entreprise: ").append(offer.getCompanyName()).append("\n");
                sb.append("  Lieu: ").append(offer.getLocation()).append("\n");
                sb.append("  Type de contrat: ").append(offer.getContractType() != null ? offer.getContractType().getName() : "Non spécifié").append("\n");
                sb.append("  Expérience requise: ").append(offer.getExperienceLevel() != null ? offer.getExperienceLevel() : "Non spécifiée").append("\n");
                sb.append("  Diplôme requis: ").append(offer.getDiploma() != null ? offer.getDiploma() : "Non spécifié").append("\n");
                sb.append("  Places disponibles: ").append(offer.getAvailablePlaces()).append("\n");
                sb.append("  Date de candidature: ").append(appliance.getCreatedAt()).append("\n");
                sb.append("  CV: ").append(appliance.getCvLink() != null ? appliance.getCvLink() : "Non fourni").append("\n");
                sb.append("\n");
            }
        }
    } else {
        sb.append("Aucune demande d'offre enregistrée\n");
    }
    
    return sb.toString();
}
public double CalculMatchingCV1() {
    double result = 0;
    int age = getAge();
    int min = 18;
    int max = 55;
    System.out.println("Age: "+age);
    // Vérification de l'âge
    if (age >= min && age <= max) {
        result=result+3;
        
    }
    
    // Vérification des postes actifs
    if (ActifPost()) {
        if (appliances != null && !appliances.isEmpty()) {
            for (Appliance appliance : appliances) {
                if ("en_cours".equals(appliance.getTraitement())) {
                    String diploma = appliance.getOffer().getDiploma();
                    String experience = appliance.getOffer().getExperienceLevel();
                    String filiere=appliance.getOffer().getFiliere();
                    System.out.println("ex Requise: "+experience);
                    System.out.println("dipl Requise: "+diploma);
                    int note=0;
                    if (appliance.getAcademicalQualifications() != null && 
                        !appliance.getAcademicalQualifications().isEmpty()) {
                        for (AcademicalQualification aq : appliance.getAcademicalQualifications()) {
                            if (diploma != null && diploma.equals(aq.getDiploma().getName())) {
                                result=result+2;
                                System.out.println("diploma: "+aq.getDiploma().getName());
                              
                            }
                            if (filiere != null) {
                                if(filiere.equals("BACC")){
                                    note=1;
                                }
                                if(filiere.equals("BACC+1")){
                                    note=2;
                                }
                                if(filiere.equals("BACC+2")){
                                    note=3;
                                }
                                if(filiere.equals("Licence")){
                                    note=4;
                                }
                                if(filiere.equals("BACC+5")){
                                    note=6;
                                }
                                if(filiere.equals("MASTER")){
                                    note=7;
                                }
                                if(0<=note-aq.getFiliere().getNiveau()){

                                    result=result+7-(note-aq.getFiliere().getNiveau());
                                }
                            }
                              if (experience != null) {
                                     int exp1=Integer.parseInt(experience);
                                     int exp2=Integer.parseInt(appliance.getExperienceLevel());
                                     if(exp1<exp2 && exp1!=0){
                                         result=result+2;
                                     }
                                     if(exp1==0){
                                        result=result+1;
                                     }
                                        if(exp2<=exp1 && exp1!=0){
                                         result=result+5;
                                     }
                                     if(exp1==exp2){
                                        result=result+4;
                                     }
                                     System.out.println("experience: "+aq.getDiploma().getName());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    return (result / 17.0) * 100;
}

public Boolean ActifPost() {
    if (appliances == null || appliances.isEmpty()) {
        return false;
    }
    
    int count = 0;
    for (Appliance appliance : appliances) {
        if ("en_cours".equals(appliance.getTraitement())) {
            count++;
        }
    }
    
    return count > 0;
}
public double CalculMatchingCV2() {
    double result = 0;
    int age = getAge();
    int min = 18;
    int max = 55;
    
    System.out.println("\n=== CALCUL MATCHING CV ===");
    System.out.println("Âge candidat: " + age);
    
    // Vérification de l'âge
    if (age >= min && age <= max) {
        result = result + 3;
        System.out.println("✓ Âge valide (+3 points)");
    } else {
        System.out.println("✗ Âge non valide");
    }
    
    // Vérification des postes actifs
    if (ActifPost()) {
        System.out.println("✓ Candidature active trouvée");
        
        if (appliances != null && !appliances.isEmpty()) {
            for (Appliance appliance : appliances) {
                if ("en_cours".equals(appliance.getTraitement())) {
                    System.out.println("\n--- Analyse offre ---");
                    
                    String diploma = appliance.getOffer().getDiploma();
                    String experience = appliance.getOffer().getExperienceLevel();
                    String filiere = appliance.getOffer().getFiliere();
                    
                    System.out.println("Diplôme requis: " + diploma);
                    System.out.println("Expérience requise: " + experience);
                    System.out.println("Filière requise: " + filiere);
                    
                    if (appliance.getAcademicalQualifications() != null && 
                        !appliance.getAcademicalQualifications().isEmpty()) {
                        
                        System.out.println("Qualifications académiques trouvées: " + 
                                          appliance.getAcademicalQualifications().size());
                        
                        for (AcademicalQualification aq : appliance.getAcademicalQualifications()) {
                            int count=0;
                            if(count==1){
                                break;
                            }
                            System.out.println("\n  Analyse qualification: " + aq.getId());
                            
                            // VÉRIFICATION NULL CRITIQUE - FILIERE
                            if (aq.getFiliere() == null) {
                                System.out.println("  ✗ Filiere NULL - skip");
                                continue;
                            }
                            
                            // VÉRIFICATION NULL CRITIQUE - NIVEAU
                            Integer niveauFiliere = aq.getFiliere().getNiveau();
                            if (niveauFiliere == null) {
                                System.out.println("  ✗ Niveau NULL - skip");
                                continue;
                            }
                            
                            System.out.println("  Filière candidat: " + aq.getFiliere().getName() + 
                                              " (niveau " + niveauFiliere + ")");
                            
                            // VÉRIFICATION DIPLÔME
                            if (diploma != null && aq.getDiploma() != null && 
                                diploma.equals(aq.getDiploma().getName())) {
                                result = result + 2;
                                System.out.println("  ✓ Diplôme match (+2 points) "+ aq.getDiploma().getName());
                            }
                            
                            // VÉRIFICATION FILIÈRE
                            if (filiere != null) {
                                int note = 0;
                                if (filiere.equals("BACC")) note = 1;
                                else if (filiere.equals("BACC+1")) note = 2;
                                else if (filiere.equals("BACC+2")) note = 3;
                                else if (filiere.equals("Licence")) note = 4;
                                else if (filiere.equals("BACC+5")) note = 6;
                                else if (filiere.equals("MASTER")) note = 7;
                                
                                if (note > 0) {
                                    int equart=note - niveauFiliere;
                                    if (equart > 0) {
                                        result = result + 7;
                                        System.out.println("Superbe niveau(+7) "+aq.getFiliere().getName());
                                    }
                                    if(equart==0)
                                     {
                                       result=result+6;
                                        System.out.println("Moyen niveau(+6) "+aq.getFiliere().getName());
                                    }
                                     if (0 > equart) {
                                        result = result + 4;
                                         System.out.println("DEBUTANT niveau(+4) "+aq.getFiliere().getName());
                                    }
                                }
                            }
                            
                            // VÉRIFICATION EXPÉRIENCE
                            if (experience != null && appliance.getExperienceLevel() != null) {
                                try {
                                    int exp1 = Integer.parseInt(experience);
                                    int exp2 = Integer.parseInt(appliance.getExperienceLevel());
                                    
                                    System.out.println("  Expérience requise: " + exp1 + " ans, candidat: " + exp2 + " ans");
                                    
                                    if (exp1 <= exp2) {
                                        result += 5;
                                        System.out.println("  ✓ Expérience supérieure (+5 points)");
                                    } 
                                    else {
                                        result += 2;
                                        System.out.println("  ✓ Expérience non requise (+2 point)");
                                    }
                                    
                                } catch (NumberFormatException e) {
                                    System.out.println("  ✗ Erreur conversion expérience");
                                }
                            }
                            count++;
                        }
                    } else {
                        System.out.println("  Aucune qualification académique");
                    }
                   
                    break;
                }
            }
        }
    } else {
        System.out.println("✗ Aucune candidature active");
    }
    
    System.out.println("\n=== RÉSULTAT FINAL ===");
    System.out.println("Points totaux: " + result);
    System.out.println("Score: " + (result / 17.0) * 100 + "%");
    System.out.println("=====================\n");
    
    return (result / 17.0) * 100;
}
public double CalculMatchingCV() {
    double result = 0;
    int age = getAge();
    int min = 18;
    int max = 55;
    
    System.out.println("\n=== CALCUL MATCHING CV ===");
    System.out.println("Âge candidat: " + age);
    
    // Vérification de l'âge (3 points max)
    if (age >= min && age <= max) {
        result = result + 3;
        System.out.println("✓ Âge valide (+3 points)");
    } else {
        System.out.println("✗ Âge non valide");
    }
    
    // Vérification des postes actifs
    if (ActifPost()) {
        System.out.println("✓ Candidature active trouvée");
        
        if (appliances != null && !appliances.isEmpty()) {
            for (Appliance appliance : appliances) {
                if ("en_cours".equals(appliance.getTraitement())) {
                    System.out.println("\n--- Analyse offre ---");
                    
                    String diploma = appliance.getOffer().getDiploma();
                    String experience = appliance.getOffer().getExperienceLevel();
                    String filiere = appliance.getOffer().getFiliere();
                    
                    System.out.println("Diplôme requis: " + diploma);
                    System.out.println("Expérience requise: " + experience);
                    System.out.println("Filière requise: " + filiere);
                    
                    // FLAG pour une seule qualification
                    boolean qualificationEvaluee = false;
                    
                    if (appliance.getAcademicalQualifications() != null && 
                        !appliance.getAcademicalQualifications().isEmpty()) {
                        
                        System.out.println("Qualifications académiques trouvées: " + 
                                          appliance.getAcademicalQualifications().size());
                        
                        for (AcademicalQualification aq : appliance.getAcademicalQualifications()) {
                            
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
                            if (experience != null && appliance.getExperienceLevel() != null) {
                                try {
                                    int expRequise = Integer.parseInt(experience);
                                    int expCandidat = Integer.parseInt(appliance.getExperienceLevel());
                                    
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
                   
                    break; // ⚠️ UNE SEULE OFFRE EN COURS
                }
            }
        }
    } else {
        System.out.println("✗ Aucune candidature active");
    }
    
    // ⚠️ LIMITER À 17 POINTS MAXIMUM
    result = Math.min(result, 17.0);
    
    System.out.println("\n=== RÉSULTAT FINAL ===");
    System.out.println("Points totaux: " + result + "/17");
    System.out.println("Score: " + (result / 17.0) * 100 + "%");
    System.out.println("=====================\n");
    
    return (result / 17.0) * 100;
}
}



