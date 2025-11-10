package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Diploma;
import com.itu.gest_emp.model.Filiere;
import com.itu.gest_emp.repository.DiplomaRepository;
import com.itu.gest_emp.repository.FiliereRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FiliereService {
    
    @Autowired
    private FiliereRepository filiereRepository;
    
    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }
    
    public Filiere getFiliereById(Long id) {
        return filiereRepository.findById(id).orElse(null);
    }
    
    public Filiere createFiliere(Filiere diploma) {
        return filiereRepository.save(diploma);
    }
    
    public void deleteFiliere(Long id) {
        filiereRepository.deleteById(id);
    }
}