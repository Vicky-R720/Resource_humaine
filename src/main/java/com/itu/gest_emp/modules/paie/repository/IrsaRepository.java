package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.Irsa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IrsaRepository extends JpaRepository<Irsa, Long> {

    // Récupérer toutes les tranches actives
    List<Irsa> findByActifTrueOrderByNumeroTrancheAsc();

    // Récupérer les tranches valides à une date donnée
    @Query("SELECT i FROM Irsa i WHERE i.actif = true " +
            "AND i.dateDebutValidite <= :date " +
            "AND (i.dateFinValidite IS NULL OR i.dateFinValidite >= :date) " +
            "ORDER BY i.numeroTranche ASC")
    List<Irsa> findTranchesByDateValidite(@Param("date") LocalDate date);

    // Récupérer la tranche correspondant à un montant pour une date
    @Query("SELECT i FROM Irsa i WHERE i.actif = true " +
            "AND i.dateDebutValidite <= :date " +
            "AND (i.dateFinValidite IS NULL OR i.dateFinValidite >= :date) " +
            "AND i.seuilMin <= :montant " +
            "AND (i.seuilMax IS NULL OR i.seuilMax >= :montant) " +
            "ORDER BY i.numeroTranche ASC")
    Optional<Irsa> findTrancheByMontantAndDate(
            @Param("montant") BigDecimal montant,
            @Param("date") LocalDate date);

    // Récupérer toutes les tranches pour une période de validité
    @Query("SELECT i FROM Irsa i WHERE i.dateDebutValidite = :date " +
            "ORDER BY i.numeroTranche ASC")
    List<Irsa> findByDateDebutValidite(@Param("date") LocalDate date);

    // Vérifier si une tranche existe déjà
    @Query("SELECT COUNT(i) > 0 FROM Irsa i WHERE i.numeroTranche = :numero " +
            "AND i.dateDebutValidite = :date")
    boolean existsByNumeroTrancheAndDate(
            @Param("numero") Integer numero,
            @Param("date") LocalDate date);

    // Récupérer le barème le plus récent
    @Query("SELECT i FROM Irsa i WHERE i.actif = true " +
            "AND i.dateDebutValidite = (SELECT MAX(i2.dateDebutValidite) FROM Irsa i2 WHERE i2.actif = true) " +
            "ORDER BY i.numeroTranche ASC")
    List<Irsa> findMostRecentBareme();
}