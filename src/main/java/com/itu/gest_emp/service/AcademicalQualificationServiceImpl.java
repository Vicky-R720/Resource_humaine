package com.itu.gest_emp.service;

import com.itu.gest_emp.model.AcademicalQualification;
import com.itu.gest_emp.repository.AcademicalQualificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AcademicalQualificationServiceImpl implements AcademicalQualificationService {
    
    @Autowired
    private AcademicalQualificationRepository academicalQualificationRepository;
    
    @Override
    public List<AcademicalQualification> getQualificationsByPersonId(Long personId) {
        return academicalQualificationRepository.findByPersonId(personId);
    }
    
    @Override
    public AcademicalQualification saveQualification(AcademicalQualification qualification) {
        return academicalQualificationRepository.save(qualification);
    }
    
    @Override
    public void deleteQualification(Long id) {
        academicalQualificationRepository.deleteById(id);
    }
    
    @Override
    public AcademicalQualification getQualificationById(Long id) {
        Optional<AcademicalQualification> qualification = academicalQualificationRepository.findById(id);
        return qualification.orElse(null);
    }
    
    @Override
    public List<AcademicalQualification> getAllQualifications() {
        return academicalQualificationRepository.findAll();
    }
}