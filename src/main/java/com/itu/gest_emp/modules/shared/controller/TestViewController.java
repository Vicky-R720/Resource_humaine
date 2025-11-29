package com.itu.gest_emp.modules.shared.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;
import com.itu.gest_emp.modules.absence_conge.service.LeaveCalculationService;

@Controller
public class TestViewController {
    @Autowired
    private LeaveCalculationService leaveCalculationService;

    @PostMapping("test/balance/simulate")
    public ResponseEntity<?> simulateView(@RequestBody Map<String, Object> payload) {

        try {
            BigDecimal initialSolde = new BigDecimal(payload.get("initialSolde").toString());
            int years = Integer.parseInt(payload.get("years").toString());

            // 1️⃣ Récupérer la liste des jours pris par année depuis le JSON
            List<Number> joursPrisList = (List<Number>) payload.get("joursPrisParAn");
            Map<Integer, BigDecimal> joursPrisParAn = new HashMap<>();
            for (int i = 0; i < joursPrisList.size(); i++) {
                joursPrisParAn.put(i + 1, BigDecimal.valueOf(joursPrisList.get(i).doubleValue()));
            }

            // 2️⃣ Récupérer la liste des acquis par année depuis le JSON
            List<Number> soldeAcquisList = (List<Number>) payload.get("soldeAcquisParAn");
            Map<Integer, BigDecimal> soldeAcquisParAn = new HashMap<>();
            for (int i = 0; i < soldeAcquisList.size(); i++) {
                soldeAcquisParAn.put(i + 1, BigDecimal.valueOf(soldeAcquisList.get(i).doubleValue()));
            }

            // 3️⃣ Appel du service avec les deux maps
            Map<Integer, BigDecimal[]> result = leaveCalculationService.simulateLeaveBalance(
                    initialSolde,
                    years,
                    soldeAcquisParAn,
                    joursPrisParAn);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/test/balance/simulate-form")
    public String balanceView() {
        return "modules/absence_conge/simulate-form";
    }
}
