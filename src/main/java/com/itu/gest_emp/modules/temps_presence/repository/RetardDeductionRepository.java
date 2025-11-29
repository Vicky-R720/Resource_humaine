package com.itu.gest_emp.modules.temps_presence.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.temps_presence.model.RetardDeduction;

public interface RetardDeductionRepository extends JpaRepository<RetardDeduction, Long> {

    RetardDeduction findByPersonnelAndDatePointage(PersonnelRh personnel, LocalDate datePointage);

}
