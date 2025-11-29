package com.itu.gest_emp.modules.temps_presence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itu.gest_emp.modules.temps_presence.model.TypeHs;

public interface TypeHsRepository extends JpaRepository<TypeHs, Long> {

    TypeHs findByCode(String code);

}
