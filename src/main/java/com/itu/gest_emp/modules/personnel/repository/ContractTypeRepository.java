package com.itu.gest_emp.modules.personnel.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.ContractType;

import java.util.Optional;

@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, Long> {
    

    Optional<ContractType> findByCode(String name);
    boolean existsByCode(String name);

}