package com.itu.gest_emp.modules.absence_conge.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LeaveStatusConverter implements AttributeConverter<LeaveRequest.LeaveStatus, String> {

    @Override
    public String convertToDatabaseColumn(LeaveRequest.LeaveStatus attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public LeaveRequest.LeaveStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return LeaveRequest.LeaveStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
