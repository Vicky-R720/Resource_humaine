package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.PersonService;
import com.itu.gest_emp.service.ApplianceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PersonService personService;

    @Autowired
    private ApplianceService applianceService;

    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/candidates";
    }

    @GetMapping("/candidates")
    public String showCandidates(@RequestParam(value = "search", required = false) String searchTerm,
            Model model) {
        List<Person> candidates;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            candidates = personService.searchByFullName(searchTerm);
        } else {
            candidates = personService.getAllPersonsSorted();
        }

        // Compter les candidats avec au moins une candidature
        long candidatesWithAppliances = candidates.stream()
                .filter(c -> c.getAppliances() != null && !c.getAppliances().isEmpty())
                .count();

        model.addAttribute("candidates", candidates);
        model.addAttribute("candidatesWithAppliances", candidatesWithAppliances);

        return "admin-candidates";
    }

    @GetMapping("/candidate-detail/{id}")
    public String showCandidateDetail(@PathVariable("id") Long id, Model model) {
        Optional<Person> candidateOpt = personService.getPersonById(id);

        if (candidateOpt.isEmpty()) {
            return "redirect:/admin/candidates?error=candidateNotFound";
        }

        Person candidate = candidateOpt.get();
        List<Appliance> appliances = applianceService.getAppliancesByPersonId(id);

        // Date de la dernière candidature
        String lastApplianceDate = "Aucune";
        if (!appliances.isEmpty()) {
            lastApplianceDate = appliances.get(0).getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        model.addAttribute("candidate", candidate);
        model.addAttribute("appliances", appliances);
        model.addAttribute("lastApplianceDate", lastApplianceDate);

        return "admin-candidate-detail";
    }

    // API REST - Récupérer tous les candidats
    @GetMapping("/api/candidates")
    @ResponseBody
    public ResponseEntity<List<Person>> getAllCandidatesApi() {
        try {
            List<Person> candidates = personService.getAllPersonsSorted();
            if (candidates.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(candidates, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Récupérer un candidat par ID
    @GetMapping("/api/candidates/{id}")
    @ResponseBody
    public ResponseEntity<Person> getCandidateByIdApi(@PathVariable("id") Long id) {
        Optional<Person> candidateData = personService.getPersonById(id);
        return candidateData.map(candidate -> new ResponseEntity<>(candidate, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // API REST - Rechercher des candidats
    @GetMapping("/api/candidates/search")
    @ResponseBody
    public ResponseEntity<List<Person>> searchCandidatesApi(@RequestParam String searchTerm) {
        try {
            List<Person> candidates = personService.searchByFullName(searchTerm);
            return new ResponseEntity<>(candidates, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Récupérer les candidatures d'un candidat
    @GetMapping("/api/candidates/{id}/appliances")
    @ResponseBody
    public ResponseEntity<List<Appliance>> getCandidateAppliancesApi(@PathVariable("id") Long id) {
        try {
            List<Appliance> appliances = applianceService.getAppliancesByPersonId(id);
            return new ResponseEntity<>(appliances, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Supprimer un candidat
    @DeleteMapping("/api/candidates/{id}")
    @ResponseBody
    public ResponseEntity<HttpStatus> deleteCandidateApi(@PathVariable("id") Long id) {
        try {
            personService.deletePerson(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/interviews")
    public String showInterviews() {
        return "admin-interviews";
    }

    @GetMapping("/results")
    public String showResults() {
        return "admin-results";
    }

    @GetMapping("/publish")
    public String showPublish() {
        return "admin-publish";
    }

    // Méthode pour tester l'affichage des informations complètes d'une personne
    // (ID=1)
    @GetMapping("/test/person-info")
    @ResponseBody
    public String testPersonInfo() {
        Optional<Person> personOpt = personService.getPersonById(1L);

        if (personOpt.isEmpty()) {
            String errorMessage = "Personne avec ID=1 non trouvée";
            System.out.println(errorMessage);
            return errorMessage;
        }

        Person person = personOpt.get();

        try {
            // Utiliser la méthode getFullInformation() de la classe Person
            String fullInfo = person.getFullInformation();

            // Afficher dans la console pour test
            System.out.println("=== INFORMATIONS COMPLÈTES PERSONNE ID=1 ===");
            System.out.println(fullInfo);
            System.out.println("============================================");

            return "<pre>" + fullInfo.replace("\n", "<br>") + "</pre>";

        } catch (Exception e) {
            String errorMessage = "Erreur lors de la récupération des informations: " + e.getMessage();
            System.out.println(errorMessage);
            e.printStackTrace();
            return errorMessage;
        }
    }

}
