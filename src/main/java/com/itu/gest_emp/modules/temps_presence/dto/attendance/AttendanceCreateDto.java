package com.itu.gest_emp.modules.temps_presence.dto.attendance;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class AttendanceCreateDto {
    private Long personnelId;
    private LocalDate datePointage;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;
    private String typePointage = "manuel";
    private String commentaire;
}
