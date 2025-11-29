package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.Cnaps;
import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CnapsRepository extends JpaRepository<Cnaps, Long> {
    
    // Récupérer les paramètres actifs par secteur
    List<Cnaps> findBySecteurActiviteAndActifTrue(SecteurActiviteEnum secteurActivite);
    
    // Récupérer les paramètres valides à une date donnée
    @Query("SELECT c FROM Cnaps c WHERE c.secteurActivite = :secteur " +
           "AND c.actif = true " +
           "AND c.dateDebutValidite <= :date " +
           "AND (c.dateFinValidite IS NULL OR c.dateFinValidite >= :date)")
    Optional<Cnaps> findBySecteurAndDateValidite(
        @Param("secteur") SecteurActiviteEnum secteur,
        @Param("date") LocalDate date
    );
    
    // Récupérer le paramètre actif le plus récent pour un secteur
    @Query("SELECT c FROM Cnaps c WHERE c.secteurActivite = :secteur " +
           "AND c.actif = true " +
           "ORDER BY c.dateDebutValidite DESC")
    List<Cnaps> findMostRecentBySecteur(@Param("secteur") SecteurActiviteEnum secteur);
    
    // Récupérer tous les paramètres actifs
    List<Cnaps> findByActifTrueOrderByDateDebutValiditeDesc();
    
    // Vérifier s'il existe déjà un paramètre pour un secteur à une date
    @Query("SELECT COUNT(c) > 0 FROM Cnaps c WHERE c.secteurActivite = :secteur " +
           "AND c.dateDebutValidite = :date")
    boolean existsBySecteurAndDate(
        @Param("secteur") SecteurActiviteEnum secteur,
        @Param("date") LocalDate date
    );
}