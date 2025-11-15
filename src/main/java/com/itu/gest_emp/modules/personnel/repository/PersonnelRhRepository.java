package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRhRepository extends JpaRepository<PersonnelRh, Long> {
    Optional<PersonnelRh> findByMatricule(String matricule);
    List<PersonnelRh> findByStatut(String statut);
    Optional<PersonnelRh> findByPersonId(Long personId);
}