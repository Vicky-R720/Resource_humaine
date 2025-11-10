package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Diploma;
import com.itu.gest_emp.model.Sector;
import com.itu.gest_emp.repository.DiplomaRepository;
import com.itu.gest_emp.repository.SectorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SectorService {
    
    @Autowired
    private SectorRepository sectorRepository;
    
    public List<Sector> getAllSectors() {
        return sectorRepository.findAll();
    }
    public Sector getSectorById(Long id) {
        return sectorRepository.findById(id).orElse(null);
    }
  
}