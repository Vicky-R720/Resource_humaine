package com.itu.gest_emp.modules.temps_presence.dto.overtime;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OvertimeDto {
    private Long id;
    private Long personnelId;
    private String personnelNomComplet;
    private Long attendanceId;
    private LocalDate dateHs;
    private BigDecimal nombreHeures;
    private String typeHs;
    private BigDecimal tauxMajoration;
    private BigDecimal montantHs;
    private String statut;
    private BigDecimal salaireHoraireBase;
}
