package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.Personnel;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.PersonService;
import com.itu.gest_emp.service.ApplianceService;
import com.itu.gest_emp.service.PersonnelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonnelService personnelService;

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

    @GetMapping("/personnel")
    public String showPersonnel(@RequestParam(value = "search", required = false) String searchTerm,
            Model model) {
        List<Personnel> personnel;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Recherche par nom/prénom
            personnel = personnelService.searchPersonnelByName(searchTerm.trim());
            model.addAttribute("searchTerm", searchTerm);
        } else {
            // Récupérer tout le personnel
            personnel = personnelService.getAllPersonnelSorted();
        }

        // Calculer les statistiques
        long personnelThisMonth = personnelService.countPersonnelHiredThisMonth();

        model.addAttribute("personnel", personnel);
        model.addAttribute("personnelThisMonth", personnelThisMonth);

        return "personnel-list";
    }

    /**
     * Affiche les détails d'un employé
     */
    @GetMapping("/personnel-detail/{id}")
    public String showPersonnelDetail(@PathVariable("id") Long id, Model model) {
        Optional<Personnel> personnelOpt = personnelService.getPersonnelById(id);

        if (personnelOpt.isEmpty()) {
            return "redirect:/admin/personnel?error=personnelNotFound";
        }

        Personnel employe = personnelOpt.get();

        // Calculer l'ancienneté
        String anciennete = personnelService.calculateSeniority(employe.getDateEmbauche());

        // Récupérer l'historique des candidatures de cette personne
        List<Appliance> candidatures = applianceService.getAppliancesByPersonId(employe.getPersonne().getId());

        model.addAttribute("employe", employe);
        model.addAttribute("anciennete", anciennete);
        model.addAttribute("candidatures", candidatures);

        return "personnel-detail";
    }

    // ========== API REST pour le personnel ==========

    /**
     * API - Récupérer tout le personnel
     */
    @GetMapping("/api/personnel")
    @ResponseBody
    public ResponseEntity<List<Personnel>> getAllPersonnelApi() {
        try {
            List<Personnel> personnel = personnelService.getAllPersonnelSorted();
            if (personnel.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(personnel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API - Récupérer un employé par ID
     */
    @GetMapping("/api/personnel/{id}")
    @ResponseBody
    public ResponseEntity<Personnel> getPersonnelByIdApi(@PathVariable("id") Long id) {
        Optional<Personnel> personnelData = personnelService.getPersonnelById(id);
        return personnelData.map(personnel -> new ResponseEntity<>(personnel, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * API - Rechercher du personnel
     */
    @GetMapping("/api/personnel/search")
    @ResponseBody
    public ResponseEntity<List<Personnel>> searchPersonnelApi(@RequestParam String searchTerm) {
        try {
            List<Personnel> personnel = personnelService.searchPersonnelByName(searchTerm);
            return new ResponseEntity<>(personnel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API - Mettre à jour un employé
     */
    @PutMapping("/api/personnel/{id}")
    @ResponseBody
    public ResponseEntity<Personnel> updatePersonnelApi(@PathVariable("id") Long id,
            @RequestParam String poste,
            @RequestParam String contact,
            @RequestParam(required = false) String adresse) {
        try {
            Optional<Personnel> personnelOpt = personnelService.getPersonnelById(id);

            if (personnelOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Personnel personnel = personnelOpt.get();

            // Mettre à jour le poste
            personnel.setPoste(poste);

            // Mettre à jour les informations de la personne
            Person personne = personnel.getPersonne();
            personne.setContact(contact);
            if (adresse != null) {
                personne.setAdresse(adresse);
            }

            Personnel updatedPersonnel = personnelService.updatePersonnel(personnel);
            return new ResponseEntity<>(updatedPersonnel, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API - Supprimer un employé
     */
    @DeleteMapping("/api/personnel/{id}")
    @ResponseBody
    public ResponseEntity<HttpStatus> deletePersonnelApi(@PathVariable("id") Long id) {
        try {
            Optional<Personnel> personnelOpt = personnelService.getPersonnelById(id);

            if (personnelOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            personnelService.deletePersonnel(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Traitement du formulaire de modification depuis la page de détail
     */
    @PostMapping("/api/personnel/{id}")
    public String updatePersonnelFromForm(@PathVariable("id") Long id,
            @RequestParam String poste,
            @RequestParam String contact,
            @RequestParam(required = false) String adresse,
            @RequestParam("_method") String method) {

        if ("PUT".equals(method)) {
            try {
                Optional<Personnel> personnelOpt = personnelService.getPersonnelById(id);

                if (personnelOpt.isPresent()) {
                    Personnel personnel = personnelOpt.get();
                    personnel.setPoste(poste);

                    Person personne = personnel.getPersonne();
                    personne.setContact(contact);
                    if (adresse != null && !adresse.trim().isEmpty()) {
                        personne.setAdresse(adresse);
                    }

                    personnelService.updatePersonnel(personnel);
                    return "redirect:/admin/personnel-detail/" + id + "?success=updated";
                }

            } catch (Exception e) {
                return "redirect:/admin/personnel-detail/" + id + "?error=updateFailed";
            }
        }

        if ("DELETE".equals(method)) {
            try {
                personnelService.deletePersonnel(id);
                return "redirect:/admin/personnel?success=deleted";
            } catch (Exception e) {
                return "redirect:/admin/personnel-detail/" + id + "?error=deleteFailed";
            }
        }

        return "redirect:/admin/personnel-detail/" + id;
    }

}
