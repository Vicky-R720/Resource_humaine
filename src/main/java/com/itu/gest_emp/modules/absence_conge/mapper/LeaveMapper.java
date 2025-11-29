package com.itu.gest_emp.modules.absence_conge.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.itu.gest_emp.modules.absence_conge.dto.*;
import com.itu.gest_emp.modules.absence_conge.model.*;

public class LeaveMapper {

    public static LeaveBalanceDto toBalanceDto(LeaveBalance b) {
        LeaveBalanceDto dto = new LeaveBalanceDto();

        dto.setId(b.getId());
        dto.setAnnee(b.getAnnee());

        dto.setLeaveTypeId(b.getLeaveType().getId());
        dto.setLeaveTypeName(b.getLeaveType().getName());

        dto.setSoldeInitial(b.getSoldeInitial());
        dto.setSoldeAcquis(b.getSoldeAcquis());
        dto.setSoldePris(b.getSoldePris());
        dto.setSoldeRestant(b.getSoldeRestant());

        dto.setCreatedAt(b.getCreatedAt());
        dto.setUpdatedAt(b.getUpdatedAt());

        return dto;
    }

    public static List<LeaveBalanceDto> toBalanceDtos(List<LeaveBalance> balances) {
        return balances.stream()
                .map(LeaveMapper::toBalanceDto)
                .collect(Collectors.toList());
    }

    public static LeaveHistoryDto toHistoryDto(LeaveRequest l) {
        LeaveHistoryDto dto = new LeaveHistoryDto();

        dto.setId(l.getId());
        dto.setMotif(l.getMotif());

        dto.setLeaveTypeId(l.getLeaveType().getId());
        dto.setLeaveTypeName(l.getLeaveType().getName());

        dto.setDateDebut(l.getDateDebut());
        dto.setDateFin(l.getDateFin());
        dto.setNombreJours(l.getNombreJours());

        dto.setStatut(l.getStatut());
        dto.setCreatedAt(l.getCreatedAt());

        return dto;
    }

    public static List<LeaveHistoryDto> toHistoryDtos(List<LeaveRequest> history) {
        return history.stream()
                .map(LeaveMapper::toHistoryDto)
                .collect(Collectors.toList());
    }
}
