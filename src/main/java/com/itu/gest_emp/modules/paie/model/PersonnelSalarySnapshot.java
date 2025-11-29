package com.itu.gest_emp.modules.paie.model;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonnelSalarySnapshot {
    private Long personnelId;
    private PersonnelRh personnel;
    private YearMonth period;
    private BigDecimal salaireBase;
    private BigDecimal heuresMensuelles; // from CNAPS
    private BigDecimal tauxHoraire;
    private BigDecimal tauxJournalier;

    public BigDecimal toHourly() {
        return tauxHoraire;
    }

    public BigDecimal toDaily() {
        return tauxJournalier;
    }
}
