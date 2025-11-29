package com.itu.gest_emp.repository;
import com.itu.gest_emp.model.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EmployeeRequestsRHRepository extends JpaRepository<EmployeeRequestsRH, Long> {
    List<EmployeeRequestsRH> findByPersonnelIdOrderByCreatedAtDesc(Long personnelId);
    List<EmployeeRequestsRH> findByPersonnelIdAndStatut(Long personnelId, String statut);
    List<EmployeeRequestsRH> findByTypeDemande(String typeDemande);
}
