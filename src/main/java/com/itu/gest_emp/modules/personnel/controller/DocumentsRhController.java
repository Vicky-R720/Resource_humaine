// DocumentsRhController.java
package com.itu.gest_emp.modules.personnel.controller;

import com.itu.gest_emp.modules.personnel.model.DocumentsRh;
import com.itu.gest_emp.modules.personnel.service.DocumentsRhService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentsRhController {

    private final DocumentsRhService documentsRhService;

    @GetMapping
    public ResponseEntity<List<DocumentsRh>> getAllDocuments() {
        return ResponseEntity.ok(documentsRhService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentsRh> getDocumentById(@PathVariable Long id) {
        return documentsRhService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personnel/{personnelId}")
    public ResponseEntity<List<DocumentsRh>> getDocumentsByPersonnelId(@PathVariable Long personnelId) {
        return ResponseEntity.ok(documentsRhService.findByPersonnelId(personnelId));
    }

    @GetMapping("/type/{typeDocument}")
    public ResponseEntity<List<DocumentsRh>> getDocumentsByType(@PathVariable String typeDocument) {
        return ResponseEntity.ok(documentsRhService.findByTypeDocument(typeDocument));
    }

    @GetMapping("/expirant")
    public ResponseEntity<List<DocumentsRh>> getDocumentsExpirant(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateLimit) {
        return ResponseEntity.ok(documentsRhService.findDocumentsExpirant(dateLimit));
    }

    @GetMapping("/non-verifies")
    public ResponseEntity<List<DocumentsRh>> getDocumentsNonVerifies() {
        return ResponseEntity.ok(documentsRhService.findDocumentsNonVerifies());
    }

    @PostMapping
    public ResponseEntity<DocumentsRh> createDocument(@RequestBody DocumentsRh document) {
        DocumentsRh created = documentsRhService.save(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentsRhService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verifier")
    public ResponseEntity<DocumentsRh> verifierDocument(
            @PathVariable Long id,
            @RequestParam Long verifierId) {
        try {
            DocumentsRh document = documentsRhService.verifierDocument(id, verifierId);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}