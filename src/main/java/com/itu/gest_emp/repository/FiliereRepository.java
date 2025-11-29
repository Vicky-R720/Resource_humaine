package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Diploma;
import com.itu.gest_emp.model.Filiere;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    List<Filiere> findAll();
}