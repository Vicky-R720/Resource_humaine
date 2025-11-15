package com.itu.gest_emp.modules.paie.repository;

import com.itu.gest_emp.modules.paie.model.SalaryComponentsRh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryComponentsRhRepository extends JpaRepository<SalaryComponentsRh, Long> {

        List<SalaryComponentsRh> findByPersonnel_Id(Long personnelId);

        List<SalaryComponentsRh> findByPersonnel_IdAndTypeComposante(Long personnelId, String typeComposante);

        @Query("""
                            SELECT s
                            FROM SalaryComponentsRh s
                            WHERE s.personnel.id = :personnelId
                              AND s.statut = 'actif'
                              AND (
                                    s.isRecurring = true
                                    OR (
                                        (s.dateDebut IS NULL OR s.dateDebut <= :date)
                                        AND
                                        (s.dateFin IS NULL OR s.dateFin >= :date)
                                    )
                              )
                        """)
        List<SalaryComponentsRh> findActiveComponentsByPersonnelAndDate(
                        @Param("personnelId") Long personnelId,
                        @Param("date") LocalDate date);

        @Query("SELECT s FROM SalaryComponentsRh s WHERE s.personnel.id = :personnelId AND s.statut = 'actif' " +
                        "AND s.isRecurring = true")
        List<SalaryComponentsRh> findRecurringComponentsByPersonnel(@Param("personnelId") Long personnelId);

        List<SalaryComponentsRh> findByTypeComposanteAndStatut(String typeComposante, String statut);

        @Query("SELECT s FROM SalaryComponentsRh s WHERE s.statut = 'actif' AND " +
                        "(:date BETWEEN s.dateDebut AND s.dateFin OR s.isRecurring = true)")
        List<SalaryComponentsRh> findActiveComponentsByDate(@Param("date") LocalDate date);

        @Query("SELECT DISTINCT s.typeComposante FROM SalaryComponentsRh s")
        List<String> findDistinctTypeComposante();
}