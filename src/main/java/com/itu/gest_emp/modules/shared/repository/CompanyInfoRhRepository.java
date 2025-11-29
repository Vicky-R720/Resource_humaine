package com.itu.gest_emp.modules.shared.repository;

import com.itu.gest_emp.modules.shared.model.CompanyInfoRh;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInfoRhRepository extends JpaRepository<CompanyInfoRh, Long> {


    CompanyInfoRh findTopByOrderByIdDesc();
}
