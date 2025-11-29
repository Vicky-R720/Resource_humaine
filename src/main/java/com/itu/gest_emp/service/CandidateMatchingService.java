package com.itu.gest_emp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.CV;
import com.itu.gest_emp.model.CandidateMatching;
import com.itu.gest_emp.model.Offer;
import com.itu.gest_emp.repository.CVRepository;
import com.itu.gest_emp.repository.CandidateMatchingRepository;
import com.itu.gest_emp.repository.OfferRepository;

@Service
public class CandidateMatchingService {

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CandidateMatchingRepository matchingRepository;

    public List<CandidateMatching> matchCandidatesWithOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        List<CV> allCVs = cvRepository.findByStatut("actif");
        List<CandidateMatching> matches = new ArrayList<>();

        for (CV cv : allCVs) {
            CandidateMatching match = calculateMatch(cv, offer);
            if (match.getScoreTotal().compareTo(new BigDecimal("60")) > 0) {
                matches.add(matchingRepository.save(match));
            }
        }

        // Trier par score décroissant
        return matches.stream()
                .sorted((m1, m2) -> m2.getScoreTotal().compareTo(m1.getScoreTotal()))
                .collect(Collectors.toList());
    }

    private CandidateMatching calculateMatch(CV cv, Offer offer) {
        CandidateMatching match = new CandidateMatching();
        match.setCv(cv);
        match.setOffer(offer);

        // Score compétences (40%)
        BigDecimal scoreCompetences = calculateCompetenceScore(cv, offer);
        match.setScoreCompetences(scoreCompetences);

        // Score expérience (30%)
        BigDecimal scoreExperience = calculateExperienceScore(cv, offer);
        match.setScoreExperience(scoreExperience);

        // Score formation (20%)
        BigDecimal scoreFormation = calculateFormationScore(cv, offer);
        match.setScoreFormation(scoreFormation);

        // Score compatibilité (10%)
        BigDecimal scoreCompatibilite = calculateCompatibilityScore(cv, offer);
        match.setScoreCompatibilite(scoreCompatibilite);

        // Score total
        BigDecimal scoreTotal = scoreCompetences.multiply(new BigDecimal("0.4"))
                .add(scoreExperience.multiply(new BigDecimal("0.3")))
                .add(scoreFormation.multiply(new BigDecimal("0.2")))
                .add(scoreCompatibilite.multiply(new BigDecimal("0.1")));

        match.setScoreTotal(scoreTotal);
        match.setPointsForts(generateStrengths(cv, offer));
        match.setPointsFaibles(generateWeaknesses(cv, offer));
        match.setRecommandations(generateRecommandations(cv, offer, scoreTotal));

        return match;
    }

    private BigDecimal calculateCompetenceScore(CV cv, Offer offer) {
        // Logique de matching des compétences
        int matches = 0;
        int total = 5; // Exemple de compétences requises

        if (cv.getCompetencesCles() != null && offer.getRequiredProfile() != null) {
            String[] competencesRequises = { "java", "spring", "sql", "angular", "git" };
            for (String competence : competencesRequises) {
                if (cv.getCompetencesCles().toLowerCase().contains(competence)) {
                    matches++;
                }
            }
        }

        return new BigDecimal((matches * 100) / total);
    }

    private BigDecimal calculateExperienceScore(CV cv, Offer offer) {
        // Matching de l'expérience
        int experienceRequise = extractExperienceFromOffer(offer);
        int experienceCandidat = cv.getExperienceAnnees();

        if (experienceCandidat >= experienceRequise) {
            return new BigDecimal("100");
        } else if (experienceCandidat >= experienceRequise - 2) {
            return new BigDecimal("75");
        } else {
            return new BigDecimal("50");
        }
    }

    private String generateStrengths(CV cv, Offer offer) {
        List<String> strengths = new ArrayList<>();

        if (cv.getExperienceAnnees() >= extractExperienceFromOffer(offer)) {
            strengths.add("Expérience suffisante");
        }

        if (cv.getCompetencesCles() != null && cv.getCompetencesCles().toLowerCase().contains("java")) {
            strengths.add("Compétences techniques solides");
        }

        return String.join(", ", strengths);
    }

    private int extractExperienceFromOffer(Offer offer) {
        // Extraction de l'expérience depuis l'offre
        if (offer.getExperienceLevel() != null) {
            if (offer.getExperienceLevel().contains("senior"))
                return 5;
            if (offer.getExperienceLevel().contains("confirmé"))
                return 3;
            if (offer.getExperienceLevel().contains("junior"))
                return 1;
        }
        return 2;
    }

    // Autres méthodes helper...
    private BigDecimal calculateFormationScore(CV cv, Offer offer) {
        return new BigDecimal("80"); // Exemple simplifié
    }

    private BigDecimal calculateCompatibilityScore(CV cv, Offer offer) {
        return new BigDecimal("90"); // Exemple simplifié
    }

    private String generateWeaknesses(CV cv, Offer offer) {
        return "Aucun point faible majeur"; // Exemple simplifié
    }

    private String generateRecommandations(CV cv, Offer offer, BigDecimal score) {
        if (score.compareTo(new BigDecimal("80")) > 0) {
            return "Candidat très prometteur - Entretien prioritaire";
        } else if (score.compareTo(new BigDecimal("60")) > 0) {
            return "Candidat intéressant - Entretien à planifier";
        } else {
            return "Candidat à reconsidérer";
        }
    }
}