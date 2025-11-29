package com.itu.gest_emp.modules.personnel.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.personnel.util.FileUploadHelper;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.PersonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelRhApiController {

    private final PersonnelRhService personnelRhService;
    private final PersonService personService;

    @GetMapping
    public List<PersonnelRh> getAll() {
        return personnelRhService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonnelRh> getById(@PathVariable Long id) {
        return personnelRhService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<PersonnelRh> getByMatricule(@PathVariable String matricule) {
        return personnelRhService.findByMatricule(matricule)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonnelRh> create(
            @RequestPart("personnel") PersonnelRh personnel,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            System.out.println(personnel.getPerson().getId());
            Optional<PersonnelRh> existing = personnelRhService
                    .findByPersonId(personnel.getPerson().getId());

            if (existing.isPresent()) {
                System.out.println("misy lesyy");
                return ResponseEntity.badRequest().build();
            }

            // upload photo
            String filename = FileUploadHelper.save("upload/photos", photo);
            if (filename != null) {
                Person p = personService.getPersonById(personnel.getPerson().getId())
                        .orElseThrow(() -> new RuntimeException("Person not found"));
                p.setPdp(filename); // FileUploadHelper retourne déjà le chemin complet
                personService.createPerson(p);
            }

            PersonnelRh created = personnelRhService.create(personnel);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            e.printStackTrace(); // pour debug (optionnel)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonnelRh> update(
            @PathVariable Long id,
            @RequestBody PersonnelRh personnel) {

        return ResponseEntity.ok(personnelRhService.update(id, personnel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personnelRhService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/terminate")
    public ResponseEntity<Void> terminate(
            @PathVariable Long id,
            @RequestParam String motif) {
        personnelRhService.terminerContrat(id, motif);
        return ResponseEntity.ok().build();
    }
}
