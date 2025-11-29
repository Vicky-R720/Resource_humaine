package com.itu.gest_emp.modules.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.itu.gest_emp.modules.shared.model.Utilisateur;

import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
  Utilisateur findByPerson_Id(Long personId);
  Utilisateur findByEmailAndMotDePasse(String email,String motDePasse);
}