package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.Notification;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.service.ApplianceService;
import com.itu.gest_emp.service.NotificationService;
import com.itu.gest_emp.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private  ApplianceService applianceService;

    // Afficher la liste des candidats avec matching
   
  @GetMapping
public String getAllPersons(Model model) {
    List<Person> persons = personService.getAllPersons();
    Map<Long, Integer> matchingScores = new HashMap<>();
    Map<Long, Integer> ages = new HashMap<>();
    
    
    for (Person person : persons) {
        // Calcul du score
        double score = person.CalculMatchingCV();
        matchingScores.put(person.getId(), (int) score);
        
        // Calcul de l'âge
        if (person.getNaissance() != null) {
            int age = Period.between(person.getNaissance(), LocalDate.now()).getYears();
            ages.put(person.getId(), age);
        }
    }
    
    model.addAttribute("persons", persons);
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("ages", ages);
    return "persons-list";
}

    // Filtrer les candidats


   // Créer un nouveau candidat - Formulaire
    @GetMapping("/verification")
    public String notifier(Model model){
        List<Person> persons = personService.getAllPersons();
    Map<Long, Integer> matchingScores = new HashMap<>();
    Map<Long, Integer> ages = new HashMap<>();

    
    
    for (Person person : persons) {
        // Calcul du score
        double score = person.CalculMatchingCV();
          matchingScores.put(person.getId(), (int) score);
        
        // Calcul de l'âge
        if (person.getNaissance() != null) {
            int age = Period.between(person.getNaissance(), LocalDate.now()).getYears();
            ages.put(person.getId(), age);
        }
        if(score>50.00){
            notificationService.createNotification(person,"votre CV a éte préselectionné,cliquez pour faire le quiz", null);
            for (Appliance ap2 : person.getAppliances()) {
                if(ap2.getTraitement().equals("en_cours")){
                    ap2.setTraitement("quiz");
                    applianceService.saveAppliance(ap2);
                  
                }
            }
        }else{
            notificationService.createNotification(person,"desolée votre competence n'as pas la profil requise", null);
        }
    }
    System.out.println("okay");
        model.addAttribute("persons", persons);
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("ages", ages);
    return "persons-list";
    

    }

    // Créer un nouveau candidat - Formulaire
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("person", new Person());
        return "person-form";
    }

    // Traiter la création d'un candidat
    @PostMapping("/create")
    public String createPerson(@ModelAttribute Person person) {
        personService.createPerson(person);
        return "redirect:/persons";
    }

    // API REST - Récupérer tous les candidats avec scores
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllPersonsApi() {
        try {
            List<Person> persons = personService.getAllPersons();
            if (persons.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            // Calcul des scores de matching
            Map<Long, Integer> matchingScores = new HashMap<>();
            for (Person person : persons) {
            
                double score = person.CalculMatchingCV();
                matchingScores.put(person.getId(), (int) score);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("persons", persons);
            response.put("matchingScores", matchingScores);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Créer un candidat
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Person> createPersonApi(@RequestBody Person person) {
        try {
            Person newPerson = personService.createPerson(person);
            return new ResponseEntity<>(newPerson, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   

  

    // API REST - Supprimer un candidat
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<HttpStatus> deletePersonApi(@PathVariable("id") Long id) {
        try {
            personService.deletePerson(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/test-matching/{personId}")
public ResponseEntity<Map<String, Object>> testMatchingCV(@PathVariable Long personId) {
    Optional<Person> personOpt = personService.getPersonById(personId);
    
    if (!personOpt.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erreur", "Personne non trouvée"));
    }

    Person person = personOpt.get();
    Map<String, Object> result = new HashMap<>();
    
    // Informations de base
    result.put("idPersonne", person.getId());
    result.put("nomComplet", person.getFullName());
    result.put("age", person.getAge());
    
    // Détails du calcul de matching
    Map<String, Object> detailsMatching = new HashMap<>();
    double scoreTotal = 0;
    
    // 1. Test de l'âge (1 point)
    int age = person.getAge();
    boolean ageValide = age >= 18 && age <= 55;
    detailsMatching.put("age", age);
    detailsMatching.put("ageValide", ageValide);
    detailsMatching.put("scoreAge", ageValide ? 1 : 0);
    scoreTotal += ageValide ? 1 : 0;
    
    // 2. Test des postes actifs
    boolean aPostesActifs = person.ActifPost();
    detailsMatching.put("aPostesActifs", aPostesActifs);
    
    // 3. Test des candidatures et matching
    List<Map<String, Object>> detailsCandidatures = new ArrayList<>();
    int correspondancesDiplome = 0;
    int correspondancesExperience = 0;
    
    if (person.getAppliances() != null) {
        for (int i = 0; i < person.getAppliances().size(); i++) {
            var candidature = person.getAppliances().get(i);
            Map<String, Object> detailsCandidature = new HashMap<>();
            
            detailsCandidature.put("indexCandidature", i);
            detailsCandidature.put("statut", candidature.getTraitement());
            detailsCandidature.put("estEnCours", "en_cours".equals(candidature.getTraitement()));
            
            // Expérience du candidat pour cette candidature
            detailsCandidature.put("experienceCandidat", candidature.getExperienceLevel());
            
            if (candidature.getOffer() != null) {
                var offre = candidature.getOffer();
                detailsCandidature.put("posteOffre", offre.getPost() != null ? offre.getPost().getName() : "Non spécifié");
                detailsCandidature.put("diplomeRequise", offre.getDiploma());
                detailsCandidature.put("experienceRequise", offre.getExperienceLevel());
                
                // Vérification matching diplôme
                boolean correspondanceDiplome = false;
                if (offre.getDiploma() != null && candidature.getAcademicalQualifications() != null) {
                    for (var aq : candidature.getAcademicalQualifications()) {
                        if (aq.getDiploma() != null && 
                            offre.getDiploma().equals(aq.getDiploma().getName())) {
                            correspondanceDiplome = true;
                            correspondancesDiplome++;
                            break;
                        }
                    }
                }
                detailsCandidature.put("correspondanceDiplome", correspondanceDiplome);
                
                // Vérification matching expérience
                boolean correspondanceExperience = false;
                if (offre.getExperienceLevel() != null && 
                    offre.getExperienceLevel().equals(candidature.getExperienceLevel())) {
                    correspondanceExperience = true;
                    correspondancesExperience++;
                }
                detailsCandidature.put("correspondanceExperience", correspondanceExperience);
                
                // Détails de l'expérience
                detailsCandidature.put("comparaisonExperience", 
                    "Requise: " + offre.getExperienceLevel() + 
                    " vs Candidat: " + candidature.getExperienceLevel());
            }
            
            detailsCandidatures.add(detailsCandidature);
        }
    }
    
    detailsMatching.put("candidatures", detailsCandidatures);
    detailsMatching.put("correspondancesDiplome", correspondancesDiplome);
    detailsMatching.put("correspondancesExperience", correspondancesExperience);
    
    // Calcul du score total
    scoreTotal += correspondancesDiplome;
    scoreTotal += correspondancesExperience;
    
    double scoreFinal = (scoreTotal / 3.0) * 100;
    detailsMatching.put("scoreBrut", scoreTotal);
    detailsMatching.put("scoreFinal", scoreFinal);
    
    result.put("detailsMatching", detailsMatching);
    result.put("scoreMatchingFinal", scoreFinal);
    
    return ResponseEntity.ok(result);
}

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}