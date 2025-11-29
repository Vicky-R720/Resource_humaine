package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractsRhRepository extends JpaRepository<ContractsRh, Long> {
  List<ContractsRh> findByPersonnelId(Long personnelId);

  @Query("""
          SELECT c FROM ContractsRh c
          WHERE c.personnel.id = :personnelId
            AND c.dateDebut <= :date
            AND (c.dateFin IS NULL OR c.dateFin >= :date)
          ORDER BY c.dateDebut DESC, c.dateFin DESC
      """)
  List<ContractsRh> findContract(
      Long personnelId,
      LocalDate date);

  List<ContractsRh> findByStatut(String statut);

  List<ContractsRh> findByDateFinBefore(LocalDate date);

  List<ContractsRh> findByContractType_CodeAndStatut(String code, String statut);

  List<ContractsRh> findByDateFin(LocalDate dateLimit);

  List<ContractsRh> findByPersonnelAndContractType_Code(PersonnelRh personnel, String string);

  List<ContractsRh> findByPersonnelIdOrderByDateDebutAsc(Long id);

  Optional<ContractsRh> findTopByPersonnelIdOrderByDateDebutDesc(Long personnelId);
}