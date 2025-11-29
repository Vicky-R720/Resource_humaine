package com.itu.gest_emp.modules.paie.controller.api;

import com.itu.gest_emp.modules.paie.model.Avance;
import com.itu.gest_emp.modules.paie.repository.AvanceRepository;
import com.itu.gest_emp.modules.paie.service.AvanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paie/avances")
public class AvanceApiController {

    @Autowired
    private AvanceService avanceService;

    @Autowired
    private AvanceRepository avanceRepository;

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody AvanceRequestDto dto) {
        try {
            Avance created = avanceService.createDemande(dto.getPersonnelId(), dto.getMontant(), dto.getMotif());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Avance>> list(@RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) String statut) {
        if (personnelId != null && statut != null) {
            // simple filter via repository
            return ResponseEntity.ok(avanceRepository.findByPersonnel_IdAndStatut(personnelId,
                    com.itu.gest_emp.modules.paie.model.AvanceStatus.valueOf(statut)));
        }
        if (personnelId != null) {
            return ResponseEntity.ok(avanceRepository.findByPersonnel_IdAndStatut(personnelId,
                    com.itu.gest_emp.modules.paie.model.AvanceStatus.DEMANDEE));
        }
        return ResponseEntity.ok(avanceRepository.findAll());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestBody ApproveDto dto) {
        try {
            Avance saved = avanceService.approve(id, dto.getApprobateurUtilisateurId(), dto.getCommentaire());
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody ApproveDto dto) {
        try {
            Avance saved = avanceService.reject(id, dto.getApprobateurUtilisateurId(), dto.getCommentaire());
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
