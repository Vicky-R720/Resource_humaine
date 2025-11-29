package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.Avance;
import com.itu.gest_emp.modules.paie.model.AvanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvanceRepository extends JpaRepository<Avance, Long> {
    List<Avance> findByPersonnel_IdAndStatutAndDateDecisionBetween(Long personnelId, AvanceStatus statut, LocalDateTime start, LocalDateTime end);
    List<Avance> findByPersonnel_IdAndStatut(Long personnelId, AvanceStatus statut);
    List<Avance> findByStatut(AvanceStatus statut);
}
