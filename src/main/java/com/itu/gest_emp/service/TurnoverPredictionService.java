package com.itu.gest_emp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.PerformancePredictionRH;
import com.itu.gest_emp.model.PersonnelRH;
import com.itu.gest_emp.repository.PerformancePredictionRHRepository;
import com.itu.gest_emp.repository.PersonnelRHRepository;

@Service
public class TurnoverPredictionService {

    @Autowired
    private PersonnelRHRepository personnelRepository;

    @Autowired
    private PerformancePredictionRHRepository predictionRepository;

    public PerformancePredictionRH predictTurnoverRisk(Long personnelId) {
        PersonnelRH personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        // Calcul du score de risque
        BigDecimal turnoverRisk = calculateTurnoverScore(personnel);
        String facteurs = analyzeRiskFactors(personnel);
        String recommandations = generateRecommandations(turnoverRisk);

        PerformancePredictionRH prediction = new PerformancePredictionRH();
        prediction.setPersonnel(personnel);
        prediction.setTypePrediction("turnover_risk");
        prediction.setScorePrediction(turnoverRisk);
        prediction.setConfidenceLevel(new BigDecimal("0.82"));
        prediction.setFacteursCles(facteurs);
        prediction.setRecommandations(recommandations);
        prediction.setDatePrediction(LocalDate.now());

        return predictionRepository.save(prediction);
    }

    private BigDecimal calculateTurnoverScore(PersonnelRH personnel) {
        double score = 0.0;

        // Facteurs de risque
        if (personnel.getDateEmbauche().isBefore(LocalDate.now().minusYears(3))) {
            score += 0.3; // Ancienneté > 3 ans
        }

        if (personnel.getDateEmbauche().isAfter(LocalDate.now().minusYears(1))) {
            score += 0.2; // Moins d'1 an (période d'essai récente)
        }

        // Poste non managérial
        if (personnel.getPost() != null && !personnel.getPost().getName().toLowerCase().contains("manager")) {
            score += 0.1;
        }

        // Salaire bas (exemple)
        if (getSalaireMoyenPoste(personnel) > 0) {
            double ratio = getSalairePersonnel(personnel) / getSalaireMoyenPoste(personnel);
            if (ratio < 0.8)
                score += 0.2;
        }

        return BigDecimal.valueOf(Math.min(score, 0.95));
    }

    private String analyzeRiskFactors(PersonnelRH personnel) {
        List<String> factors = new ArrayList<>();

        if (personnel.getDateEmbauche().isBefore(LocalDate.now().minusYears(3))) {
            factors.add("Ancienneté élevée");
        }
        if (personnel.getDateEmbauche().isAfter(LocalDate.now().minusYears(1))) {
            factors.add("Nouvel employé");
        }
        if (getSalairePersonnel(personnel) < getSalaireMoyenPoste(personnel) * 0.8) {
            factors.add("Salaire inférieur au marché");
        }

        return String.join(", ", factors);
    }

    private String generateRecommandations(BigDecimal riskScore) {
        if (riskScore.compareTo(new BigDecimal("0.7")) > 0) {
            return "• Entretien de retention urgent\n• Évolution salariale à étudier\n• Plan de développement personnel\n• Mentorat";
        } else if (riskScore.compareTo(new BigDecimal("0.4")) > 0) {
            return "• Surveillance de la satisfaction\n• Entretien trimestriel\n• Opportunités d'évolution";
        } else {
            return "• Maintenir les conditions actuelles\n• Suivi régulier";
        }
    }

    // Méthodes helper
    private double getSalairePersonnel(PersonnelRH personnel) {
        // Implémentez la logique pour récupérer le salaire
        return 2000000.0; // Exemple
    }

    private double getSalaireMoyenPoste(PersonnelRH personnel) {
        // Implémentez la logique pour récupérer le salaire moyen du poste
        return 2500000.0; // Exemple
    }
}