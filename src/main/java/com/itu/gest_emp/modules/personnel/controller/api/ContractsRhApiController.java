// ==================== API CONTROLLER ====================
// Path: com/itu/gest_emp/modules/personnel/controller/api/ContractsRhApiController.java

package com.itu.gest_emp.modules.personnel.controller.api;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.RuptureContratService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/personnel/contracts")
@RequiredArgsConstructor
public class ContractsRhApiController {

    private final ContractsRhService contractsRhService;
    private final RuptureContratService ruptureContratService;

    /**
     * Récupérer la liste des contrats d'un employé
     */
    @GetMapping("/personnel/{personnelId}")
    public ResponseEntity<List<ContractsRh>> getContractsByPersonnel(@PathVariable Long personnelId) {
        List<ContractsRh> contracts = contractsRhService.findByPersonnelId(personnelId);
        return ResponseEntity.ok(contracts);
    }

    /**
     * Récupérer un contrat par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContractsRh> getContractById(@PathVariable Long id) {
        return contractsRhService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouveau contrat
     */
    @PostMapping
    public ResponseEntity<ContractsRh> createContract(@RequestBody ContractsRh contract) {
        ContractsRh created = contractsRhService.create(contract);
        return ResponseEntity.ok(created);
    }

    /**
     * Mettre à jour un contrat
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContractsRh> updateContract(
            @PathVariable Long id,
            @RequestBody ContractsRh contract) {
        contract.setId(id);
        ContractsRh updated = contractsRhService.update(id, contract);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprimer un contrat
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractsRhService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Valider ou refuser la période d'essai
     */
    @PatchMapping("/{id}/valider-essai")
    public ResponseEntity<ContractsRh> validerPeriodeEssai(
            @PathVariable Long id,
            @RequestParam boolean valide) {
        ContractsRh contract = contractsRhService.validerPeriodeEssai(id, valide);
        return ResponseEntity.ok(contract);
    }

    /**
     * Renouveler un contrat
     */
    @PostMapping("/{id}/renouveler")
    public ResponseEntity<ContractsRh> renewContract(
            @PathVariable Long id,
            @RequestBody ContractsRh newContract) {
        ContractsRh renewed = contractsRhService.prolongerOuTransformerEnCDI(id, null, null);
        return ResponseEntity.ok(renewed);
    }

    /**
     * Récupérer les contrats expirant bientôt
     */
    @GetMapping("/expirant")
    public ResponseEntity<List<ContractsRh>> getContractsExpirant(
            @RequestParam(required = false, defaultValue = "1") int months) {
        LocalDate dateLimit = LocalDate.now().plusMonths(months);
        List<ContractsRh> contractsExpirant = contractsRhService.findContractsExpirant(dateLimit);
        return ResponseEntity.ok(contractsExpirant);
    }

    /**
     * Valider ou refuser une demande de rupture
     */
    @PostMapping("/rupture/{demandeId}/valider")
    public ResponseEntity<Void> validerRupture(
            @PathVariable Long demandeId,
            @RequestParam Long validateurId,
            @RequestParam boolean accepte,
            @RequestParam(required = false) String commentaire) {

        ruptureContratService.validerDemande(
                demandeId,
                validateurId,
                accepte,
                commentaire);

        return ResponseEntity.ok().build();
    }
}