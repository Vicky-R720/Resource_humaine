package com.itu.gest_emp.modules.absence_conge.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class LeaveBalanceDashboardDto {

    private List<LeaveBalanceDto> balances;
    private BigDecimal totalRemaining;
}
