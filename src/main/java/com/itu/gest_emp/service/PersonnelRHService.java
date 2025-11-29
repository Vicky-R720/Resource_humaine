package com.itu.gest_emp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.model.PersonnelRH;
import com.itu.gest_emp.repository.PersonnelRHRepository;


@Service
public class PersonnelRHService {

    @Autowired
    private PersonnelRHRepository personnelRHRepository;

    public List<PersonnelRH> getAllPersonnel() {
        return personnelRHRepository.findAll();
    }

    public PersonnelRH getPersonnelById(Long id) {
        return personnelRHRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
    }
}