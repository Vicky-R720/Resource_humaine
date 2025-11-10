package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplianceRepository extends JpaRepository<Appliance, Long> {
    
    List<Appliance> findByPersonId(Long personId);
    
    List<Appliance> findByOfferId(Long offerId);
    
    @Query("SELECT a FROM Appliance a ORDER BY a.createdAt DESC")
    List<Appliance> findAllOrderByDateDesc();
    
    @Query("SELECT COUNT(a) FROM Appliance a WHERE a.offer.id = :offerId")
    Long countByOfferId(@Param("offerId") Long offerId);
}