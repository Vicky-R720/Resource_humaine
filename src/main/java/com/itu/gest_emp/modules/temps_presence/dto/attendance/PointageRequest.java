package com.itu.gest_emp.modules.temps_presence.dto.attendance;

import java.time.LocalTime;

import lombok.Data;

@Data
public class PointageRequest {
    private Long personnelId;
    private String typePointage; // arrivee, depart, pause_debut, pause_fin
    private LocalTime heure;
    private String commentaire;
}