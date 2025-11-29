package com.itu.gest_emp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu.gest_emp.model.PerformancePredictionRH;
import com.itu.gest_emp.model.PersonnelRH;
import com.itu.gest_emp.repository.PersonnelRHRepository;
import com.itu.gest_emp.service.AuthenticationService;
import com.itu.gest_emp.service.PerformancePredictionService;

@Controller
@RequestMapping("/manager/predictionss")
public class PerformancePredictionController {

    @Autowired
    private PerformancePredictionService predictionService;

    @Autowired
    private PersonnelRHRepository personnelRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public String predictionsDashboard(Model model) {
        if (!authenticationService.isManager()) {
            return "redirect:/employee/self-service/dashboard";
        }

        List<PersonnelRH> allPersonnel = personnelRepository.findByStatut("actif");
        model.addAttribute("personnelList", allPersonnel);
        return "manager/predictions-dashboard";
    }

    @PostMapping("/turnover/{personnelId}")
    public String predictTurnover(@PathVariable Long personnelId, RedirectAttributes redirectAttributes) {
        if (!authenticationService.isManager()) {
            return "redirect:/employee/self-service/dashboard";
        }

        try {
            PerformancePredictionRH prediction = predictionService.predictTurnoverRisk(personnelId);
            redirectAttributes.addFlashAttribute("success",
                    "Prédiction générée pour " + prediction.getPersonnel().getPerson().getNom());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la prédiction");
        }

        return "redirect:/manager/predictions";
    }

    @GetMapping("/personnel/{personnelId}")
    public String getPersonnelPredictions(@PathVariable Long personnelId, Model model) {
        if (!authenticationService.isManager()) {
            return "redirect:/employee/self-service/dashboard";
        }

        PersonnelRH personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        List<PerformancePredictionRH> predictions = predictionService.getPersonnelPredictions(personnelId);

        model.addAttribute("personnel", personnel);
        model.addAttribute("predictions", predictions);

        return "manager/personnel-predictions";
    }
}