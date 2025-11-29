package com.itu.gest_emp.repository;


import com.itu.gest_emp.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface PerformancePredictionRHRepository extends JpaRepository<PerformancePredictionRH, Long> {
    List<PerformancePredictionRH> findByPersonnelIdOrderByDatePredictionDesc(Long personnelId);

    List<PerformancePredictionRH> findByTypePredictionAndDatePredictionAfter(String typePrediction, LocalDate date);

    Optional<PerformancePredictionRH> findTopByPersonnelIdAndTypePredictionOrderByDatePredictionDesc(Long personnelId,
            String typePrediction);
}
