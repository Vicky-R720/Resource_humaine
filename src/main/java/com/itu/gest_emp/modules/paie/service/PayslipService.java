package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.PayslipsRh;
import com.itu.gest_emp.modules.paie.model.SalaryParametersRh;
import com.itu.gest_emp.modules.paie.repository.PayslipsRhRepository;
import com.itu.gest_emp.modules.paie.repository.SalaryParametersRhRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.temps_presence.service.PaieIntegrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PayslipService {

    @Autowired
    private PersonnelRhService personnelRhService;

    @Autowired
    private SalaryComponentsService salaryComponentsService;

    @Autowired
    private PaieIntegrationService paie;

    @Autowired
    private ContractsRhService contractsRhService;

    public PersonnelRhService getPersonnelRhService() {
        return personnelRhService;
    }

    @Autowired
    private PayslipsRhRepository payslipsRhRepository;

    @Autowired
    private SalaryParametersRhRepository salaryParametersRhRepository;

    public Optional<PayslipsRh> generatePayslip(Long personnelId, Integer mois, Integer annee) {
        // Vérifier si le bulletin existe déjà
        if (payslipsRhRepository.existsByPersonnel_IdAndMoisAndAnnee(personnelId, mois, annee)) {
            return Optional.empty();
        }

        // Logique de calcul du bulletin de paie
        PayslipsRh payslip = new PayslipsRh();
        payslip.setPersonnel(personnelRhService.findById(personnelId).orElseThrow());
        payslip.setMois(mois);
        payslip.setAnnee(annee);

        // Calculer le salaire de base (à implémenter avec ContractsRH)
        BigDecimal salaireBase = calculateSalaireBase(personnelId, mois, annee);
        payslip.setSalaireBase(salaireBase);

        // Calculer les primes (à implémenter)
        BigDecimal totalPrimes = calculateTotalPrimes(personnelId, mois, annee);
        payslip.setTotalPrimes(totalPrimes);

        // Calculer les heures supplémentaires (à implémenter avec OvertimeRH)
        BigDecimal heuresSupplementaires = calculateHeuresSupplementaires(personnelId, mois, annee);
        payslip.setHeuresSupplementaires(heuresSupplementaires);

        // Calculer le total brut
        BigDecimal totalBrut = salaireBase.add(totalPrimes).add(heuresSupplementaires);
        payslip.setTotalBrut(totalBrut);

        // Calculer les retenues
        calculateRetenues(payslip, totalBrut);

        // Calculer le net à payer
        BigDecimal netAPayer = totalBrut.subtract(payslip.getTotalRetenues());
        payslip.setNetAPayer(netAPayer);

        payslip.setStatut("brouillon");
        payslip.setCreatedAt(java.time.LocalDateTime.now());

        return Optional.of(payslipsRhRepository.save(payslip));
    }

    private BigDecimal calculateSalaireBase(Long personnelId, Integer mois, Integer annee) {
        return contractsRhService.getSalaireBase(personnelId, mois, annee);
    }

    private BigDecimal calculateTotalPrimes(Long personnelId, Integer mois, Integer annee) {
        return salaryComponentsService.calculateTotalPrimes(personnelId, mois, annee);
    }

    private BigDecimal calculateHeuresSupplementaires(Long personnelId, Integer mois, Integer annee) {
        return (BigDecimal) (paie.exportDataForPaie(personnelId, mois, annee).get("totalMontantHs"));
    }

    private void calculateRetenues(PayslipsRh payslip, BigDecimal totalBrut) {
        LocalDate currentDate = LocalDate.now();

        // Calcul CNAPS (1% employé, 13% employeur)
        BigDecimal tauxCnapsEmployee = getTauxByNomParam("CNAPS_EMPLOYEE");
        BigDecimal cnapsEmployee = totalBrut.multiply(tauxCnapsEmployee).divide(BigDecimal.valueOf(100));
        payslip.setCnapsEmployee(cnapsEmployee);

        BigDecimal tauxCnapsEmployer = getTauxByNomParam("CNAPS_EMPLOYER");
        BigDecimal cnapsEmployer = totalBrut.multiply(tauxCnapsEmployer).divide(BigDecimal.valueOf(100));
        payslip.setCnapsEmployer(cnapsEmployer);

        // Calcul OSTIE (1% employé, 5% employeur)
        BigDecimal tauxOstieEmployee = getTauxByNomParam("OSTIE_EMPLOYEE");
        BigDecimal ostieEmployee = totalBrut.multiply(tauxOstieEmployee).divide(BigDecimal.valueOf(100));
        payslip.setOstieEmployee(ostieEmployee);

        BigDecimal tauxOstieEmployer = getTauxByNomParam("OSTIE_EMPLOYER");
        BigDecimal ostieEmployer = totalBrut.multiply(tauxOstieEmployer).divide(BigDecimal.valueOf(100));
        payslip.setOstieEmployer(ostieEmployer);

        // Calcul IRSA (progressif par tranches - à implémenter complètement)
        BigDecimal irsa = calculateIRSA(totalBrut);
        payslip.setIrsa(irsa);

        // Total retenues
        BigDecimal totalRetenues = cnapsEmployee.add(ostieEmployee).add(irsa);
        payslip.setTotalRetenues(totalRetenues);
    }

    private BigDecimal getTauxByNomParam(String nomParam) {
        return salaryParametersRhRepository.findByNomParametre(nomParam)
                .map(SalaryParametersRh::getValeur)
                .orElseThrow(() -> new IllegalArgumentException("Paramètre salaire introuvable : " + nomParam));
    }

    private BigDecimal calculateIRSA(BigDecimal totalBrutMensuel) {

        // Récupérer les tranches IRSA actives, ordonnées par seuil_min
        List<SalaryParametersRh> tranches = salaryParametersRhRepository
                .findActiveParametersByCategorieAndDate("irsa", LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(t -> t.getSeuilMin() == null ? BigDecimal.ZERO : t.getSeuilMin()))
                .collect(Collectors.toList());
        BigDecimal irsa = BigDecimal.ZERO;

        for (SalaryParametersRh tranche : tranches) {
            BigDecimal seuilMin = tranche.getSeuilMin();
            BigDecimal seuilMax = tranche.getSeuilMax();
            BigDecimal taux = tranche.getValeur();
            BigDecimal tmp = totalBrutMensuel.multiply(taux.divide(new BigDecimal(100)));
            irsa = irsa.add(tmp);
            if (seuilMax != null) {
                if (seuilMin.compareTo(totalBrutMensuel) <= 0 && seuilMax.compareTo(totalBrutMensuel) >= 0) {
                    break;
                }
            } else {
                if (seuilMin.compareTo(totalBrutMensuel) <= 0) {
                    break;
                }

            }

        }

        // Retourner l'IRSA mensuel
        return irsa;
    }

    public List<PayslipsRh> getPayslipsByPersonnel(Long personnelId) {
        return payslipsRhRepository.findByPersonnel_IdOrderByAnneeDescMoisDesc(personnelId);
    }

    public List<PayslipsRh> getPayslipsByPeriod(Integer mois, Integer annee) {
        return payslipsRhRepository.findByMoisAndAnnee(mois, annee);
    }
}