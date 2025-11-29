package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Cnaps;
import com.itu.gest_emp.modules.paie.model.PersonnelSalarySnapshot;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.shared.service.CompanyInfoRhService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SalarySnapshotService {

    private final PersonnelRhService personnelRhService;
    private final ContractsRhService contractsRhService;
    private final CompanyInfoRhService companyInfoRhService;
    private final CnapsService cnapsService;

    // Simple in-memory cache for the duration of JVM. Key = personnelId + '_' +
    // year + '_' + month
    private final Map<String, PersonnelSalarySnapshot> cache = new ConcurrentHashMap<>();

    public PersonnelSalarySnapshot getSnapshot(Long personnelId, Integer mois, Integer annee) {
        String key = personnelId + "_" + annee + "_" + mois;
        return cache.computeIfAbsent(key, k -> buildSnapshot(personnelId, mois, annee));
    }

    private PersonnelSalarySnapshot buildSnapshot(Long personnelId, Integer mois, Integer annee) {
        PersonnelRh personnel = personnelRhService.findById(personnelId)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable : " + personnelId));

        YearMonth ym = YearMonth.of(annee, mois);
        LocalDate date = ym.atEndOfMonth();

        BigDecimal salaireBase = contractsRhService.getSalaireBase(personnelId, mois, annee);

        Cnaps cnaps = cnapsService.load(companyInfoRhService.getSecteurActivite(), date);
        BigDecimal heuresMensuelles = cnaps.getHeuresMensuel();

        BigDecimal tauxHoraire = BigDecimal.ZERO;
        if (salaireBase != null && heuresMensuelles != null && heuresMensuelles.compareTo(BigDecimal.ZERO) > 0) {
            tauxHoraire = salaireBase.divide(heuresMensuelles, 4, RoundingMode.HALF_UP);
        }

        BigDecimal tauxJournalier = BigDecimal.ZERO;
        if (salaireBase != null) {
            tauxJournalier = salaireBase.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
        }

        PersonnelSalarySnapshot s = new PersonnelSalarySnapshot(personnelId, personnel, ym, salaireBase,
                heuresMensuelles, tauxHoraire, tauxJournalier);
        return s;
    }

    public void clearCacheFor(Long personnelId, Integer mois, Integer annee) {
        String key = personnelId + "_" + annee + "_" + mois;
        cache.remove(key);
    }

    public void clearAll() {
        cache.clear();
    }
}
