package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Cnaps;
import com.itu.gest_emp.modules.paie.model.Irsa;
import com.itu.gest_emp.modules.paie.model.Ostie;
import com.itu.gest_emp.modules.paie.model.PayslipLinesRh;
import com.itu.gest_emp.modules.paie.model.PayslipsRh;
import com.itu.gest_emp.modules.paie.model.SalaryComponentsRh;
import com.itu.gest_emp.modules.paie.model.SalaryParametersRh;
import com.itu.gest_emp.modules.paie.repository.PayslipsRhRepository;
import com.itu.gest_emp.modules.paie.repository.SalaryParametersRhRepository;
import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;
import com.itu.gest_emp.modules.shared.service.CompanyInfoRhService;
import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.service.PaieIntegrationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayslipService {

    private final PersonnelRhService personnelRhService;
    private final SalaryComponentsService salaryComponentsService;
    private final PaieIntegrationService paieIntegrationService;
    private final ContractsRhService contractsRhService;
    private final CompanyInfoRhService companyInfoRhService;
    private final CnapsService cnapsService;
    private final IrsaService irsaService;
    private final OstieService ostieService;
    private final PayslipsRhRepository payslipsRhRepository;
    private final SalaryParametersRhRepository salaryParametersRhRepository;
    private final com.itu.gest_emp.modules.paie.service.AvanceService avanceService;
    private final com.itu.gest_emp.modules.paie.service.SalarySnapshotService salarySnapshotService;

    private static final String STATUT_INACTIF = "inactif";
    private static final String STATUT_BROUILLON = "brouillon";
    private static final int MULTIPLICATEUR_SEUIL_CNAPS = 8;

    @Transactional
    public Optional<PayslipsRh> generatePayslip(Long personnelId, Integer mois, Integer annee) {
        // Vérification doublon
        Optional<PayslipsRh> existing = payslipsRhRepository
                .findByPersonnel_IdAndMoisAndAnnee(personnelId, mois, annee);
        if (existing.isPresent()) {
            return existing;
        }

        LocalDate periodEndDate = YearMonth.of(annee, mois).atEndOfMonth();
        ContractsRh contrat = contractsRhService.getContrat(personnelId, periodEndDate);

        PayslipsRh payslip = initializePayslip(personnelId, mois, annee, contrat);

        calculateSalaryComponents(payslip, personnelId, mois, annee, contrat);

        generatePayslipLines(payslip);

        return Optional.of(payslipsRhRepository.save(payslip));
    }

    private PayslipsRh initializePayslip(Long personnelId, Integer mois, Integer annee, ContractsRh contrat) {
        PayslipsRh payslip = new PayslipsRh();
        payslip.setPersonnel(personnelRhService.findById(personnelId)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable : " + personnelId)));
        payslip.setMois(mois);
        payslip.setAnnee(annee);
        payslip.setStatut(STATUT_BROUILLON);
        payslip.setCreatedAt(LocalDateTime.now());

        if (isContractInactive(contrat)) {
            payslip.setIndemnitePreavis(getValueOrZero(contrat.getIndemnitePreavis()));
            payslip.setRetenuePreavis(getValueOrZero(contrat.getRetenuePreavis()));
        } else {
            payslip.setIndemnitePreavis(BigDecimal.ZERO);
            payslip.setRetenuePreavis(BigDecimal.ZERO);
        }

        return payslip;
    }

    private void calculateSalaryComponents(PayslipsRh payslip, Long personnelId,
            Integer mois, Integer annee, ContractsRh contrat) {
        // Salaire de base via snapshot (cache) pour éviter appels répétés
        com.itu.gest_emp.modules.paie.model.PersonnelSalarySnapshot snapshot = null;
        try {
            snapshot = salarySnapshotService.getSnapshot(personnelId, mois, annee);
        } catch (Exception e) {
            // fallback to direct retrieval
        }

        BigDecimal salaireBase = snapshot != null && snapshot.getSalaireBase() != null
                ? snapshot.getSalaireBase()
                : contractsRhService.getSalaireBase(personnelId, mois, annee);

        payslip.setSalaireBase(salaireBase);

        // Primes
        List<SalaryComponentsRh> activeComponents = salaryComponentsService
                .getActiveComponentsByPersonnelAndDate(personnelId, mois, annee);
        int ordre = payslip.getLignes().size() + 1;
        for (SalaryComponentsRh composante : activeComponents) {
            payslip.getLignes().add(createPrimeLine(payslip, composante, ordre++));
        }

        BigDecimal totalPrimes = salaryComponentsService.calculateTotalPrimes(activeComponents);
        payslip.setTotalPrimes(totalPrimes);

        // Export données temps & présence
        Map<String, Object> exportData = paieIntegrationService.exportDataForPaie(
                mois, annee, payslip.getPersonnel(), snapshot);

        processTimeAndAttendanceData(payslip, exportData, contrat);

        // Total brut
        BigDecimal totalBrut = salaireBase.add(totalPrimes).add(payslip.getHeuresSupplementaires());
        payslip.setTotalBrut(totalBrut);

        // Retenues
        calculateDeductions(payslip, totalBrut);

        // Avances approuvées pour la période — récupération en une seule requête
        try {
            List<com.itu.gest_emp.modules.paie.model.Avance> avancesList = avanceService.getApprovedAdvancesForPeriod(personnelId, mois, annee);
            java.math.BigDecimal totalAvances = avancesList.stream()
                    .map(a -> a.getMontant() != null ? a.getMontant() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // Stocker le total dans l'entité payslip (champ `avances`) pour reporting
            payslip.setAvances(getValueOrZero(totalAvances));

            // Ajouter une ligne détaillée par avance (évite appels DB répétés)
            int ordreAv = payslip.getLignes().size() + 1;
            for (com.itu.gest_emp.modules.paie.model.Avance av : avancesList) {
                String code = "AVANCE_" + av.getId();
                String label = "Avance (" + (av.getDateDecision() != null ? av.getDateDecision().toLocalDate().toString() : av.getDateDemande().toLocalDate().toString()) + ") - " + (av.getMotif() != null ? av.getMotif() : "");
                payslip.getLignes().add(createLine(payslip, code, label, "RETENUE", null, av.getMontant(), ordreAv++));
            }
        } catch (Exception e) {
            // ignore failures retrieving avances to avoid breaking paie generation
        }

        // Net à payer
        calculateNetPayment(payslip);
    }

    @SuppressWarnings("unchecked")
    private void processTimeAndAttendanceData(PayslipsRh payslip, Map<String, Object> exportData,
            ContractsRh contrat) {
        BigDecimal totalMontantAbsence = (BigDecimal) exportData.getOrDefault(
                "totalMontantAbsencesDeduites", BigDecimal.ZERO);
        BigDecimal totalMontantRetard = (BigDecimal) exportData.getOrDefault(
                "totalMontantRetard", BigDecimal.ZERO);
        BigDecimal totalHS = (BigDecimal) exportData.getOrDefault(
                "totalMontantHs", BigDecimal.ZERO);
        List<OvertimeRh> heuresSup = (List<OvertimeRh>) exportData.getOrDefault(
                "overtimes", new ArrayList<>());

        // Ajout des lignes d'heures supplémentaires
        int ordre = payslip.getLignes().size() + 1;
        for (OvertimeRh overtime : heuresSup) {
            payslip.getLignes().add(createOvertimeLine(payslip, overtime, ordre++));
        }

        payslip.setTotalMontantAbsence(totalMontantAbsence);
        payslip.setTotalMontantRetard(totalMontantRetard);
        payslip.setHeuresSupplementaires(totalHS);

        // Valeur droit congé uniquement si contrat inactif
        if (isContractInactive(contrat)) {
            BigDecimal valeurDroitConge = (BigDecimal) exportData.getOrDefault(
                    "valeurDroitConge", BigDecimal.ZERO);
            payslip.setValeurDroitConge(valeurDroitConge);
        } else {
            payslip.setValeurDroitConge(BigDecimal.ZERO);
        }
    }

    private void calculateDeductions(PayslipsRh payslip, BigDecimal totalBrut) {
        SecteurActiviteEnum secteur = companyInfoRhService.getSecteurActivite();

        // Calcul du plafond CNAPS
        BigDecimal smig = getParameterValue("smig");
        BigDecimal seuilCnaps = smig.multiply(BigDecimal.valueOf(MULTIPLICATEUR_SEUIL_CNAPS));
        BigDecimal baseCnaps = totalBrut.min(seuilCnaps);

        // CNAPS
        Cnaps cnaps = cnapsService.loadCurrent(secteur);
        cnaps.setSalaireBrut(baseCnaps);
        BigDecimal cnapsEmployee = cnaps.cotisationEmploye();
        BigDecimal cnapsEmployer = cnaps.cotisationEmployeur();
        payslip.setCnapsEmployee(cnapsEmployee);
        payslip.setCnapsEmployer(cnapsEmployer);

        // OSTIE
        Ostie ostie = ostieService.loadCurrent(totalBrut);
        BigDecimal ostieEmployee = ostie.cotisationEmploye();
        BigDecimal ostieEmployer = ostie.cotisationEmployeur();
        payslip.setOstieEmployee(ostieEmployee);
        payslip.setOstieEmployer(ostieEmployer);

        // Montant imposable
        BigDecimal montantImposable = totalBrut.subtract(cnapsEmployee).subtract(ostieEmployee);
        payslip.setMontantImposable(montantImposable);

        // IRSA
        List<Irsa> tranches = irsaService.getTranchesByDate(LocalDate.now());
        BigDecimal irsa = addIrsaLines(payslip, montantImposable, tranches);
        payslip.setIrsa(irsa);

        // Total retenues
        BigDecimal totalRetenues = cnapsEmployee.add(ostieEmployee).add(irsa);
        payslip.setTotalRetenues(totalRetenues);
    }

    private BigDecimal addIrsaLines(PayslipsRh payslip, BigDecimal montantImposable, List<Irsa> tranches) {
        BigDecimal irsaTotal = BigDecimal.ZERO;
        int ordre = payslip.getLignes().size() + 1;

        for (Irsa tranche : tranches) {
            BigDecimal impotTranche = irsaService.calculerIrsaPourTranche(montantImposable, tranche);
            if (impotTranche.compareTo(BigDecimal.ZERO) > 0) {
                PayslipLinesRh line = createIrsaLine(payslip, tranche, impotTranche, ordre++);
                payslip.getLignes().add(line);
                irsaTotal = irsaTotal.add(impotTranche);
            }
        }
        return irsaTotal;
    }

    private void calculateNetPayment(PayslipsRh payslip) {
        BigDecimal netAPayer = payslip.getTotalBrut()
                .subtract(payslip.getTotalRetenues())
                .add(payslip.getIndemnitePreavis())
                .subtract(payslip.getRetenuePreavis())
                .subtract(payslip.getTotalMontantAbsence())
                .subtract(payslip.getTotalMontantRetard())
                .add(payslip.getValeurDroitConge());

        // Soustraire avances (champ dédié dans PayslipsRh)
        if (payslip.getAvances() != null) {
            netAPayer = netAPayer.subtract(payslip.getAvances());
        }

        payslip.setNetAPayer(netAPayer);
    }

    private void generatePayslipLines(PayslipsRh payslip) {
        List<PayslipLinesRh> lignes = new ArrayList<>();
        int ordre = 1;

        // Gains
        lignes.add(createLine(payslip, "SB", "Salaire de base", "GAIN",
                null, payslip.getSalaireBase(), ordre++));

        if (isPositive(payslip.getTotalPrimes())) {
            lignes.add(createLine(payslip, "PRIMES", "Primes", "GAIN",
                    null, payslip.getTotalPrimes(), ordre++));
        }

        if (isPositive(payslip.getHeuresSupplementaires())) {
            lignes.add(createLine(payslip, "HS", "Heures supplémentaires", "GAIN",
                    null, payslip.getHeuresSupplementaires(), ordre++));
        }

        if (isPositive(payslip.getValeurDroitConge())) {
            lignes.add(createLine(payslip, "DROIT_CONGE", "Droits de congés", "GAIN",
                    null, payslip.getValeurDroitConge(), ordre++));
        }

        if (isPositive(payslip.getIndemnitePreavis())) {
            lignes.add(createLine(payslip, "DROIT_PREAVIS", "Indemnité préavis", "GAIN",
                    null, payslip.getIndemnitePreavis(), ordre++));
        }

        // Retenues
        if (isPositive(payslip.getRetenuePreavis())) {
            lignes.add(createLine(payslip, "RET_PREAVIS", "Retenue préavis", "RETENUE",
                    null, payslip.getRetenuePreavis(), ordre++));
        }

        if (isPositive(payslip.getTotalMontantAbsence())) {
            lignes.add(createLine(payslip, "ABS", "Absences", "RETENUE",
                    null, payslip.getTotalMontantAbsence(), ordre++));
        }

        if (isPositive(payslip.getTotalMontantRetard())) {
            lignes.add(createLine(payslip, "RETARD", "Retards", "RETENUE",
                    null, payslip.getTotalMontantRetard(), ordre++));
        }

        if (isPositive(payslip.getCnapsEmployee())) {
            lignes.add(createLine(payslip, "COT_CNAPS", "CNAPS employé", "RETENUE",
                    null, payslip.getCnapsEmployee(), ordre++));
        }

        if (isPositive(payslip.getOstieEmployee())) {
            lignes.add(createLine(payslip, "COT_OSTIE", "OSTIE employé", "RETENUE",
                    null, payslip.getOstieEmployee(), ordre++));
        }

        if (isPositive(payslip.getIrsa())) {
            lignes.add(createLine(payslip, "IRSA", "IRSA", "RETENUE",
                    null, payslip.getIrsa(), ordre++));
        }

        if (isPositive(payslip.getAutresRetenues())) {
            lignes.add(createLine(payslip, "AUTRES_RET", "Autres retenues", "RETENUE",
                    null, payslip.getAutresRetenues(), ordre++));
        }

        if (isPositive(payslip.getAutresIndemnites())) {
            lignes.add(createLine(payslip, "AUTRES_INDT", "Autres indemnités", "GAIN",
                    null, payslip.getAutresIndemnites(), ordre++));
        }

        payslip.getLignes().addAll(lignes);
    }

    // Méthodes utilitaires
    private PayslipLinesRh createLine(PayslipsRh payslip, String code, String label,
            String type, BigDecimal taux, BigDecimal montant, int ordre) {
        PayslipLinesRh line = new PayslipLinesRh();
        line.setPayslip(payslip);
        line.setCode(code);
        line.setLabel(label);
        line.setType(type);
        line.setTaux(taux);
        line.setMontant(montant);
        line.setOrdre(ordre);
        return line;
    }

    private PayslipLinesRh createPrimeLine(PayslipsRh payslip,
            SalaryComponentsRh composante, int ordre) {
        String code = "PRIME_" + composante.getId() + " " + composante.getTypeComposante();
        String label = composante.getDescription();
        return createLine(payslip, code, label, "GAIN",
                null, composante.getMontant(), ordre);
    }

    private PayslipLinesRh createOvertimeLine(PayslipsRh payslip, OvertimeRh overtime, int ordre) {
        String code = "HS_" + overtime.getId() + " " + overtime.getTypeHs().getDescription();
        String label = "Heures sup. (" + overtime.getDateHs().toString() + ")";
        return createLine(payslip, code, label, "GAIN",
                overtime.getTypeHs().getTauxMajoration(),
                overtime.getMontantHs(), ordre);
    }

    private PayslipLinesRh createIrsaLine(PayslipsRh payslip, Irsa tranche,
            BigDecimal impotTranche, int ordre) {
        String code = "IRSA" + tranche.getNumeroTranche();
        String label = String.format("Tranche IRSA de: %s à %s",
                tranche.getSeuilMin(), tranche.getSeuilMax());
        return createLine(payslip, code, label, "RETENUE",
                tranche.getTaux(), impotTranche, ordre);
    }

    private BigDecimal getParameterValue(String nomParam) {
        return salaryParametersRhRepository
                .findActiveByNomParametre(nomParam, LocalDate.now())
                .map(SalaryParametersRh::getValeur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Paramètre introuvable : " + nomParam));
    }

    private boolean isContractInactive(ContractsRh contrat) {
        return STATUT_INACTIF.equalsIgnoreCase(contrat.getStatut());
    }

    private BigDecimal getValueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    // Méthodes de consultation
    public List<PayslipsRh> getPayslipsByPersonnel(Long personnelId) {
        return payslipsRhRepository.findByPersonnel_IdOrderByAnneeDescMoisDesc(personnelId);
    }

    public List<PayslipsRh> getPayslipsByPeriod(Integer mois, Integer annee) {
        return payslipsRhRepository.findByMoisAndAnnee(mois, annee);
    }

    public PayslipsRh findById(Long id) {
        return payslipsRhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bulletin introuvable avec l'ID : " + id));
    }

    public List<PayslipsRh> findAll() {
        return payslipsRhRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        payslipsRhRepository.deleteById(id);
    }

    // Méthodes d'export (à implémenter)
    public byte[] generatePdf(Long id) {
        // TODO: Implémenter la génération PDF
        throw new UnsupportedOperationException("Génération PDF non implémentée");
    }

    public byte[] generateExcel(Long id) {
        // TODO: Implémenter la génération Excel
        throw new UnsupportedOperationException("Génération Excel non implémentée");
    }

    public PersonnelRhService getPersonnelRhService() {
        return personnelRhService;
    }
}