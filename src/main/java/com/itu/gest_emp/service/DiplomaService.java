package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Diploma;
import com.itu.gest_emp.repository.DiplomaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiplomaService {
    
    @Autowired
    private DiplomaRepository diplomaRepository;
    
    public List<Diploma> getAllDiplomas() {
        return diplomaRepository.findAllByOrderByNameAsc();
    }
    
    public Diploma getDiplomaById(Long id) {
        return diplomaRepository.findById(id).orElse(null);
    }
    
    public Diploma createDiploma(Diploma diploma) {
        return diplomaRepository.save(diploma);
    }
    
    public void deleteDiploma(Long id) {
        diplomaRepository.deleteById(id);
    }
}