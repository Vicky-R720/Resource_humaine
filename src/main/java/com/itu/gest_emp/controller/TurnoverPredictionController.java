package com.itu.gest_emp.controller;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu.gest_emp.model.PerformancePredictionRH;
import com.itu.gest_emp.service.TurnoverPredictionService;

@Controller
@RequestMapping("/manager/prediction")
public class TurnoverPredictionController {

    @Autowired
    private TurnoverPredictionService predictionService;

    @PostMapping("/turnover/{personnelId}")
    public String predictTurnover(@PathVariable Long personnelId, RedirectAttributes redirectAttributes) {
        try {
            PerformancePredictionRH prediction = predictionService.predictTurnoverRisk(personnelId);
            redirectAttributes.addFlashAttribute("success",
                    "Prédiction générée - Score: " + prediction.getScorePrediction().multiply(new BigDecimal(100))
                            + "%");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la prédiction");
        }
        return "redirect:/manager/rh/personnel";
    }
}