package com.itu.gest_emp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.AnomalieDetection;

@Repository
public interface AnomalieDetectionRepository extends JpaRepository<AnomalieDetection, Long> {
    List<AnomalieDetection> findByPersonnelIdAndStatut(Long personnelId, String statut);

    List<AnomalieDetection> findByStatut(String statut);

    List<AnomalieDetection> findBySeverite(String severite);

    List<AnomalieDetection> findBySeveriteAndStatut(String severite, String statut);
}
