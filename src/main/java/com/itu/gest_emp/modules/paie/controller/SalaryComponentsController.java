package com.itu.gest_emp.modules.paie.controller;

import com.itu.gest_emp.modules.paie.model.SalaryComponentsRh;
import com.itu.gest_emp.modules.paie.service.SalaryComponentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/modules/paie/primes")
public class SalaryComponentsController {

    @Autowired
    private SalaryComponentsService salaryComponentsService;

    @GetMapping("/employe/{personnelId}")
    public String listPrimesEmploye(@PathVariable Long personnelId, Model model) {
        List<SalaryComponentsRh> primes = salaryComponentsService.getComponentsByPersonnel(personnelId);
        model.addAttribute("primes", primes);
        model.addAttribute("personnelId", personnelId);
        model.addAttribute("pageTitle", "Gestion des primes - Employé");
        return "modules/paie/primes-employe";
    }

    @GetMapping("/employe/{personnelId}/nouveau")
    public String showCreateForm(@PathVariable Long personnelId, Model model) {
        List<String> types = salaryComponentsService.getTypesComposantes();

        model.addAttribute("composante", new SalaryComponentsRh());
        model.addAttribute("personnelId", personnelId);
        model.addAttribute("typesComposantes", types);
        model.addAttribute("pageTitle", "Nouvelle prime/indemnité");
        return "modules/paie/editer-composante";
    }

    @GetMapping("/editer/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SalaryComponentsRh composante = salaryComponentsService.getComponentsByPersonnel(null)
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(new SalaryComponentsRh());
                System.out.println(composante.getTypeComposante());

        List<String> types = salaryComponentsService.getTypesComposantes();

        model.addAttribute("composante", composante);
        model.addAttribute("typesComposantes", types);
        model.addAttribute("pageTitle", "Modifier la prime/indemnité");
        return "modules/paie/editer-composante";
    }

    @PostMapping("/sauvegarder")
    public String sauvegarderComposante(@ModelAttribute SalaryComponentsRh composante,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {
        try {
            if (composante.getId() == null) {
                salaryComponentsService.createComponent(composante);
                redirectAttributes.addFlashAttribute("success", "Prime/indemnité créée avec succès");
            } else {
                salaryComponentsService.updateComponent(composante.getId(), composante);
                redirectAttributes.addFlashAttribute("success", "Prime/indemnité modifiée avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }

        return "redirect:/modules/paie/primes/employe/" + personnelId;
    }

    @PostMapping("/prime-ponctuelle")
    public String creerPrimePonctuelle(@RequestParam Long personnelId,
            @RequestParam String type,
            @RequestParam BigDecimal montant,
            @RequestParam String description,
            @RequestParam String dateEffet,
            RedirectAttributes redirectAttributes) {
        try {
            salaryComponentsService.createPrimePonctuelle(personnelId, type, montant, description,
                    java.time.LocalDate.parse(dateEffet));
            redirectAttributes.addFlashAttribute("success", "Prime ponctuelle ajoutée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }

        return "redirect:/modules/paie/primes/employe/" + personnelId;
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerComposante(@PathVariable Long id,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {
        try {
            salaryComponentsService.deleteComponent(id);
            redirectAttributes.addFlashAttribute("success", "Prime/indemnité supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }

        return "redirect:/modules/paie/primes/employe/" + personnelId;
    }
}