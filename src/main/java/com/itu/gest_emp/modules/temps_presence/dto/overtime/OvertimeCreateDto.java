package com.itu.gest_emp.modules.temps_presence.dto.overtime;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class OvertimeCreateDto {
    private Long personnelId;
    private Long attendanceId;
    private LocalDate dateHs;
    private BigDecimal nombreHeures;
    private String typeHs;
    private BigDecimal salaireHoraireBase;
}