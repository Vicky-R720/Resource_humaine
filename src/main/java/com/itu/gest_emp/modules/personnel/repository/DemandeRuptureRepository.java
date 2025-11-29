package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itu.gest_emp.modules.personnel.model.DemandeRuptureContrat;

public interface DemandeRuptureRepository extends JpaRepository<DemandeRuptureContrat,Long>{
    
}
