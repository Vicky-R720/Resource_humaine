package com.itu.gest_emp.modules.absence_conge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.absence_conge.model.AbsencePV;

@Repository
public interface AbsencePVRepository extends JpaRepository<AbsencePV, Long> {

}
