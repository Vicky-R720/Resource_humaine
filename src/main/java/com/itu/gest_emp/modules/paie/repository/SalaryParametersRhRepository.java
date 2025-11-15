package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.SalaryParametersRh;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryParametersRhRepository extends JpaRepository<SalaryParametersRh, Long> {
    
    Optional<SalaryParametersRh> findByNomParametre(String nomParametre);

    

    List<SalaryParametersRh> findByCategorie(String categorie);
    
    List<SalaryParametersRh> findByIsActiveTrue();
    
    @Query("SELECT s FROM SalaryParametersRh s WHERE s.isActive = true AND " +
           "(:date BETWEEN s.dateDebutValidite AND s.dateFinValidite OR " +
           "s.dateFinValidite IS NULL AND s.dateDebutValidite <= :date)")
    List<SalaryParametersRh> findActiveParametersByDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM SalaryParametersRh s WHERE s.categorie = :categorie AND s.isActive = true AND " +
           "(:date BETWEEN s.dateDebutValidite AND s.dateFinValidite OR " +
           "s.dateFinValidite IS NULL AND s.dateDebutValidite <= :date)")
    List<SalaryParametersRh> findActiveParametersByCategorieAndDate(@Param("categorie") String categorie, 
                                                                   @Param("date") LocalDate date);
}