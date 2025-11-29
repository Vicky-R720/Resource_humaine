// ContractsRhService.java
package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.repository.ContractTypeRepository;
import com.itu.gest_emp.modules.personnel.repository.ContractsRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractsRhService {

    private final ContractsRhRepository contractsRhRepository;
    private final ContractTypeRepository contractTypeRepository;

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

    public ContractsRh getContrat(Long personnelId, LocalDate date) {
        return contractsRhRepository
                .findContract(personnelId, date)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Aucun contrat  trouvé pour le personnel " + personnelId + " à la date " + date));
    }

    public BigDecimal getSalaireBase(Long personnelId, Integer mois, Integer annee) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDate date = ym.atEndOfMonth();
        ContractsRh contrat = getContrat(personnelId, date);
        return contrat.getSalaireBase();
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
        if (contract.getContractType() != null)
            existing.setContractType(contract.getContractType());
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
        contract.setDateValidationEssai(LocalDate.now()); // date de validation

        if (!valide) {
            contract.setStatut("terminé");
            contract.setMotifFin("Période d'essai non validée");
        }

        return contractsRhRepository.save(contract);
    }

    public ContractsRh prolongerOuTransformerEnCDI(Long ancienContratId, LocalDate nouvelleDateFin,
            BigDecimal salaire) {
        ContractsRh ancienContrat = contractsRhRepository.findById(ancienContratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + ancienContratId));

        if (!"CDD".equalsIgnoreCase(ancienContrat.getContractType().getCode())) {
            throw new IllegalStateException("Seuls les CDD peuvent être prolongés");
        }

        List<ContractsRh> cddsExistants = contractsRhRepository
                .findByPersonnelAndContractType_Code(ancienContrat.getPersonnel(), "CDD");

        long totalMois = cddsExistants.stream()
                .mapToLong(c -> ChronoUnit.MONTHS.between(c.getDateDebut(), c.getDateFin()))
                .sum();

        long dureeProposee = ChronoUnit.MONTHS.between(ancienContrat.getDateFin().plusDays(1), nouvelleDateFin);
        totalMois += dureeProposee;

        ContractsRh nouveauContrat = new ContractsRh();
        nouveauContrat.setPersonnel(ancienContrat.getPersonnel());
        nouveauContrat.setSalaireBase(salaire);
        nouveauContrat.setDateDebut(ancienContrat.getDateFin().plusDays(1));

        // Vérifier la limite 24 mois
        if (totalMois > 24) {
            // Transforme en CDI
            nouveauContrat.setContractType(contractTypeRepository.findByCode("CDI").orElseThrow());
            nouveauContrat.setDateFin(null);
            nouveauContrat.setStatut("actif");

            ancienContrat.setStatut("termine");
            ancienContrat.setMotifFin("Passage en CDI");
            contractsRhRepository.save(ancienContrat);
        } else {
            // Nouveau CDD
            nouveauContrat.setContractType(ancienContrat.getContractType());
            nouveauContrat.setDateFin(nouvelleDateFin);
            nouveauContrat.setStatut("actif");

            ancienContrat.setStatut("termine");
            ancienContrat.setMotifFin("Renouvellement");
            contractsRhRepository.save(ancienContrat);
        }

        return contractsRhRepository.save(nouveauContrat);
    }

    public List<ContractsRh> findByPersonnelIdOrderByDateDebutAsc(Long id) {
        return contractsRhRepository.findByPersonnelIdOrderByDateDebutAsc(id);
    }
}