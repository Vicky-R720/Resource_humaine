package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.AcademicalQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AcademicalQualificationRepository extends JpaRepository<AcademicalQualification, Long> {
    
    @Query("SELECT aq FROM AcademicalQualification aq " +
           "JOIN aq.appliance a " +
           "JOIN a.person p " +
           "WHERE p.id = :personId")
    List<AcademicalQualification> findByPersonId(@Param("personId") Long personId);
    
    @Query("SELECT aq FROM AcademicalQualification aq " +
           "WHERE aq.appliance.person.id = :personId")
    List<AcademicalQualification> findByAppliancePersonId(@Param("personId") Long personId);
}