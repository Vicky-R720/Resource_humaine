package com.itu.gest_emp.repository;


import com.itu.gest_emp.model.*;
import com.itu.gest_emp.model.Filiere;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ManagerDashboardRHRepository extends JpaRepository<ManagerDashboardRH, Long> {
    List<ManagerDashboardRH> findByManagerIdOrderByCreatedAtDesc(Long managerId);
    List<ManagerDashboardRH> findByManagerIdAndIsResolved(Long managerId, Boolean isResolved);
    List<ManagerDashboardRH> findByManagerIdAndSeverite(Long managerId, String severite);
    
    @Query("SELECT m FROM ManagerDashboardRH m WHERE m.manager.id = :managerId AND m.isResolved = false ORDER BY m.createdAt DESC")
    List<ManagerDashboardRH> findActiveAlertsByManagerId(Long managerId);
    
    @Query("SELECT m FROM ManagerDashboardRH m WHERE m.manager.id = :managerId AND m.personnel.id = :personnelId ORDER BY m.createdAt DESC")
    List<ManagerDashboardRH> findByManagerIdAndPersonnelId(Long managerId, Long personnelId);
}