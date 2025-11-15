package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractsRhRepository extends JpaRepository<ContractsRh, Long> {
    List<ContractsRh> findByPersonnelId(Long personnelId);

    @Query("""
                SELECT c FROM ContractsRh c
                WHERE c.personnel.id = :personnelId
                  AND c.statut = :statut
                  AND c.dateDebut <= :date
                  AND (c.dateFin IS NULL OR c.dateFin >= :date)
                ORDER BY c.dateDebut DESC, c.dateFin DESC
            """)
    List<ContractsRh> findActiveContract(
            Long personnelId,
            LocalDate date,
            String statut);

    List<ContractsRh> findByStatut(String statut);

    List<ContractsRh> findByDateFinBefore(LocalDate date);

    List<ContractsRh> findByTypeContratAndStatut(String typeContrat, String statut);

    List<ContractsRh> findByDateFin(LocalDate dateLimit);
}