package com.itu.gest_emp.modules.absence_conge.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class LeaveCalculationService {

    /**
     * Calcule le nombre de jours ouvrés entre deux dates
     */
    public BigDecimal calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        long workingDays = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            if (isWorkingDay(date)) {
                workingDays++;
            }
            date = date.plusDays(1);
        }

        return BigDecimal.valueOf(workingDays);
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Calcule les jours de congés acquis selon la règle 2.5 jours/mois
     */
    public BigDecimal calculateAcquiredDays(int monthsWorked) {
        return BigDecimal.valueOf(monthsWorked * 2.5);
    }

    /**
     * Vérifie si une période chevauche une autre demande
     */
    public boolean hasOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    /**
     * Simule le solde de congés année par année pour un employé
     * 
     * @param initialSoldeInitial solde initial au départ
     * @param yearsWorked         nombre d'années à simuler
     * @param joursPrisParAn      congés pris chaque année (BigDecimal)
     * @return Map année -> tableau [soldeInitial, soldeAcquis, soldePris,
     *         soldeRestant]
     */
    public Map<Integer, BigDecimal[]> simulateLeaveBalance(BigDecimal initialSoldeInitial, int yearsWorked,
            BigDecimal joursPrisParAn) {
        Map<Integer, BigDecimal[]> result = new HashMap<>();
        BigDecimal soldeInitial = initialSoldeInitial;
        BigDecimal maxCumul = BigDecimal.valueOf(2.5 * 12 * 3); // plafond 3 ans cumulables = 90 jours

        for (int year = 1; year <= yearsWorked; year++) {
            BigDecimal soldeAcquis = BigDecimal.valueOf(12 * 2.5); // 2.5 jours/mois * 12 mois

            // Appliquer le plafond cumulable
            if (soldeInitial.add(soldeAcquis).compareTo(maxCumul) > 0) {
                soldeAcquis = maxCumul.subtract(soldeInitial);
                if (soldeAcquis.compareTo(BigDecimal.ZERO) < 0)
                    soldeAcquis = BigDecimal.ZERO;
            }

            BigDecimal soldeRestant = soldeInitial.add(soldeAcquis).subtract(joursPrisParAn);

            // Enregistrer pour cette année
            result.put(year, new BigDecimal[] { soldeInitial, soldeAcquis, joursPrisParAn, soldeRestant });

            // Préparer l'année suivante
            soldeInitial = soldeRestant;
        }

        return result;
    }

    public Map<Integer, BigDecimal[]> simulateLeaveBalance(BigDecimal initialSoldeInitial, int yearsWorked,
            Map<Integer, BigDecimal> joursPrisParAn) {
        Map<Integer, BigDecimal[]> result = new HashMap<>();
        BigDecimal soldeInitial = initialSoldeInitial;
        BigDecimal maxCumul = BigDecimal.valueOf(2.5 * 12 * 3); // plafond 3 ans cumulables = 90 jours

        for (int year = 1; year <= yearsWorked; year++) {
            BigDecimal soldeAcquis = BigDecimal.valueOf(12 * 2.5); // 2.5 jours/mois * 12 mois

            // Appliquer le plafond cumulable
            if (soldeInitial.add(soldeAcquis).compareTo(maxCumul) > 0) {
                soldeAcquis = maxCumul.subtract(soldeInitial);
                if (soldeAcquis.compareTo(BigDecimal.ZERO) < 0)
                    soldeAcquis = BigDecimal.ZERO;
            }

            BigDecimal soldeRestant = soldeInitial.add(soldeAcquis).subtract(joursPrisParAn.get(year));

            // Enregistrer pour cette année
            result.put(year, new BigDecimal[] { soldeInitial, soldeAcquis, joursPrisParAn.get(year), soldeRestant });

            // Préparer l'année suivante
            soldeInitial = soldeRestant;
        }

        return result;
    }
}
