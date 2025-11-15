package com.itu.gest_emp.modules.temps_presence.dto.attendance;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceDto {
    private Long id;
    private Long personnelId;
    private String personnelNomComplet;
    private LocalDate datePointage;
    private LocalTime heureArrivee;
    private LocalTime heureDepart;
    private LocalTime heurePauseDebut;
    private LocalTime heurePauseFin;
    private Integer dureeTravailMinutes;
    private String statut;
    private String typePointage;
    private String commentaire;
    private Boolean isValidated;
    private Integer retardMinutes;
    private Integer dureePauseMinutes;
}

