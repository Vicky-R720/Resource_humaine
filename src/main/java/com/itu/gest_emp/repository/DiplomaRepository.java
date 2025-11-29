package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Diploma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiplomaRepository extends JpaRepository<Diploma, Long> {
    List<Diploma> findAllByOrderByNameAsc();
  
}