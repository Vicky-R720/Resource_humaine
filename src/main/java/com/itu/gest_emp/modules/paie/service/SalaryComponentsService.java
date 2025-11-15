package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.SalaryComponentsRh;
import com.itu.gest_emp.modules.paie.repository.SalaryComponentsRhRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryComponentsService {

    @Autowired
    private SalaryComponentsRhRepository salaryComponentsRhRepository;

    public SalaryComponentsRh createComponent(SalaryComponentsRh component) {
        component.setCreatedAt(java.time.LocalDateTime.now());
        component.setUpdatedAt(java.time.LocalDateTime.now());
        return salaryComponentsRhRepository.save(component);
    }

    public SalaryComponentsRh updateComponent(Long id, SalaryComponentsRh componentDetails) {
        return salaryComponentsRhRepository.findById(id).map(component -> {
            component.setTypeComposante(componentDetails.getTypeComposante());
            component.setMontant(componentDetails.getMontant());
            component.setIsRecurring(componentDetails.getIsRecurring());
            component.setDateDebut(componentDetails.getDateDebut());
            component.setDateFin(componentDetails.getDateFin());
            component.setDescription(componentDetails.getDescription());
            component.setStatut(componentDetails.getStatut());
            component.setUpdatedAt(java.time.LocalDateTime.now());
            return salaryComponentsRhRepository.save(component);
        }).orElse(null);
    }

    public void deleteComponent(Long id) {
        salaryComponentsRhRepository.deleteById(id);
    }

    public List<SalaryComponentsRh> getComponentsByPersonnel(Long personnelId) {
        return salaryComponentsRhRepository.findByPersonnel_Id(personnelId);
    }

    public BigDecimal calculateTotalPrimes(Long personnelId, Integer mois, Integer annee) {
        LocalDate referenceDate = LocalDate.of(annee, mois, 1);
        List<SalaryComponentsRh> activeComponents = salaryComponentsRhRepository
                .findActiveComponentsByPersonnelAndDate(personnelId, referenceDate);
        System.out.println(activeComponents.size());
        return activeComponents.stream()
                .map(SalaryComponentsRh::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculatePrimeAncienneteAuto(Long personnelId) {
        // À implémenter: Calcul automatique basé sur l'ancienneté
        // Récupérer la date d'embauche depuis PersonnelRH
        // Calculer les années d'ancienneté
        // Appliquer le taux depuis SalaryParametersRH
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateAllocationFamilialeAuto(Long personnelId) {
        // À implémenter: Calcul automatique basé sur le nombre d'enfants
        // Récupérer nombre_enfants depuis PersonnelRH
        // Appliquer le montant par enfant depuis SalaryParametersRH
        return BigDecimal.ZERO;
    }

    public List<SalaryComponentsRh> getPrimesRecurrentesByPersonnel(Long personnelId) {
        return salaryComponentsRhRepository.findRecurringComponentsByPersonnel(personnelId);
    }

    public List<String> getTypesComposantes() {
        return salaryComponentsRhRepository.findDistinctTypeComposante();
    }

    public SalaryComponentsRh createPrimePonctuelle(Long personnelId, String type, BigDecimal montant,
            String description, LocalDate dateEffet) {
        SalaryComponentsRh prime = new SalaryComponentsRh();
        prime.setPersonnel(new PersonnelRh());
        prime.getPersonnel().setId(personnelId);
        prime.setTypeComposante(type);
        prime.setMontant(montant);
        prime.setIsRecurring(false);
        prime.setDateDebut(dateEffet);
        prime.setDateFin(dateEffet); // Prime ponctuelle = même date début et fin
        prime.setDescription(description);
        prime.setStatut("actif");

        return createComponent(prime);
    }
}