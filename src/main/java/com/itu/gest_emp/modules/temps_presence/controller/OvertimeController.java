package com.itu.gest_emp.modules.temps_presence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu.gest_emp.modules.temps_presence.service.OvertimeService;
import com.itu.gest_emp.modules.temps_presence.dto.overtime.*;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequestMapping("/temps_presence/overtime")
@RequiredArgsConstructor
public class OvertimeController {

    private final OvertimeService overtimeService;

    @GetMapping
    public String listOvertime(Model model, @RequestParam(required = false) Long personnelId) {
        List<OvertimeRh> overtimes;
        if (personnelId != null) {
            overtimes = overtimeService.getOvertimeByPersonnel(personnelId);
        } else {
            overtimes = overtimeService.getAllOvertime();
        }
        model.addAttribute("overtimes", overtimes);
        return "modules/temps_presence/overtime-list";
    }

    @GetMapping("/en-attente")
    public String listOvertimeEnAttente(Model model) {
        List<OvertimeRh> overtimes = overtimeService.getOvertimeEnAttente();
        model.addAttribute("overtimes", overtimes);
        return "modules/temps_presence/overtime-validation";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("overtime", new OvertimeCreateDto());
        return "modules/temps_presence/overtime-form";
    }

    @PostMapping("/create")
    public String createOvertime(@ModelAttribute OvertimeCreateDto dto, RedirectAttributes redirectAttributes) {
        try {
            overtimeService.createOvertime(dto);
            redirectAttributes.addFlashAttribute("success", "Heures supplémentaires créées");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/temps_presence/overtime";
    }

    @PostMapping("/{id}/valider")
    public String validerOvertime(@PathVariable Long id,
            @ModelAttribute OvertimeValidationDto validationDto,
            @RequestParam Long validatorId,
            RedirectAttributes redirectAttributes) {
        try {
            overtimeService.validerOvertime(id, validationDto, validatorId);
            redirectAttributes.addFlashAttribute("success", "Validation enregistrée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/temps_presence/overtime/en-attente";
    }
}