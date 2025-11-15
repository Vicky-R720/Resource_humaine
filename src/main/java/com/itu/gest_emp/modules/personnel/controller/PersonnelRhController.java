package com.itu.gest_emp.modules.personnel.controller;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.CareerHistoryService;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.DocumentsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/personnel") // On retire /api pour les pages HTML
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonnelRhController {

    private final PersonnelRhService personnelRhService;
    private final ContractsRhService contratService;
    private final CareerHistoryService historiqueService;
    private final DocumentsRhService documentService;

    // -------------------------------
    // Pages Thymeleaf
    // -------------------------------

    // Liste du personnel
    @GetMapping("/page/index")
    public String index(Model model) {
        List<PersonnelRh> personnels = personnelRhService.findAll();
        model.addAttribute("personnels", personnels);
        return "modules/personnel/personnel-list";
    }

    // Détail d’un personnel
    @GetMapping("/page/detail/{id}")
    public String getPagePersonnelById(@PathVariable Long id, Model model) {
        PersonnelRh personnel = personnelRhService.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        model.addAttribute("personnel", personnel);

        // Récupérer les contrats, documents, historique
        model.addAttribute("contrats", contratService.findByPersonnelId(personnel.getId()));
        model.addAttribute("historiques", historiqueService.findByPersonnelId(personnel.getId()));
        model.addAttribute("documents", documentService.findByPersonnelId(personnel.getId()));

        return "modules/personnel/personnel-detail";

    }

    // -------------------------------
    // API REST
    // -------------------------------

    @GetMapping("/api") // renvoie tous les personnels en JSON
    @ResponseBody
    public List<PersonnelRh> getAllPersonnel() {
        return personnelRhService.findAll();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PersonnelRh> getPersonnelById(@PathVariable Long id) {
        return personnelRhService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/matricule/{matricule}")
    @ResponseBody
    public ResponseEntity<PersonnelRh> getPersonnelByMatricule(@PathVariable String matricule) {
        return personnelRhService.findByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/statut/{statut}")
    @ResponseBody
    public List<PersonnelRh> getPersonnelByStatut(@PathVariable String statut) {
        return personnelRhService.findByStatut(statut);
    }

    @GetMapping("/api/person/{personId}")
    @ResponseBody
    public ResponseEntity<PersonnelRh> getPersonnelByPersonId(@PathVariable Long personId) {
        return personnelRhService.findByPersonId(personId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<PersonnelRh> createPersonnel(@RequestBody PersonnelRh personnelRh) {
        PersonnelRh created = personnelRhService.create(personnelRh);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PersonnelRh> updatePersonnel(
            @PathVariable Long id,
            @RequestBody PersonnelRh personnelRh) {
        try {
            PersonnelRh updated = personnelRhService.update(id, personnelRh);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        personnelRhService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/{id}/terminer")
    @ResponseBody
    public ResponseEntity<Void> terminerContrat(
            @PathVariable Long id,
            @RequestParam String motif) {
        personnelRhService.terminerContrat(id, motif);
        return ResponseEntity.ok().build();
    }
}
