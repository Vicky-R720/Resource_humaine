package com.itu.gest_emp.modules.temps_presence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRh, Long> {
    
    Optional<AttendanceRh> findByPersonnel_IdAndDatePointage(Long personnelId, LocalDate datePointage);
    
    List<AttendanceRh> findByPersonnel_IdAndDatePointageBetween(Long personnelId, LocalDate startDate, LocalDate endDate);
    
    List<AttendanceRh> findByDatePointageBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT a FROM AttendanceRh a WHERE a.personnel.id = :personnelId AND EXTRACT(MONTH FROM a.datePointage) = :month AND EXTRACT(YEAR FROM a.datePointage) = :year")
    List<AttendanceRh> findByPersonnelIdAndMonthYear(@Param("personnelId") Long personnelId, 
                                                   @Param("month") int month, 
                                                   @Param("year") int year);
    
    @Query("SELECT a FROM AttendanceRh a WHERE a.statut = 'retard' AND a.datePointage BETWEEN :startDate AND :endDate")
    List<AttendanceRh> findRetardsBetweenDates(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    Long countByPersonnel_IdAndStatutAndDatePointageBetween(Long personnelId, String statut, LocalDate startDate, LocalDate endDate);
}