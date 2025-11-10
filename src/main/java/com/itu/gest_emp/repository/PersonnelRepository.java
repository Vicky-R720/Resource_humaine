package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    
    /**
     * Rechercher le personnel par nom ou prénom (insensible à la casse)
     */
    @Query("SELECT p FROM Personnel p WHERE " +
           "LOWER(CONCAT(p.personne.prenom, ' ', p.personne.nom)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY p.dateEmbauche DESC")
    List<Personnel> findByPersonneNomOrPrenomContaining(@Param("searchTerm") String searchTerm);
    
    /**
     * Compter les embauches entre deux dates
     */
    @Query("SELECT COUNT(p) FROM Personnel p WHERE p.dateEmbauche BETWEEN :startDate AND :endDate")
    long countByDateEmbaucheBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
}