package com.itu.gest_emp.repository;
import com.itu.gest_emp.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface LeaveRequestsRHRepository extends JpaRepository<LeaveRequestsRH, Long> {
    List<LeaveRequestsRH> findByPersonnelIdOrderByCreatedAtDesc(Long personnelId);
    List<LeaveRequestsRH> findByPersonnelIdAndStatut(Long personnelId, String statut);
    List<LeaveRequestsRH> findByDateDebutBetween(LocalDate start, LocalDate end);
    
    // SUPPRIMER les méthodes qui utilisent des champs inexistants :
    // List<LeaveRequestsRH> findByPersonnelPostIdAndStatut(Long postId, String statut);
    // List<LeaveRequestsRH> findPendingRequestsByPostIds(@Param("postIds") List<Long> postIds);
    
    // GARDER ou AJOUTER cette méthode qui utilise des champs existants :
    @Query("SELECT lr FROM LeaveRequestsRH lr WHERE lr.personnel.id IN :personnelIds AND lr.statut = 'en_attente'")
    List<LeaveRequestsRH> findPendingRequestsByPersonnelIds(@Param("personnelIds") List<Long> personnelIds);
    
    // Ajouter une méthode pour trouver toutes les demandes en attente
    List<LeaveRequestsRH> findByStatutOrderByCreatedAtDesc(String statut);
}