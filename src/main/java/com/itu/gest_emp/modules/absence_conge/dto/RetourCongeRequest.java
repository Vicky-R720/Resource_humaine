package com.itu.gest_emp.modules.absence_conge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RetourCongeRequest {
    private LocalDate dateRetour;
    private String justification;
}