package com.itu.gest_emp.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.AttendanceRH;

@Repository
public interface AttendanceRHRepository extends JpaRepository<AttendanceRH, Long> {
        List<AttendanceRH> findByPersonnelIdAndDatePointageAfter(Long personnelId, LocalDate date);

        List<AttendanceRH> findByPersonnelIdAndDatePointageBetween(Long personnelId, LocalDate start, LocalDate end);

        Optional<AttendanceRH> findByPersonnelIdAndDatePointage(Long personnelId, LocalDate datePointage);

        List<AttendanceRH> findByPersonnelIdOrderByDatePointageDesc(Long personnelId);

        // CORRECTION : Utiliser LocalTime au lieu de String dans les requêtes
        @Query("SELECT a FROM AttendanceRH a WHERE a.personnel.id = :personnelId AND a.datePointage >= :startDate AND a.heureArrivee > :heureLimite")
        List<AttendanceRH> findRetardsByPersonnelAfterDate(@Param("personnelId") Long personnelId,
                        @Param("startDate") LocalDate startDate,
                        @Param("heureLimite") LocalTime heureLimite);

        @Query("SELECT a FROM AttendanceRH a WHERE a.personnel.id = :personnelId AND a.datePointage >= :startDate AND a.dureeTravailMinutes > :dureeLimite")
        List<AttendanceRH> findHeuresSupExcessives(@Param("personnelId") Long personnelId,
                        @Param("startDate") LocalDate startDate,
                        @Param("dureeLimite") Integer dureeLimite);
}