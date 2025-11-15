// ContractsRhService.java
package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.repository.ContractsRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractsRhService {

    private final ContractsRhRepository contractsRhRepository;

    public List<ContractsRh> findAll() {
        return contractsRhRepository.findAll();
    }

    public Optional<ContractsRh> findById(Long id) {
        return contractsRhRepository.findById(id);
    }

    public List<ContractsRh> findByPersonnelId(Long personnelId) {
        return contractsRhRepository.findByPersonnelId(personnelId);
    }

    public List<ContractsRh> findByStatut(String statut) {
        return contractsRhRepository.findByStatut(statut);
    }

    public List<ContractsRh> findContractsExpirant(LocalDate dateLimit) {
        return contractsRhRepository.findByDateFin(dateLimit);
    }

    public BigDecimal getSalaireBase(Long personnelId, Integer mois, Integer annee) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDate date = ym.atEndOfMonth();

        return contractsRhRepository
                .findActiveContract(personnelId, date, "actif")
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Aucun contrat actif trouvé pour le personnel " + personnelId + " à la date " + date))
                .getSalaireBase();
    }

    public ContractsRh create(ContractsRh contract) {
        // Calcul automatique de la date fin essai
        if (contract.getDureeEssaiMois() != null && contract.getDureeEssaiMois() > 0) {
            contract.setDateFinEssai(contract.getDateDebut().plusMonths(contract.getDureeEssaiMois()));
        }

        if (contract.getStatut() == null) {
            contract.setStatut("actif");
        }

        return contractsRhRepository.save(contract);
    }

    public ContractsRh update(Long id, ContractsRh contract) {
        ContractsRh existing = contractsRhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

        if (contract.getTypeContrat() != null)
            existing.setTypeContrat(contract.getTypeContrat());
        if (contract.getDateDebut() != null)
            existing.setDateDebut(contract.getDateDebut());
        if (contract.getDateFin() != null)
            existing.setDateFin(contract.getDateFin());
        if (contract.getDureeEssaiMois() != null) {
            existing.setDureeEssaiMois(contract.getDureeEssaiMois());
            existing.setDateFinEssai(existing.getDateDebut().plusMonths(contract.getDureeEssaiMois()));
        }
        if (contract.getIsEssaiValide() != null)
            existing.setIsEssaiValide(contract.getIsEssaiValide());
        if (contract.getSalaireBase() != null)
            existing.setSalaireBase(contract.getSalaireBase());
        if (contract.getStatut() != null)
            existing.setStatut(contract.getStatut());
        if (contract.getDocumentPath() != null)
            existing.setDocumentPath(contract.getDocumentPath());

        return contractsRhRepository.save(existing);
    }

    public void deleteById(Long id) {
        contractsRhRepository.deleteById(id);
    }

    public ContractsRh validerPeriodeEssai(Long id, boolean valide) {
        ContractsRh contract = contractsRhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

        contract.setIsEssaiValide(valide);
        if (!valide) {
            contract.setStatut("terminé");
            contract.setMotifFin("Période d'essai non validée");
        }

        return contractsRhRepository.save(contract);
    }

    public ContractsRh renouvelerContrat(Long ancienContratId, ContractsRh nouveauContrat) {
        ContractsRh ancienContrat = contractsRhRepository.findById(ancienContratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + ancienContratId));

        // Terminer l'ancien contrat
        ancienContrat.setStatut("terminé");
        ancienContrat.setMotifFin("Renouvellement");
        contractsRhRepository.save(ancienContrat);

        // Créer le nouveau contrat
        nouveauContrat.setPersonnel(ancienContrat.getPersonnel());
        return create(nouveauContrat);
    }
}