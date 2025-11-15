package com.itu.gest_emp.modules.temps_presence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<OvertimeRh, Long> {


    List<OvertimeRh> findByStatut(String statut);

    @Query("SELECT o FROM OvertimeRh o WHERE o.personnel.id = :personnelId AND EXTRACT(MONTH FROM o.dateHs) = :month AND EXTRACT(YEAR FROM o.dateHs) = :year")
    List<OvertimeRh> findByPersonnelIdAndMonthYear(@Param("personnelId") Long personnelId,
            @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT SUM(o.montantHs) FROM OvertimeRh o WHERE o.personnel.id = :personnelId AND o.statut = 'paye' AND o.dateHs BETWEEN :startDate AND :endDate")
    BigDecimal findTotalMontantHsPayeByPersonnel(@Param("personnelId") Long personnelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<OvertimeRh> findByDateHsBetweenAndStatut(LocalDate startDate, LocalDate endDate, String string);

    List<OvertimeRh> findByPersonnel_Id(Long personnelId);

    List<OvertimeRh> findByPersonnel_IdAndDateHsBetween(Long personnelId, LocalDate startDate, LocalDate endDate);

    // List<Object[]> findHeuresMensuellesParPersonnel(LocalDate startDate, LocalDate endDate);
}