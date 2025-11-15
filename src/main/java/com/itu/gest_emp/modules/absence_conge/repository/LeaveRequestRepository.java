package com.itu.gest_emp.modules.absence_conge.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByPersonnel_Id(Long personnelId);

    List<LeaveRequest> findByStatut(String statut);

    List<LeaveRequest> findByValidatedByIsNullAndStatut(String statut);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.dateDebut <= :date AND lr.dateFin >= :date AND lr.statut = 'approuve'")
    List<LeaveRequest> findApprovedByDate(@Param("date") LocalDate date);

    List<LeaveRequest> findByValidatedBy_IdAndStatut(Long serviceId, String string);
}