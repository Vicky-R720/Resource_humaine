package com.itu.gest_emp.modules.absence_conge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest.LeaveStatus;

import lombok.Data;

@Data
public class LeaveHistoryDto {

    private Long id;

    private Long leaveTypeId;
    private String leaveTypeName;
    private String motif;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal nombreJours;

    private LeaveStatus statut;
    private LocalDateTime createdAt;
}
