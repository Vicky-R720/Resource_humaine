package com.itu.gest_emp.modules.temps_presence.dto.overtime;

import lombok.Data;

@Data
public class OvertimeValidationDto {
    private Boolean approuve;
    private String commentaire;
}