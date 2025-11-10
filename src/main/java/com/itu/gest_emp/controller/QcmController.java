package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.QcmQuestion;
import com.itu.gest_emp.model.Reponse;
import com.itu.gest_emp.service.QcmService;
import com.itu.gest_emp.service.ApplianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/qcm")
public class QcmController {

    @Autowired
    private QcmService qcmService;

    @Autowired
    private ApplianceService applianceService;

    @GetMapping("")
    public String showQcmHome(Model model) {
        return "qcm-home";
    }

    @GetMapping("/entreprise-alcool")
    public String showQcmEntrepriseAlcool(Model model) {
        List<QcmQuestion> questions = qcmService.getQuestionsEntrepriseAlcool();
        model.addAttribute("questions", questions);
        model.addAttribute("titre", "QCM - Entreprises Alcoolisées Malagasy");
        return "qcm-quiz";
    }

    @GetMapping("/culture-generale")
    public String showQcmCultureGenerale(Model model) {
        List<QcmQuestion> questions = qcmService.getQuestionsCultureGenerale();
        model.addAttribute("questions", questions);
        model.addAttribute("titre", "QCM - Culture Générale");
        return "qcm-quiz";
    }

    @GetMapping("/mixte/{applianceId}")
    public String showQcmMixte(@PathVariable Long applianceId,Model model) {
        List<QcmQuestion> questions = qcmService.getRandomQuestions(10);
        model.addAttribute("questions", questions);
        model.addAttribute("titre", "QCM Mixte - Entreprises et Culture Générale");
        model.addAttribute("applianceId", applianceId);
        return "qcm-quiz";
    }

    @PostMapping("/correctionQuizz")
    public String processCorrection(@RequestParam Map<String, String> allParams,@RequestParam("applianceId") Long applianceId, Model model) {
        int score = 0;
        int total = 0;

        // Compter le nombre total de questions
        total = (int) allParams.keySet().stream()
                .filter(key -> key.startsWith("reponse_"))
                .count();

        // Vérifier chaque réponse
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("reponse_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().replace("reponse_", ""));
                    Long reponseId = Long.parseLong(entry.getValue());

                    QcmQuestion question = qcmService.getQuestionById(questionId);
                    if (question != null && question.getReponses() != null) {
                        // Vérifier si la réponse est correcte
                        boolean isCorrect = question.getReponses().stream()
                                .filter(r -> r.getId().equals(reponseId))
                                .findFirst()
                                .map(Reponse::isEstCorrecte)
                                .orElse(false);

                        if (isCorrect) {
                            score++;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorer les paramètres mal formattés
                }
            }
        }
        // Sauvegarder le score dans l'appliance
       // Sauvegarder le score dans l'appliance
    Appliance appliance = applianceService.getApplianceById(applianceId).orElse(null);
        if (appliance != null) {
             appliance.setNoteQCM(score);
             appliance.setTraitement("entretien"); // Mettre à jour le statut
             applianceService.saveAppliance(appliance);
            }
            System.out.println("makato"+applianceId);

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        return "redirect:/offers";
    }

    // API REST
    @GetMapping("/api/questions")
    @ResponseBody
    public List<QcmQuestion> getAllQuestionsApi() {
        return qcmService.getAllQuestions();
    }

    @GetMapping("/api/questions/{id}")
    @ResponseBody
    public QcmQuestion getQuestionByIdApi(@PathVariable Long id) {
        return qcmService.getQuestionById(id);
    }

    @GetMapping("/api/questions/categorie/{categorie}")
    @ResponseBody
    public List<QcmQuestion> getQuestionsByCategorieApi(@PathVariable String categorie) {
        return qcmService.getQuestionsByCategorie(categorie);
    }

    @PostMapping("/correction")
public String processusCorrection(@RequestParam Map<String, String> allParams,@RequestParam("applianceId") Long applianceId, Model model) {
    int score = 0;
    int total = 0;
    
    System.out.println("=== DÉBUT CORRECTION QCM ===");
    System.out.println("Paramètres reçus: " + allParams);

    // Compter le nombre total de questions
    total = (int) allParams.keySet().stream()
            .filter(key -> key.startsWith("reponse_"))
            .count();
    
    System.out.println("Nombre total de questions répondues: " + total);

    // Vérifier chaque réponse
    for (Map.Entry<String, String> entry : allParams.entrySet()) {
        if (entry.getKey().startsWith("reponse_")) {
            try {
                Long questionId = Long.parseLong(entry.getKey().replace("reponse_", ""));
                Long reponseId = Long.parseLong(entry.getValue());

                System.out.println("Question ID: " + questionId + " → Réponse ID: " + reponseId);

                QcmQuestion question = qcmService.getQuestionById(questionId);
                if (question != null && question.getReponses() != null) {
                    // Vérifier si la réponse est correcte
                    boolean isCorrect = question.getReponses().stream()
                            .filter(r -> r.getId().equals(reponseId))
                            .findFirst()
                            .map(Reponse::isEstCorrecte)
                            .orElse(false);

                    System.out.println("Question: " + question.getTexte());
                    System.out.println("Réponse choisie ID: " + reponseId);
                    System.out.println("Est correcte: " + isCorrect);

                    if (isCorrect) {
                        score++;
                        System.out.println("✓ BONNE RÉPONSE - Score: " + score);
                    } else {
                        System.out.println("✗ MAUVAISE RÉPONSE");
                        
                        // Afficher la bonne réponse
                        question.getReponses().stream()
                                .filter(Reponse::isEstCorrecte)
                                .findFirst()
                                .ifPresent(correctReponse -> 
                                    System.out.println("La bonne réponse était: " + correctReponse.getTexte())
                                );
                    }
                } else {
                    System.out.println("❌ Question non trouvée ou sans réponses");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Erreur de format: " + entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("---");
        }
    }

    System.out.println("=== RÉSULTAT FINAL ===");
    System.out.println("Score: " + score + "/" + total);
    System.out.println("Pourcentage: " + (total > 0 ? (score * 100 / total) + "%" : "N/A"));
    System.out.println("=== FIN CORRECTION ===");
    Appliance appliance = applianceService.getApplianceById(applianceId).orElse(null);
        if (appliance != null) {
             appliance.setNoteQCM(score);
             appliance.setTraitement("entretien"); // Mettre à jour le statut
             applianceService.saveAppliance(appliance);
            }
    model.addAttribute("score", score);
    model.addAttribute("total", total);
    return "redirect:/offers";
}
}