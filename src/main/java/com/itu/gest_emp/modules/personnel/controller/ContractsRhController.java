// ==================== CONTROLLER ====================

// ContractsRhController.java
package com.itu.gest_emp.modules.personnel.controller;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/personnel/contracts")
@RequiredArgsConstructor
public class ContractsRhController {

    private final ContractsRhService contractsRhService;
    private final PersonnelRhService personnelRhService;

    /**
     * Afficher la page de gestion des contrats pour un employé
     */
    @GetMapping("/manage")
    public String manageContracts(@RequestParam Long personnelId, Model model) {
        // Charger les informations de l'employé
        PersonnelRh personnel = personnelRhService.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        // Charger tous les contrats de l'employé
        List<ContractsRh> contracts = contractsRhService.findByPersonnelId(personnelId);

        // Créer un nouveau contrat vide pour le formulaire
        ContractsRh newContract = new ContractsRh();
        newContract.setPersonnel(personnel);

        model.addAttribute("personnel", personnel);
        model.addAttribute("contracts", contracts);
        model.addAttribute("newContract", newContract);

        return "modules/personnel/contracts-manage";
    }

    /**
     * Créer un nouveau contrat
     */
    @PostMapping("/create")
    public String createContract(@ModelAttribute ContractsRh contract,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {
        try {
            // Associer le personnel au contrat
            PersonnelRh personnel = personnelRhService.findById(personnelId)
                    .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
            contract.setPersonnel(personnel);

            // Créer le contrat
            contractsRhService.create(contract);

            redirectAttributes.addFlashAttribute("successMessage", "Contrat créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la création du contrat");
        }

        return "redirect:/personnel/contracts/manage?personnelId=" + personnelId;
    }

    /**
     * Valider ou refuser la période d'essai
     */
    @PostMapping("/{id}/valider-essai")
    public String validerPeriodeEssai(@PathVariable Long id,
            @RequestParam boolean valide,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {
        try {
            contractsRhService.validerPeriodeEssai(id, valide);

            String message = valide ? "Période d'essai validée avec succès !"
                    : "Période d'essai refusée. Le contrat est terminé.";
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la validation");
        }

        return "redirect:/personnel/contracts/manage?personnelId=" + personnelId;
    }

    /**
     * Afficher le formulaire de renouvellement de contrat
     */
    @GetMapping("/{id}/renouveler")
    public String showRenewForm(@PathVariable Long id, Model model) {
        ContractsRh oldContract = contractsRhService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        // Préparer le nouveau contrat avec des valeurs par défaut
        ContractsRh newContract = new ContractsRh();
        newContract.setPersonnel(oldContract.getPersonnel());
        newContract.setTypeContrat(oldContract.getTypeContrat());
        newContract.setSalaireBase(oldContract.getSalaireBase());
        newContract.setDateDebut(LocalDate.now());

        model.addAttribute("oldContract", oldContract);
        model.addAttribute("newContract", newContract);
        model.addAttribute("personnel", oldContract.getPersonnel());

        return "modules/personnel/contract-renew";
    }

    /**
     * Renouveler un contrat
     */
    @PostMapping("/{id}/renouveler")
    public String renewContract(@PathVariable Long id,
            @ModelAttribute ContractsRh newContract,
            RedirectAttributes redirectAttributes) {
        try {
            contractsRhService.renouvelerContrat(id, newContract);
            redirectAttributes.addFlashAttribute("successMessage", "Contrat renouvelé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du renouvellement");
        }

        Long personnelId = newContract.getPersonnel().getId();
        return "redirect:/personnel/contracts/manage?personnelId=" + personnelId;
    }

    /**
     * Supprimer un contrat
     */
    @PostMapping("/{id}/delete")
    public String deleteContract(@PathVariable Long id,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {
        try {
            contractsRhService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Contrat supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression");
        }

        return "redirect:/personnel/contracts/manage?personnelId=" + personnelId;
    }

    /**
     * Voir les contrats expirant bientôt
     */
    @GetMapping("/expirant")
    public ResponseEntity<List<ContractsRh>> contractsExpirant(Model model) {
        LocalDate dateLimit = LocalDate.now().plusMonths(1); // Contrats expirant dans 1 mois
        List<ContractsRh> contractsExpirant = contractsRhService.findContractsExpirant(dateLimit);

        model.addAttribute("contractsExpirant", contractsExpirant);

        model.addAttribute("dateLimit", dateLimit);

        return ResponseEntity.ok(contractsExpirant);
    }
}
