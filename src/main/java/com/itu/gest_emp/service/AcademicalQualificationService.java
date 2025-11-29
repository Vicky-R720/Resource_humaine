package com.itu.gest_emp.service;

import com.itu.gest_emp.model.AcademicalQualification;
import java.util.List;

public interface AcademicalQualificationService {
    
    List<AcademicalQualification> getQualificationsByPersonId(Long personId);
    
    AcademicalQualification saveQualification(AcademicalQualification qualification);
    
    void deleteQualification(Long id);
    
    AcademicalQualification getQualificationById(Long id);
    
    List<AcademicalQualification> getAllQualifications();
}