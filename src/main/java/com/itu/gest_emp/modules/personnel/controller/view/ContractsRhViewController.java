// ==================== VIEW CONTROLLER ====================
// Path: com/itu/gest_emp/modules/personnel/controller/view/ContractsRhViewController.java

package com.itu.gest_emp.modules.personnel.controller.view;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.model.DemandeRuptureContrat;
import com.itu.gest_emp.modules.personnel.service.ContractTypeService;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.personnel.service.RuptureContratService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/personnel/contracts")
@RequiredArgsConstructor
public class ContractsRhViewController {

    private final ContractsRhService contractsRhService;
    private final PersonnelRhService personnelRhService;
    private final RuptureContratService ruptureContratService;
    private final ContractTypeService contractTypeService;

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
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());

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
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
        newContract.setContractType(oldContract.getContractType());
        newContract.setSalaireBase(oldContract.getSalaireBase());
        newContract.setDateDebut(oldContract.getDateFin().plusDays(1));

        model.addAttribute("oldContract", oldContract);
        model.addAttribute("newContract", newContract);
        model.addAttribute("personnel", oldContract.getPersonnel());
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());

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
            LocalDate nouvelleDateFin = newContract.getDateFin();
            BigDecimal salaire = newContract.getSalaireBase();

            contractsRhService.prolongerOuTransformerEnCDI(id, nouvelleDateFin, salaire);

            redirectAttributes.addFlashAttribute("successMessage", "Contrat renouvelé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du renouvellement : " + e.getMessage());
        }

        // Récupérer le personnel à partir de l'ancien contrat
        ContractsRh ancienContrat = contractsRhService.findById(id).orElseThrow();
        Long personnelId = ancienContrat.getPersonnel().getId();

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
     * Afficher la page des contrats expirant bientôt
     */
    @GetMapping("/expirant")
    public String contractsExpirant(Model model) {
        LocalDate dateLimit = LocalDate.now().plusMonths(1); // Contrats expirant dans 1 mois
        List<ContractsRh> contractsExpirant = contractsRhService.findContractsExpirant(dateLimit);

        model.addAttribute("contractsExpirant", contractsExpirant);
        model.addAttribute("dateLimit", dateLimit);

        return "modules/personnel/contracts-expirant";
    }

    /**
     * Afficher le formulaire de demande de rupture
     */
    @GetMapping("/{id}/rupture/demande")
    public String showRuptureDemande(@PathVariable Long id, Model model) {
        ContractsRh contract = contractsRhService.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        if (!contract.getStatut().equals("actif")) {
            throw new IllegalStateException("Ce contrat n'est pas actif");
        }

        // Créer un objet demande vide
        DemandeRuptureContrat demande = new DemandeRuptureContrat();

        model.addAttribute("contract", contract);
        model.addAttribute("demande", demande);
        model.addAttribute("demandeurId", contract.getPersonnel().getId()); // ou utilisateur connecté

        return "modules/personnel/contract-rupture-demande";
    }

    /**
     * Soumettre la demande de rupture
     */
    @PostMapping("/{id}/rupture/demande")
    public String submitRuptureDemande(
            @PathVariable Long id,
            @ModelAttribute DemandeRuptureContrat demande,
            @RequestParam Long demandeurId,
            RedirectAttributes redirectAttributes) {
        try {
            ruptureContratService.creerDemande(
                    id,
                    demandeurId,
                    demande.getMotif(),
                    demande.getJustification(),
                    demande.getDateEffetSouhaitee(),
                    demande.isPreavisEffectue());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Demande de rupture soumise avec succès ! Elle sera traitée par les RH.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erreur lors de la soumission : " + e.getMessage());
        }

        return "redirect:/personnel/contracts/manage?personnelId=" + demandeurId;
    }
}