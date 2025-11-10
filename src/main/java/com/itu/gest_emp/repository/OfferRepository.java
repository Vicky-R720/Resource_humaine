package com.itu.gest_emp.repository;

import com.itu.gest_emp.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    
    List<Offer> findByPostId(Long postId);
    List<Offer> findByContractTypeId(Long contractTypeId);
    List<Offer> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Offer> findByLocationContainingIgnoreCase(String location);
    
    @Query("SELECT o FROM Offer o ORDER BY o.publicationDate DESC")
    List<Offer> findAllOrderByPublicationDateDesc();
    
    @Query("SELECT o FROM Offer o WHERE o.experienceLevel LIKE %:level%")
    List<Offer> findByExperienceLevel(@Param("level") String level);
}