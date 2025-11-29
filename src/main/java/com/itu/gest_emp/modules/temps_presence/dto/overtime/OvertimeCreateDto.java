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

    // On passe maintenant l'ID du type d'HS
    private Long typeHsId;

    private BigDecimal salaireHoraireBase;
}
