package com.itu.gest_emp.repository;
import com.itu.gest_emp.model.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface PayslipsRHRepository extends JpaRepository<PayslipsRH, Long> {
    List<PayslipsRH> findByPersonnelIdOrderByAnneeDescMoisDesc(Long personnelId);
    Optional<PayslipsRH> findByPersonnelIdAndMoisAndAnnee(Long personnelId, Integer mois, Integer annee);

    List<PayslipsRH> findByPersonnelIdAndStatut(Long personnelId, String statut);
    

    List<PayslipsRH> findTop3ByPersonnelIdOrderByAnneeDescMoisDesc(Long personnelId);

   
}