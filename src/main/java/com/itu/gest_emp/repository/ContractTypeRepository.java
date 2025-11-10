package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, Long> {
    
    Optional<ContractType> findByName(String name);
    boolean existsByName(String name);
}