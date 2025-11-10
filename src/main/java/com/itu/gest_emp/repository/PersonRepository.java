package com.itu.gest_emp.repository;
import com.itu.gest_emp.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    List<Person> findByNomContainingIgnoreCase(String nom);
    
    List<Person> findByPrenomContainingIgnoreCase(String prenom);
    
    @Query("SELECT p FROM Person p WHERE LOWER(CONCAT(p.prenom, ' ', p.nom)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Person> findByFullNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    List<Person> findByOrderByNomAsc();
    Person findByContact(String contact);
    boolean existsByContact(String contact);

    // Filtrage par adresse/localisation
List<Person> findByAdresseContainingIgnoreCase(String location);

// Filtrage par âge (via date de naissance)
@Query("SELECT p FROM Person p WHERE YEAR(CURRENT_DATE) - YEAR(p.naissance) BETWEEN :ageMin AND :ageMax")
List<Person> findByAgeBetween(@Param("ageMin") int ageMin, @Param("ageMax") int ageMax);
}
