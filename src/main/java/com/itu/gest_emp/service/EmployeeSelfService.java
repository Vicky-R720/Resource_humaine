package com.itu.gest_emp.service;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.itu.gest_emp.repository.*;
import com.itu.gest_emp.model.*;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class EmployeeSelfService {
    
    private final PersonnelRHRepository personnelRHRepository;
    private final LeaveRequestsRHRepository leaveRequestsRHRepository;
    private final LeaveBalanceRHRepository leaveBalanceRHRepository;
    private final PayslipsRHRepository payslipsRHRepository;
    private final EmployeeRequestsRHRepository employeeRequestsRHRepository;

    public EmployeeSelfService(PersonnelRHRepository personnelRHRepository,
                             LeaveRequestsRHRepository leaveRequestsRHRepository,
                             LeaveBalanceRHRepository leaveBalanceRHRepository,
                             PayslipsRHRepository payslipsRHRepository,
                             EmployeeRequestsRHRepository employeeRequestsRHRepository) {
        this.personnelRHRepository = personnelRHRepository;
        this.leaveRequestsRHRepository = leaveRequestsRHRepository;
        this.leaveBalanceRHRepository = leaveBalanceRHRepository;
        this.payslipsRHRepository = payslipsRHRepository;
        this.employeeRequestsRHRepository = employeeRequestsRHRepository;
    }

    // Gestion des informations personnelles
    public Optional<PersonnelRH> getPersonnelByPersonId(Long personId) {
        return personnelRHRepository.findByPersonId(personId);
    }

    public PersonnelRH updatePersonalInfo(Long personnelId, PersonnelRH updatedInfo) {
        PersonnelRH existing = personnelRHRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        
        // Mise à jour des champs modifiables par l'employé
        if (updatedInfo.getPerson().getContact() != null) {
            existing.getPerson().setContact(updatedInfo.getPerson().getContact());
        }
        if (updatedInfo.getPerson().getAdresse() != null) {
            existing.getPerson().setAdresse(updatedInfo.getPerson().getAdresse());
        }
        if (updatedInfo.getPersonneUrgenceNom() != null) {
            existing.setPersonneUrgenceNom(updatedInfo.getPersonneUrgenceNom());
        }
        if (updatedInfo.getPersonneUrgenceContact() != null) {
            existing.setPersonneUrgenceContact(updatedInfo.getPersonneUrgenceContact());
        }
        if (updatedInfo.getPersonneUrgenceLien() != null) {
            existing.setPersonneUrgenceLien(updatedInfo.getPersonneUrgenceLien());
        }
        if (updatedInfo.getRib() != null) {
            existing.setRib(updatedInfo.getRib());
        }
        if (updatedInfo.getBanque() != null) {
            existing.setBanque(updatedInfo.getBanque());
        }
        
        return personnelRHRepository.save(existing);
    }

    // Gestion des congés
    public List<LeaveRequestsRH> getEmployeeLeaveRequests(Long personnelId) {
        return leaveRequestsRHRepository.findByPersonnelIdOrderByCreatedAtDesc(personnelId);
    }

    public LeaveRequestsRH submitLeaveRequest(LeaveRequestsRH leaveRequest) {
        // Calcul automatique du nombre de jours
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(leaveRequest.getDateDebut(), leaveRequest.getDateFin()) + 1;
        leaveRequest.setNombreJours((double) daysBetween);
        
        return leaveRequestsRHRepository.save(leaveRequest);
    }

    public void cancelLeaveRequest(Long requestId) {
        LeaveRequestsRH request = leaveRequestsRHRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande de congé non trouvée"));
        
        if ("en_attente".equals(request.getStatut())) {
            request.setStatut("annule");
            leaveRequestsRHRepository.save(request);
        }
    }

    // Consultation des soldes de congés
    public List<LeaveBalanceRH> getLeaveBalances(Long personnelId) {
        return leaveBalanceRHRepository.findByPersonnelId(personnelId);
    }

    // CORRECTION : Utiliser java.util.Map explicitement
    public java.util.Map<String, Object> getLeaveSummary(Long personnelId) {
        List<LeaveBalanceRH> balances = leaveBalanceRHRepository.findByPersonnelIdAndAnnee(personnelId, java.time.LocalDate.now().getYear());
        
        double totalSoldeRestant = balances.stream()
                .mapToDouble(LeaveBalanceRH::getSoldeRestant)
                .sum();
        
        long pendingRequests = leaveRequestsRHRepository
                .findByPersonnelIdAndStatut(personnelId, "en_attente")
                .size();
        
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalSoldeRestant", totalSoldeRestant);
        summary.put("pendingRequests", pendingRequests);
        summary.put("balances", balances);
        
        return summary;
    }

    // Consultation des bulletins de paie
    public List<PayslipsRH> getEmployeePayslips(Long personnelId) {
        return payslipsRHRepository.findByPersonnelIdOrderByAnneeDescMoisDesc(personnelId);
    }

    public Optional<PayslipsRH> getPayslip(Long personnelId, Integer mois, Integer annee) {
        return payslipsRHRepository.findByPersonnelIdAndMoisAndAnnee(personnelId, mois, annee);
    }

    // Gestion des demandes employés
    public List<EmployeeRequestsRH> getEmployeeRequests(Long personnelId) {
        return employeeRequestsRHRepository.findByPersonnelIdOrderByCreatedAtDesc(personnelId);
    }

    public EmployeeRequestsRH submitEmployeeRequest(EmployeeRequestsRH request) {
        return employeeRequestsRHRepository.save(request);
    }

    public List<EmployeeRequestsRH> getRequestsByStatus(Long personnelId, String statut) {
        return employeeRequestsRHRepository.findByPersonnelIdAndStatut(personnelId, statut);
    }
}