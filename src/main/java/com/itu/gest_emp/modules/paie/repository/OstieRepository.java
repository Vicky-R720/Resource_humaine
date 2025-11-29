package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.Ostie;
import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OstieRepository extends JpaRepository<Ostie, Long> {
    

    
    
    // Récupérer tous les paramètres actifs
    List<Ostie> findByActifTrueOrderByDateDebutValiditeDesc();



     @Query("""
        SELECT o FROM Ostie o
        WHERE o.actif = true
          AND o.dateDebutValidite <= :date
          AND (o.dateFinValidite IS NULL OR o.dateFinValidite >= :date)
    """)
    Optional<Ostie> findByDateValidite(@Param("date") LocalDate date);



     boolean existsByDateDebutValidite(LocalDate dateDebutValidite);
}