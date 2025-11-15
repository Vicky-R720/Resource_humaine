package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.CareerHistoryRh;

import java.util.List;

@Repository
public interface CareerHistoryRhRepository extends JpaRepository<CareerHistoryRh, Long> {
    List<CareerHistoryRh> findByPersonnelIdOrderByDateMouvementDesc(Long personnelId);
    List<CareerHistoryRh> findByTypeMouvement(String typeMouvement);
}