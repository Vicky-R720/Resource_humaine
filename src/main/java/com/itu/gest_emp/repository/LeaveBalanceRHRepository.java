package com.itu.gest_emp.repository;
import com.itu.gest_emp.model.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface LeaveBalanceRHRepository extends JpaRepository<LeaveBalanceRH, Long> {
    Optional<LeaveBalanceRH> findByPersonnelIdAndLeaveTypeIdAndAnnee(Long personnelId, Long leaveTypeId, Integer annee);
    List<LeaveBalanceRH> findByPersonnelIdAndAnnee(Long personnelId, Integer annee);
    List<LeaveBalanceRH> findByPersonnelId(Long personnelId);
}