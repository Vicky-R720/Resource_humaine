package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveBalanceRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
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
    public ResponseEntity<?> getLeaveBalances(@PathVariable Long personnelId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByPersonnel_Id(personnelId);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("balances", balances);
        dashboard.put("totalRemaining", calculateTotalRemaining(balances));

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Historique des congés par employé
     */
    @GetMapping("/history/{personnelId}")
    public ResponseEntity<?> getLeaveHistory(@PathVariable Long personnelId) {
        List<LeaveRequest> history = leaveRequestRepository.findByPersonnel_Id(personnelId);
        return ResponseEntity.ok(history);
    }

    private BigDecimal calculateTotalRemaining(List<LeaveBalance> balances) {
        return balances.stream()
                .map(LeaveBalance::getSoldeRestant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
