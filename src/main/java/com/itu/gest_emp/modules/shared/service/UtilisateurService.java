package com.itu.gest_emp.modules.shared.service;


import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.repository.UtilisateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur saveUtilisateur(Utilisateur Utilisateur) {
        return utilisateurRepository.save(Utilisateur);
    }

    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    

    public Utilisateur createUtilisateur(Utilisateur Utilisateur) {
        return utilisateurRepository.save(Utilisateur);
    }




    public Utilisateur findByEmailAndMotDePasse(String email,String motDePasse) {
        return utilisateurRepository.findByEmailAndMotDePasse(email,motDePasse);
    }

    public Utilisateur findById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public Utilisateur findByPersonId(Long personId) {
        return utilisateurRepository.findByPerson_Id(personId);
    }
}