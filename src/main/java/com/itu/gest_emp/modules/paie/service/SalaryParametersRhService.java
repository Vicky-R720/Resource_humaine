package com.itu.gest_emp.modules.paie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.gest_emp.modules.paie.model.SalaryParametersRh;
import com.itu.gest_emp.modules.paie.repository.SalaryParametersRhRepository;

@Service
public class SalaryParametersRhService {
    @Autowired
    private SalaryParametersRhRepository salaryParametersRhRepository;

    public SalaryParametersRh findByNomParametre(String nom) {
        return salaryParametersRhRepository.findByNomParametre(nom).orElseThrow();
    }
}
