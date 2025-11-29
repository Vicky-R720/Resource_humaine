package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.AvanceAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvanceActionRepository extends JpaRepository<AvanceAction, Long> {
    List<AvanceAction> findByAvance_IdOrderByActionDateDesc(Long avanceId);
}
