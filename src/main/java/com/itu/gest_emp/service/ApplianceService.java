package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.repository.ApplianceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplianceService {
    
    @Autowired
    private ApplianceRepository applianceRepository;
    
    public List<Appliance> getAllAppliances() {
        return applianceRepository.findAllOrderByDateDesc();
    }
    
    public Optional<Appliance> getApplianceById(Long id) {
        return applianceRepository.findById(id);
    }
    
    public Appliance saveAppliance(Appliance appliance) {
        return applianceRepository.save(appliance);
    }
    
    public void deleteAppliance(Long id) {
        applianceRepository.deleteById(id);
    }
    
    public List<Appliance> getAppliancesByPersonId(Long personId) {
        return applianceRepository.findByPersonId(personId);
    }
    
    public List<Appliance> getAppliancesByOfferId(Long offerId) {
        return applianceRepository.findByOfferId(offerId);
    }
    
    public Long countAppliancesByOfferId(Long offerId) {
        return applianceRepository.countByOfferId(offerId);
    }

    // Nouvelles méthodes pour le filtrage
    
    /**
     * Filtrer les candidatures par tranche d'âge
     */
    public List<Appliance> filterByAgeRange(String ageRange) {
        return getAllAppliances().stream()
            .filter(appliance -> {
                int age = appliance.getPerson().getAge();
                switch (ageRange) {
                    case "18-25": return age >= 18 && age <= 25;
                    case "26-35": return age >= 26 && age <= 35;
                    case "36-45": return age >= 36 && age <= 45;
                    case "46-55": return age >= 46 && age <= 55;
                    case "55+": return age > 55;
                    default: return true;
                }
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filtrer les candidatures par adresse/localisation
     */
    public List<Appliance> filterByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return getAllAppliances();
        }
        
        return getAllAppliances().stream()
            .filter(appliance -> {
                String address = appliance.getPerson().getAdresse();
                return address != null && 
                       address.toLowerCase().contains(location.toLowerCase());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filtrer les candidatures par diplôme
     */
    public List<Appliance> filterByDiploma(String diploma) {
        if (diploma == null || diploma.trim().isEmpty()) {
            return getAllAppliances();
        }
        
        return getAllAppliances().stream()
            .filter(appliance -> {
                if (appliance.getAcademicalQualifications() == null || 
                    appliance.getAcademicalQualifications().isEmpty()) {
                    return false;
                }
                
                return appliance.getAcademicalQualifications().stream()
                    .anyMatch(aq -> aq.getFiliere() != null && 
                                   diploma.equals(aq.getFiliere().getName()));
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filtrer les candidatures par score de matching
     */
    public List<Appliance> filterByMatchingScore(String scoreRange) {
        if (scoreRange == null || scoreRange.trim().isEmpty()) {
            return getAllAppliances();
        }
        
        return getAllAppliances().stream()
            .filter(appliance -> {
                double score = appliance.matchingCV();
                switch (scoreRange) {
                    case "80-100": return score >= 80 && score <= 100;
                    case "60-79": return score >= 60 && score < 80;
                    case "40-59": return score >= 40 && score < 60;
                    case "0-39": return score >= 0 && score < 40;
                    default: return true;
                }
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filtrage multiple avec tous les critères
     */
    public List<Appliance> filterAppliances(String ageRange, String address, String diploma, 
                                          String scoreRange, String offer, String company) {
        List<Appliance> appliances = getAllAppliances();
        
        // Appliquer chaque filtre successivement
        if (ageRange != null && !ageRange.trim().isEmpty()) {
            appliances = appliances.stream()
                .filter(a -> matchesAgeRange(a.getPerson().getAge(), ageRange))
                .collect(Collectors.toList());
        }
        
        if (address != null && !address.trim().isEmpty()) {
            appliances = appliances.stream()
                .filter(a -> a.getPerson().getAdresse() != null && 
                           a.getPerson().getAdresse().toLowerCase().contains(address.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Continuer avec les autres filtres...
        
        return appliances;
    }
    
    private boolean matchesAgeRange(int age, String ageRange) {
        switch (ageRange) {
            case "18-25": return age >= 18 && age <= 25;
            case "26-35": return age >= 26 && age <= 35;
            case "36-45": return age >= 36 && age <= 45;
            case "46-55": return age >= 46 && age <= 55;
            case "55+": return age > 55;
            default: return true;
        }
    }
}