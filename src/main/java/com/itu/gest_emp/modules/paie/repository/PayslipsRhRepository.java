package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.PayslipsRh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipsRhRepository extends JpaRepository<PayslipsRh, Long> {

    Optional<PayslipsRh> findByPersonnel_IdAndMoisAndAnnee(Long personnelId, Integer mois, Integer annee);

    List<PayslipsRh> findByPersonnel_IdOrderByAnneeDescMoisDesc(Long personnelId);

    List<PayslipsRh> findByMoisAndAnnee(Integer mois, Integer annee);

    @Query("SELECT p FROM PayslipsRh p WHERE p.personnel.id = :personnelId AND p.annee = :annee ORDER BY p.mois")
    List<PayslipsRh> findByPersonnelIdAndAnnee(@Param("personnelId") Long personnelId, @Param("annee") Integer annee);

    boolean existsByPersonnel_IdAndMoisAndAnnee(Long personnelId, Integer mois, Integer annee);
}