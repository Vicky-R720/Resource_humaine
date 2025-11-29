package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Personnel;
import com.itu.gest_emp.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PersonnelService {
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    /**
     * Récupérer tout le personnel trié par date d'embauche (plus récent en premier)
     */
    public List<Personnel> getAllPersonnelSorted() {
        return personnelRepository.findAll(Sort.by(Sort.Direction.DESC, "dateEmbauche"));
    }
    
    /**
     * Rechercher le personnel par nom/prénom
     */
    public List<Personnel> searchPersonnelByName(String searchTerm) {
        // Cette méthode nécessite d'ajouter une requête dans PersonnelRepository
        // Pour l'instant, on filtre en Java (pas optimal pour de gros volumes)
        return getAllPersonnelSorted().stream()
            .filter(p -> {
                String fullName = (p.getPersonne().getPrenom() + " " + p.getPersonne().getNom()).toLowerCase();
                return fullName.contains(searchTerm.toLowerCase());
            })
            .toList();
    }
    
    /**
     * Récupérer un employé par ID
     */
    public Optional<Personnel> getPersonnelById(Long id) {
        return personnelRepository.findById(id);
    }
    
    /**
     * Compter les embauches de ce mois
     */
    public long countPersonnelHiredThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        
        return getAllPersonnelSorted().stream()
            .filter(p -> p.getDateEmbauche().isAfter(startOfMonth) && p.getDateEmbauche().isBefore(endOfMonth))
            .count();
    }
    
    /**
     * Calculer l'ancienneté en format lisible
     */
    public String calculateSeniority(LocalDateTime dateEmbauche) {
        if (dateEmbauche == null) {
            return "Non définie";
        }
        
        Period period = Period.between(dateEmbauche.toLocalDate(), LocalDateTime.now().toLocalDate());
        
        int annees = period.getYears();
        int mois = period.getMonths();
        
        if (annees > 0) {
            if (mois > 0) {
                return annees + " an" + (annees > 1 ? "s" : "") + " et " + mois + " mois";
            } else {
                return annees + " an" + (annees > 1 ? "s" : "");
            }
        } else if (mois > 0) {
            return mois + " mois";
        } else {
            return "Moins d'un mois";
        }
    }
    
    /**
     * Mettre à jour un employé
     */
    public Personnel updatePersonnel(Personnel personnel) {
        return personnelRepository.save(personnel);
    }
    
    /**
     * Supprimer un employé
     */
    public void deletePersonnel(Long id) {
        if (!personnelRepository.existsById(id)) {
            throw new RuntimeException("Personnel avec ID " + id + " introuvable");
        }
        personnelRepository.deleteById(id);
    }
    
    // Méthodes existantes
    public List<Personnel> getAllPersonnel() {
        return personnelRepository.findAll();
    }
    
    public Personnel createPersonnel(Personnel personnel) {
        return personnelRepository.save(personnel);
    }
}