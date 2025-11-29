package com.itu.gest_emp.service;
import com.itu.gest_emp.repository.ApplianceRepository;
import com.itu.gest_emp.repository.PersonnelRHRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itu.gest_emp.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


    
@Service
public class AuthenticationService {
    
    private final HttpSession session;
    private final PersonnelRHRepository personnelRHRepository;
    
    public AuthenticationService(HttpSession session, PersonnelRHRepository personnelRHRepository) {
        this.session = session;
        this.personnelRHRepository = personnelRHRepository;
    }
    
    public boolean isManager() {
        try {
            PersonnelRH personnel = getCurrentPersonnel();
            // EMP002 est manager par défaut
            return personnel != null && "EMP002".equals(personnel.getMatricule());
        } catch (Exception e) {
            return false;
        }
    }
    
    public PersonnelRH getCurrentPersonnel() {
        PersonnelRH personnel = (PersonnelRH) session.getAttribute("currentPersonnel");
        if (personnel == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }
        return personnel;
    }
    
    public Person getCurrentPerson() {
        return getCurrentPersonnel().getPerson();
    }
    
    public Long getCurrentPersonId() {
        return getCurrentPersonnel().getPerson().getId();
    }
    
    public boolean isAuthenticated() {
        return session.getAttribute("currentPersonnel") != null;
    }
}