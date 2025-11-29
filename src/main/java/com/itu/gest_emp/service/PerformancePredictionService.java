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
public class PerformancePredictionService {

    @Autowired
    private PerformancePredictionRHRepository predictionRepository;

    @Autowired
    private PersonnelRHRepository personnelRepository;

    public PerformancePredictionRH predictTurnoverRisk(Long personnelId) {
        PersonnelRH personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        // Simulation d'algorithme de prédiction
        BigDecimal turnoverRisk = calculateTurnoverRisk(personnel);
        String recommandations = generateRecommandations(turnoverRisk);

        PerformancePredictionRH prediction = new PerformancePredictionRH();
        prediction.setPersonnel(personnel);
        prediction.setTypePrediction("turnover_risk");
        prediction.setScorePrediction(turnoverRisk);
        prediction.setConfidenceLevel(new BigDecimal("0.85"));
        prediction.setFacteursCles(analyzeRiskFactors(personnel));
        prediction.setRecommandations(recommandations);
        prediction.setDatePrediction(LocalDate.now());

        return predictionRepository.save(prediction);
    }

    private BigDecimal calculateTurnoverRisk(PersonnelRH personnel) {
        double baseRisk = 0.3;

        // Facteurs de risque
        if (personnel.getDateEmbauche().isBefore(LocalDate.now().minusYears(2))) {
            baseRisk += 0.2;
        }

        return BigDecimal.valueOf(Math.min(baseRisk, 0.95));
    }

    private String analyzeRiskFactors(PersonnelRH personnel) {
        List<String> factors = new ArrayList<>();

        if (personnel.getDateEmbauche().isBefore(LocalDate.now().minusYears(2))) {
            factors.add("Ancienneté élevée");
        }

        return String.join(", ", factors);
    }

    private String generateRecommandations(BigDecimal riskScore) {
        if (riskScore.compareTo(new BigDecimal("0.7")) > 0) {
            return "Risque élevé : Entretien de retention, évolution carrière, formation.";
        } else if (riskScore.compareTo(new BigDecimal("0.4")) > 0) {
            return "Risque modéré : Surveiller satisfaction au travail.";
        } else {
            return "Risque faible : Maintenir conditions actuelles.";
        }
    }

    public List<PerformancePredictionRH> getPersonnelPredictions(Long personnelId) {
        return predictionRepository.findByPersonnelIdOrderByDatePredictionDesc(personnelId);
    }
}
