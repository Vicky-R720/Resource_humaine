package com.itu.gest_emp.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.itu.gest_emp.modules.shared.model.Service;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>{

}
