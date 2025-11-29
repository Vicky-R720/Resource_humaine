package com.itu.gest_emp.modules.absence_conge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LeaveBalanceDto {

    private Long id;
    private Long leaveTypeId;
    private String leaveTypeName;
    private Integer annee;

    private BigDecimal soldeInitial;
    private BigDecimal soldeAcquis;
    private BigDecimal soldePris;
    private BigDecimal soldeRestant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
