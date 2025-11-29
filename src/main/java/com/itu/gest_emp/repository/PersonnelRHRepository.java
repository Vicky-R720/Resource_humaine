package com.itu.gest_emp.repository;


import com.itu.gest_emp.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRHRepository extends JpaRepository<PersonnelRH, Long> {
    Optional<PersonnelRH> findByPersonId(Long personId);
    Optional<PersonnelRH> findByMatricule(String matricule);
    Optional<PersonnelRH> findByPersonContact(String contact);
    List<PersonnelRH> findByStatut(String statut);
    
    @Query("SELECT p FROM PersonnelRH p WHERE p.person.contact = :identifier OR p.matricule = :identifier")
    Optional<PersonnelRH> findByIdentifier(String identifier);
    
    // SUPPRIMER les méthodes qui utilisent des champs inexistants :
    // List<PersonnelRH> findByPostId(Long postId);
    // List<PersonnelRH> findByPostIdsAndActive(@Param("postIds") List<Long> postIds);
    // List<PersonnelRH> findByDepartmentAndActive(@Param("department") String department);
    
    // REMPLACER par des méthodes qui utilisent des champs existants :
    @Query("SELECT p FROM PersonnelRH p WHERE p.statut = 'actif'")
    List<PersonnelRH> findActivePersonnel();
    
    @Query("SELECT p FROM PersonnelRH p WHERE p.post IS NOT NULL AND p.statut = 'actif'")
    List<PersonnelRH> findActivePersonnelWithPost();
}