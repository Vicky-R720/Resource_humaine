package com.itu.gest_emp.modules.personnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;


import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRhRepository extends JpaRepository<PersonnelRh, Long> {
    Optional<PersonnelRh> findByMatricule(String matricule);

    List<PersonnelRh> findByStatut(String statut);

    Optional<PersonnelRh> findByPerson_Id(Long personId);



    @Query("""
                SELECT p
                FROM PersonnelRh p
                WHERE p.post.equipe.service.id = :serviceId
            """)
    List<PersonnelRh> findByServiceId(@Param("serviceId") Long serviceId);

    @Query("""
                SELECT p
                FROM PersonnelRh p
                WHERE p.post.equipe.id = :equipeId
            """)
    List<PersonnelRh> findByEquipeId(@Param("equipeId") Long equipeId);
            
    // List<PersonnelRh> findAbsentsSansConges(LocalDate aujourdhui);

}