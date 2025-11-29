package com.itu.gest_emp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itu.gest_emp.model.AnomalieDetection;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.service.AnomalyDetectionService;

@RestController
@RequestMapping("/api/anomalies")
public class AnomalyDetectionController {

    @Autowired
    private AnomalyDetectionService anomalyService;

    @GetMapping("/personnel/{personnelId}")
    public List<AnomalieDetection> getAnomalies(@PathVariable Long personnelId) {
        return anomalyService.detectAnomalies(personnelId);
    }

    @PostMapping("/detect/{personnelId}")
    public ResponseEntity<?> detectAnomaliesForPersonnel(@PathVariable Long personnelId) {
        try {
            List<AnomalieDetection> anomalies = anomalyService.detectAnomalies(personnelId);
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la détection: " + e.getMessage());
        }
    }

    private Person getPersonFromUser(User user) {
        // Implémentez la logique pour convertir User en Person
        // Pour l'instant, retournez null ou une personne par défaut
        return null;
    }
}