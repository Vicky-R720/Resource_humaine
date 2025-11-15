package com.itu.gest_emp.modules.absence_conge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.absence_conge.model.LeaveBalance;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByPersonnel_Id(Long personnelId);

    Optional<LeaveBalance> findByPersonnel_IdAndLeaveTypeIdAndAnnee(Long personnelId, Long leaveTypeId, Integer annee);

    List<LeaveBalance> findByAnnee(Integer annee);
}