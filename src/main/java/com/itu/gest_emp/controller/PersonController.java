package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.Notification;
import com.itu.gest_emp.model.Offer;
import com.itu.gest_emp.model.Person;
import com.itu.gest_emp.model.Personnel;
import com.itu.gest_emp.service.ApplianceService;
import com.itu.gest_emp.service.NotificationService;
import com.itu.gest_emp.service.OfferService;
import com.itu.gest_emp.service.PdfService;
import com.itu.gest_emp.service.PersonService;
import com.itu.gest_emp.service.PersonnelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.net.http.HttpHeaders;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private  ApplianceService applianceService;

    @Autowired
    private  OfferService offreService;

    @Autowired
    private  PersonnelService personnelService;

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

@GetMapping("/appliances1")
public String getAllAppliances(Model model) {
    List<Appliance> appliancesi = applianceService.getAllAppliances();
    Map<Long, Integer> matchingScores = new HashMap<>();
    Map<Long, String> matchingEvaluations = new HashMap<>();
    
    // Filtrer seulement les "en_cours"
    List<Appliance> appliances = Appliance.getApplianceswithTraitement(appliancesi, "en_cours");
    
    for (Appliance appliance : appliances) {
        double score = appliance.matchingCV();
        matchingScores.put(appliance.getId(), (int) score);
        matchingEvaluations.put(appliance.getId(), appliance.getMatchingEvaluation());
    }
    
    model.addAttribute("appliances", appliances);
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("matchingEvaluations", matchingEvaluations);
    return "appliances-list";
}
@GetMapping("/entretienPlanning")
public String getAllAppliancesEnt(Model model) {
    List<Appliance> appliancesi = applianceService.getAllAppliances();
    Map<Long, Integer> matchingScores = new HashMap<>();
    Map<Long, String> matchingEvaluations = new HashMap<>();
    
    // Filtrer seulement les "en_cours"
    List<Appliance> appliances = Appliance.getApplianceswithTraitement(appliancesi, "entretien");
    model.addAttribute("appliances", appliances);
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("matchingEvaluations", matchingEvaluations);
    return "appliances-attente";
}

@GetMapping("/verificationApp")
public String verifierAppliance(Model model) {
    List<Appliance> allAppliances = applianceService.getAllAppliances();
    
    // Filtrer seulement les "en_cours" pour le traitement
    List<Appliance> appliancesToProcess = Appliance.getApplianceswithTraitement(allAppliances, "en_cours");
    
    for (Appliance appliance : appliancesToProcess) {
        double score = appliance.matchingCV();
        
        if (score > 50.00) {
            // Notification pour le candidat
            notificationService.createNotification(
                appliance.getPerson(),
                "QUIZ_READY:" + // Préfixe pour identifier le type
                (appliance.getOffer() != null && appliance.getOffer().getPost() != null ? 
                 appliance.getOffer().getPost().getName() : "Poste"),
                appliance
            );
            // Mettre à jour le statut de l'appliance
            appliance.setTraitement("quiz");
            applianceService.saveAppliance(appliance);
            
        } else {
            // Notification de refus
            notificationService.createNotification(
                appliance.getPerson(),
                "Désolé, votre candidature pour le poste '" + 
                (appliance.getOffer() != null && appliance.getOffer().getPost() != null ? 
                 appliance.getOffer().getPost().getName() : "Poste") + 
                "' n'a pas été retenue. Votre profil ne correspond pas aux exigences requises."
            ,appliance);
            
            // Mettre à jour le statut en "refusé"
            appliance.setTraitement("refuse");
            applianceService.saveAppliance(appliance);
        }
    }
    
    System.out.println("Vérification des candidatures terminée");
    
    // REDIRIGER vers la page /appliances pour voir le résultat
    return "redirect:/persons/appliances";
}

@GetMapping("/verificationPlanning")
public String verifierPlanning(Model model) {
    List<Appliance> allAppliances = applianceService.getAllAppliances();
    
    // Filtrer seulement les "en_cours" pour le traitement
    List<Appliance> appliancesToProcess = Appliance.getApplianceswithTraitement(allAppliances, "entretien");
    
    for (Appliance appliance : appliancesToProcess) {
    
            // Notification pour le candidat
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            appliance.setPlanning();
            String dateE=appliance.getPlanning().format(formatter);
            notificationService.createNotification(
                appliance.getPerson(),
                "voici votre date d'entretien prevu chez nous à"+dateE,
                appliance
            );
            // Mettre à jour le statut de l'appliance
            appliance.setTraitement("entretienfinal");
            applianceService.saveAppliance(appliance);
    
    System.out.println("Vérification des candidatures terminée");
    
    // REDIRIGER vers la page /appliances pour voir le résultat
}
return "redirect:/persons/entretienPlanning";


}
@GetMapping("/evaluer")
public String Evaluer(Model model) {
    List<Appliance> allAppliances = applianceService.getAllAppliances();
    
    // Filtrer seulement les "en_cours" pour le traitement
    List<Appliance> appliancesToProcess = Appliance.getApplianceswithTraitement(allAppliances, "entretienfinal");
    
    model.addAttribute("appliances", appliancesToProcess);

    
    // REDIRIGER vers la page /appliances pour voir le résultat

return "noter";

}

// Ajoutez ces méthodes dans PersonController

@GetMapping("/noter/{applianceId}")
public String showNoteForm(@PathVariable Long applianceId, Model model) {
    Optional<Appliance> applianceOpt = applianceService.getApplianceById(applianceId);
    
    if (!applianceOpt.isPresent()) {
        // Rediriger vers la page d'évaluation si l'appliance n'existe pas
        return "redirect:/persons/evaluer";
    }
    
    Appliance appliance = applianceOpt.get();
    model.addAttribute("appliance", appliance);
    return "noter-form";
}

@PostMapping("/noter")
public String saveNote(@RequestParam Long applianceId, 
                      @RequestParam Integer note, 
                      @RequestParam(required = false) String commentaire,
                      Model model) {
    try {
        Optional<Appliance> applianceOpt = applianceService.getApplianceById(applianceId);
        
        if (!applianceOpt.isPresent()) {
            model.addAttribute("error", "Candidature non trouvée");
            return "redirect:/persons/evaluer";
        }
        
        Appliance appliance = applianceOpt.get();
        
        // Sauvegarder la note (vous devrez ajouter ces champs à votre modèle Appliance)
       
        appliance.setNoteEval(note+ appliance.getNoteQCM());
        appliance.setTraitement("note"); // Nouveau statut pour les candidatures notées
        
        applianceService.saveAppliance(appliance);
        
        // Créer une notification pour le candidat
        notificationService.createNotification(
            appliance.getPerson(),
            "Votre entretien pour le poste '" + 
            (appliance.getOffer() != null && appliance.getOffer().getPost() != null ? 
             appliance.getOffer().getPost().getName() : "Poste") + 
            "' a été évalué. Vous recevrez bientôt les résultats.",
            appliance
        );
        
        model.addAttribute("success", "Note sauvegardée avec succès !");
        return "redirect:/persons/evaluer";
        
    } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la sauvegarde : " + e.getMessage());
        return "redirect:/persons/evaluer";
    }
}

@GetMapping("/notes")
public String getAllNotes(Model model) {
    List<Appliance> allAppliances = applianceService.getAllAppliances();
    Map<Long, Integer> matchingScores = new HashMap<>();
    Map<Long, Integer> QCMScores = new HashMap<>();
    Map<Long, Integer> EScores = new HashMap<>();
    Map<Long, String> matchingEvaluations = new HashMap<>();
    // Filtrer seulement les candidatures notées
    List<Appliance> notedAppliances = Appliance.getApplianceswithTraitement(allAppliances, "note");
    for (Appliance appliance : notedAppliances) {
        double score = (appliance.getNoteEval()+appliance.getNoteQCM())*5;
        matchingScores.put(appliance.getId(), (int) score);
        QCMScores.put(appliance.getId(), (int) appliance.getNoteQCM());
        EScores.put(appliance.getId(), (int) appliance.getNoteEval());
        System.out.println("Eval :"+appliance.getNoteEval());
        System.out.println("QCM :"+appliance.getNoteQCM());
        
    }
    
    model.addAttribute("appliances", notedAppliances);
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("qcmscore",QCMScores );
    model.addAttribute("escore",EScores );
    model.addAttribute("matchingScores", matchingScores);
    model.addAttribute("matchingEvaluations", matchingEvaluations);

    return "note-list";
}

@PostMapping("/noter/{applianceId}")
public String saveNot(@PathVariable Long applianceId, 
                      @RequestParam Integer note, 
                      @RequestParam(required = false) String commentaire,
                      Model model) {
    try {
        Optional<Appliance> applianceOpt = applianceService.getApplianceById(applianceId);
        
        if (!applianceOpt.isPresent()) {
            model.addAttribute("error", "Candidature non trouvée");
            return "redirect:/persons/evaluer";
        }
        
        Appliance appliance = applianceOpt.get();
        
        // Validation de la note
        if (note < 1 || note > 10) {
            model.addAttribute("error", "La note doit être entre 1 et 10");
            return "redirect:/persons/noter/" + applianceId;
        }
        
        // Sauvegarder la note et le commentaire
        appliance.setNoteEval(note + appliance.getNoteQCM());
        appliance.setTraitement("note");
        
        applianceService.saveAppliance(appliance);
        
        // Notification pour le candidat
        String posteName = appliance.getOffer() != null && appliance.getOffer().getPost() != null 
            ? appliance.getOffer().getPost().getName() 
            : "Poste";
            
        notificationService.createNotification(
            appliance.getPerson(),
            "Votre entretien pour le poste '" + posteName + 
            "' a été évalué avec une note de " + note + "/10. " +
            (commentaire != null ? "Commentaire: " + commentaire : ""),
            appliance
        );
        
        model.addAttribute("success", "Note sauvegardée avec succès !");
        return "redirect:/persons/notes";
        
    } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la sauvegarde : " + e.getMessage());
        return "redirect:/persons/noter/" + applianceId;
    }
}
// API REST pour sauvegarder la note via AJAX
@PostMapping("/api/noter/{applianceId}")
@ResponseBody
public ResponseEntity<Map<String, Object>> saveNoteApi(
        @PathVariable Long applianceId,
        @RequestBody Map<String, Object> noteData) {
    
    try {
        Optional<Appliance> applianceOpt = applianceService.getApplianceById(applianceId);
        
        if (!applianceOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Candidature non trouvée"));
        }
        
        Appliance appliance = applianceOpt.get();
        
        // Récupérer les données
        Integer note = (Integer) noteData.get("note");
        String commentaire = (String) noteData.get("comment");
        
        // Validation
        if (note == null || note < 1 || note > 10) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "La note doit être entre 1 et 10"));
        }
        
        // Sauvegarder la note
        appliance.setNoteEval(note);
        appliance.setTraitement("note");
        
        applianceService.saveAppliance(appliance);
        
        // Notification
        String posteName = appliance.getOffer() != null && appliance.getOffer().getPost() != null 
            ? appliance.getOffer().getPost().getName() 
            : "Poste";
            
        notificationService.createNotification(
            appliance.getPerson(),
            "Votre entretien pour le poste '" + posteName + 
            "' a été évalué avec une note de " + note + "/10. " +
            (commentaire != null ? "Commentaire: " + commentaire : ""),
            appliance
        );
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Note sauvegardée avec succès",
            "applianceId", applianceId
        ));
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
    }
}
@PostMapping("/appliances/{id}/accept")
@ResponseBody
public ResponseEntity<Map<String, Object>> acceptAppliance(@PathVariable Long id) {
    try {
        Optional<Appliance> applianceOpt = applianceService.getApplianceById(id);
        
        if (!applianceOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Candidature non trouvée"));
        }
        
        Appliance appliance = applianceOpt.get();
        appliance.setTraitement("accepte");
        
        // Générer le contrat PDF
        byte[] contratPdf = pdfService.generateContratEssai(appliance);
        applianceService.saveAppliance(appliance);
        
        // Créer le message avec le pattern CONTRAT_LINK:
        String posteName = appliance.getOffer() != null && appliance.getOffer().getPost() != null 
            ? appliance.getOffer().getPost().getName() 
            : "Poste";
            
        String message = "Félicitations ! Votre candidature pour le poste de '" + posteName + 
            "' a été acceptée. CONTRAT_LINK:/persons/appliances/" + appliance.getId() + "/download-contrat";
        
        notificationService.createNotification(
            appliance.getPerson(),
            message,
            appliance
        );
        personnelService.updatePersonnel(new Personnel(appliance.getPerson(),appliance.getOffer().getPost().getName()));
        Offer offre=appliance.getOffer();
        if(offre.getAvailablePlaces()!=0){
            offre.setAvailablePlaces(offre.getAvailablePlaces()-1);
            offreService.createOffer(offre);
        }
        
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Candidature acceptée"));
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
    }
}
// Refuser une candidature
@PostMapping("/appliances/{id}/reject")
@ResponseBody
public ResponseEntity<Map<String, Object>> rejectAppliance(@PathVariable Long id) {
    try {
        Optional<Appliance> applianceOpt = applianceService.getApplianceById(id);
        
        if (!applianceOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Candidature non trouvée"));
        }
        
        Appliance appliance = applianceOpt.get();
        appliance.setTraitement("refuse");
        applianceService.saveAppliance(appliance);
        
        // Notification au candidat
        notificationService.createNotification(
            appliance.getPerson(),
            "Votre candidature pour le poste '" + 
            (appliance.getOffer() != null && appliance.getOffer().getPost() != null ? 
             appliance.getOffer().getPost().getName() : "Poste") + 
            "' n'a malheureusement pas été retenue.",
            appliance
        );
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Candidature refusée avec succès"
        ));
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
    }
}
    // ✅ MÉTHODE CORRIGÉE - Version simple
    @GetMapping("/appliances/{id}/download-contrat")
    public ResponseEntity<byte[]> downloadContrat(@PathVariable Long id) {
        try {
            Optional<Appliance> applianceOpt = applianceService.getApplianceById(id);
            
            if (!applianceOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Génération à la volée
            byte[] pdf = pdfService.generateContratEssai(applianceOpt.get());
            
            // Headers simples sans HttpHeaders
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=contrat_essai_" + id + ".pdf")
                .body(pdf);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    @GetMapping("/appliances")
    public String getAllAppliances(
            @RequestParam(value = "ageRange", required = false) String ageRange,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "diploma", required = false) String diploma,
            @RequestParam(value = "scoreRange", required = false) String scoreRange,
            @RequestParam(value = "offer", required = false) String offer,
            @RequestParam(value = "company", required = false) String company,
            Model model) {
        
        // Récupérer toutes les candidatures
        List<Appliance> appliancesi = applianceService.getAllAppliances();
        
        // Filtrer seulement les "en_cours"
        List<Appliance> appliances = Appliance.getApplianceswithTraitement(appliancesi, "en_cours");
        
        // Appliquer les filtres si des paramètres sont fournis
        if (hasFilters(ageRange, address, diploma, scoreRange, offer, company)) {
            appliances = filterAppliances(appliances, ageRange, address, diploma, 
                                        scoreRange, offer, company);
        }
        
        // Calculer les scores de matching
        Map<Long, Integer> matchingScores = new HashMap<>();
        Map<Long, String> matchingEvaluations = new HashMap<>();
        
        for (Appliance appliance : appliances) {
            double score = appliance.matchingCV();
            matchingScores.put(appliance.getId(), (int) score);
            matchingEvaluations.put(appliance.getId(), getMatchingEvaluation((int) score));
        }
        
        model.addAttribute("appliances", appliances);
        model.addAttribute("matchingScores", matchingScores);
        model.addAttribute("matchingEvaluations", matchingEvaluations);
        
        return "appliances-list";
    }
    
    /**
     * API pour filtrage AJAX
     */
    @GetMapping("/appliances/filter")
    @ResponseBody
    public String filterAppliancesAjax(
            @RequestParam(value = "ageRange", required = false) String ageRange,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "diploma", required = false) String diploma,
            @RequestParam(value = "scoreRange", required = false) String scoreRange,
            @RequestParam(value = "offer", required = false) String offer,
            @RequestParam(value = "company", required = false) String company,
            Model model) {
        
        // Utiliser la même logique que getAllAppliances
        return getAllAppliances(ageRange, address, diploma, scoreRange, offer, company, model);
    }
    
    /**
     * Vérifier si au moins un filtre est fourni
     */
    private boolean hasFilters(String ageRange, String address, String diploma, 
                              String scoreRange, String offer, String company) {
        return (ageRange != null && !ageRange.trim().isEmpty()) ||
               (address != null && !address.trim().isEmpty()) ||
               (diploma != null && !diploma.trim().isEmpty()) ||
               (scoreRange != null && !scoreRange.trim().isEmpty()) ||
               (offer != null && !offer.trim().isEmpty()) ||
               (company != null && !company.trim().isEmpty());
    }
    
    /**
     * Appliquer les filtres sur la liste des candidatures
     */
    private List<Appliance> filterAppliances(List<Appliance> appliances, 
                                           String ageRange, String address, String diploma,
                                           String scoreRange, String offer, String company) {
        
        return appliances.stream()
            .filter(appliance -> {
                Person person = appliance.getPerson();
                
                // Filtre par âge
                if (ageRange != null && !ageRange.trim().isEmpty()) {
                    int age = person.getAge();
                    if (!matchesAgeRange(age, ageRange)) {
                        return false;
                    }
                }
                
                // Filtre par adresse
                if (address != null && !address.trim().isEmpty()) {
                    String personAddress = person.getAdresse();
                    if (personAddress == null || 
                        !personAddress.toLowerCase().contains(address.toLowerCase())) {
                        return false;
                    }
                }
                
                // Filtre par diplôme
                if (diploma != null && !diploma.trim().isEmpty()) {
                    if (!hasDiploma(appliance, diploma)) {
                        return false;
                    }
                }
                
                // Filtre par score de matching
                if (scoreRange != null && !scoreRange.trim().isEmpty()) {
                    double score = person.CalculMatchingCV();
                    if (!matchesScoreRange(score, scoreRange)) {
                        return false;
                    }
                }
                
                // Filtre par offre/poste
                if (offer != null && !offer.trim().isEmpty()) {
                    String postName = appliance.getOffer() != null && 
                                     appliance.getOffer().getPost() != null ? 
                                     appliance.getOffer().getPost().getName() : "";
                    if (!postName.toLowerCase().contains(offer.toLowerCase())) {
                        return false;
                    }
                }
                
                // Filtre par entreprise
                if (company != null && !company.trim().isEmpty()) {
                    String companyName = appliance.getOffer() != null ? 
                                        appliance.getOffer().getCompanyName() : "";
                    if (companyName == null || 
                        !companyName.toLowerCase().contains(company.toLowerCase())) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Vérifier si l'âge correspond à la tranche sélectionnée
     */
    private boolean matchesAgeRange(int age, String ageRange) {
        switch (ageRange) {
            case "18-25": 
                return age >= 18 && age <= 25;
            case "26-35": 
                return age >= 26 && age <= 35;
            case "36-45": 
                return age >= 36 && age <= 45;
            case "46-55": 
                return age >= 46 && age <= 55;
            case "55+": 
                return age > 55;
            default: 
                return true;
        }
    }
    
    /**
     * Vérifier si la candidature contient le diplôme spécifié
     */
    private boolean hasDiploma(Appliance appliance, String diploma) {
        if (appliance.getAcademicalQualifications() == null || 
            appliance.getAcademicalQualifications().isEmpty()) {
            return false;
        }
        
        return appliance.getAcademicalQualifications().stream()
            .anyMatch(aq -> aq.getDiploma() != null && 
                           diploma.equals(aq.getFiliere().getName()));
    }
    
    /**
     * Vérifier si le score correspond à la plage sélectionnée
     */
    private boolean matchesScoreRange(double score, String scoreRange) {
        switch (scoreRange) {
            case "80-100": 
                return score >= 80 && score <= 100;
            case "60-79": 
                return score >= 60 && score < 80;
            case "40-59": 
                return score >= 40 && score < 60;
            case "0-39": 
                return score >= 0 && score < 40;
            default: 
                return true;
        }
    }
    
    /**
     * Obtenir l'évaluation textuelle du score de matching
     */
    private String getMatchingEvaluation(int score) {
        if (score >= 80) {
            return "Excellent";
        } else if (score >= 60) {
            return "Bon";
        } else if (score >= 40) {
            return "Moyen";
        } else {
            return "Faible";
        }
    }
    
    /**
     * Mettre à jour le statut d'une candidature
     */
    @PostMapping("/appliances/{id}/status")
    @ResponseBody
    public String updateApplianceStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            java.util.Optional<Appliance> applianceOpt = applianceService.getApplianceById(id);
            
            if (applianceOpt.isPresent()) {
                Appliance appliance = applianceOpt.get();
                appliance.setTraitement(status);
                applianceService.saveAppliance(appliance);
                return "success";
            } else {
                return "error: Candidature non trouvée";
            }
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    

    

}