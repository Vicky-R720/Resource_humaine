package com.itu.gest_emp.modules.paie.controller;

import com.itu.gest_emp.modules.paie.model.PayslipsRh;
import com.itu.gest_emp.modules.paie.service.PayslipService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/modules/paie")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

 

    @GetMapping("/bulletins")
    public String listBulletins(Model model) {
        model.addAttribute("pageTitle", "Gestion des bulletins de paie");
        return "modules/paie/bulletins";
    }

    @GetMapping("/generation")
    public String showGenerationForm(Model model) {
        model.addAttribute("personnels", payslipService.getPersonnelRhService().findAll());
        model.addAttribute("pageTitle", "Génération des bulletins");
        return "modules/paie/generation";
    }

    @PostMapping("/generer-bulletins")
    public String genererBulletins(@RequestParam Integer mois,
            @RequestParam Integer annee, @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes, Model model) {
        try {
            payslipService.generatePayslip(personnelId, mois, annee);
            redirectAttributes.addFlashAttribute("success",
                    "Génération des bulletins pour " + mois + "/" + annee + " lancée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la génération: " + e.getMessage());
        }

        return "redirect:/modules/paie/generation";
    }

    @GetMapping("/employe/{id}/bulletins")
    public String bulletinsEmploye(@PathVariable Long id, Model model) {
        List<PayslipsRh> bulletins = payslipService.getPayslipsByPersonnel(id);
        model.addAttribute("bulletins", bulletins);
        model.addAttribute("personnelId", id);
        model.addAttribute("pageTitle", "Historique des bulletins");
        return "modules/paie/historique-employe";
    }
}