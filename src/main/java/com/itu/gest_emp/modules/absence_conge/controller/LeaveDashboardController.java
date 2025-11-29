package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.itu.gest_emp.modules.absence_conge.dto.*;
import com.itu.gest_emp.modules.absence_conge.mapper.LeaveMapper;
import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/leave-dashboard")
public class LeaveDashboardController {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    /**
     * Tableau de bord des soldes de congés
     */
    @GetMapping("/balances/{personnelId}")
    public ResponseEntity<LeaveBalanceDashboardDto> getLeaveBalances(@PathVariable Long personnelId) {

        List<LeaveBalance> balances = leaveBalanceRepository.findByPersonnel_Id(personnelId);

        LeaveBalanceDashboardDto dashboard = new LeaveBalanceDashboardDto();
        dashboard.setBalances(LeaveMapper.toBalanceDtos(balances));
        dashboard.setTotalRemaining(calculateTotalRemaining(balances));

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Historique des congés par employé
     */
    @GetMapping("/history/{personnelId}")
    public ResponseEntity<List<LeaveHistoryDto>> getLeaveHistory(@PathVariable Long personnelId) {

        List<LeaveRequest> history = leaveRequestRepository.findByPersonnel_Id(personnelId);

        return ResponseEntity.ok(LeaveMapper.toHistoryDtos(history));
    }

    private BigDecimal calculateTotalRemaining(List<LeaveBalance> balances) {
        return balances.stream()
                .map(LeaveBalance::getSoldeRestant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
