package com.itu.gest_emp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.model.CV;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {
    List<CV> findByStatut(String statut);

    Optional<CV> findByPersonId(Long personId);
}
