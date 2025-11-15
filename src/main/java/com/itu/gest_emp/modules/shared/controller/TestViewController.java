package com.itu.gest_emp.modules.shared.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.itu.gest_emp.modules.absence_conge.service.LeaveCalculationService;

@Controller
public class TestViewController {
    @Autowired
    private LeaveCalculationService leaveCalculationService;

    @GetMapping("/test/submit")
    public String submitView() {
        return "modules/absence_conge/submit-leave";
    }

    @GetMapping("/test/validate")
    public String validateView() {
        return "modules/absence_conge/validate-leave";
    }

    @PostMapping("test/balance/simulate")
    public ResponseEntity<?> simulateView(@RequestBody Map<String, Object> payload) {

        BigDecimal initialSolde = new BigDecimal(payload.get("initialSolde").toString());
        int years = Integer.parseInt(payload.get("years").toString());
        Map<Integer,BigDecimal> joursPris = payload.get("joursPrisParAn") instanceof Map ? 
            ((Map<?,?>)payload.get("joursPrisParAn")).entrySet().stream()
                .collect(
                    java.util.stream.Collectors.toMap(
                        e -> Integer.parseInt(e.getKey().toString()),
                        e -> new BigDecimal(e.getValue().toString())
                    )
                )
            : java.util.Collections.emptyMap();

        Map<Integer, BigDecimal[]> result = leaveCalculationService.simulateLeaveBalance(
                initialSolde,
                years,
                joursPris);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/test/balance/simulate-form")
    public String balanceView() {
        return "modules/absence_conge/simulate-form";
    }
}
