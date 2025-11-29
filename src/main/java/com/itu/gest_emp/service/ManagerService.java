package com.itu.gest_emp.service;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itu.gest_emp.repository.*;
import com.itu.gest_emp.model.*;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class ManagerService {
    
    private final PersonnelRHRepository personnelRHRepository;
    private final LeaveRequestsRHRepository leaveRequestsRHRepository;
    private final AuthenticationService authenticationService;
    
    public ManagerService(PersonnelRHRepository personnelRHRepository,
                         LeaveRequestsRHRepository leaveRequestsRHRepository,
                         AuthenticationService authenticationService) {
        this.personnelRHRepository = personnelRHRepository;
        this.leaveRequestsRHRepository = leaveRequestsRHRepository;
        this.authenticationService = authenticationService;
    }
    
    // Récupérer tous les employés (équipe du manager)
    public List<PersonnelRH> getTeamMembers() {
        // Le manager voit tous les employés sauf lui-même
        List<PersonnelRH> allPersonnel = personnelRHRepository.findByStatut("actif");
        PersonnelRH currentManager = authenticationService.getCurrentPersonnel();
        
        return allPersonnel.stream()
                .filter(p -> !p.getId().equals(currentManager.getId()))
                .collect(Collectors.toList());
    }
    
    // Récupérer les demandes de congés en attente de l'équipe
    public List<LeaveRequestsRH> getPendingLeaveRequests() {
        List<PersonnelRH> teamMembers = getTeamMembers();
        List<Long> teamPersonnelIds = teamMembers.stream()
                .map(PersonnelRH::getId)
                .collect(Collectors.toList());
        
        if (teamPersonnelIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return leaveRequestsRHRepository.findPendingRequestsByPersonnelIds(teamPersonnelIds);
    }
    
    // Valider une demande de congé
    public LeaveRequestsRH approveLeaveRequest(Long requestId, String comment) {
        LeaveRequestsRH request = leaveRequestsRHRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande de congé non trouvée"));
        
        // Vérifier que la demande appartient à un membre de l'équipe
        List<PersonnelRH> teamMembers = getTeamMembers();
        boolean isTeamMember = teamMembers.stream()
                .anyMatch(member -> member.getId().equals(request.getPersonnel().getId()));
        
        if (!isTeamMember) {
            throw new RuntimeException("Vous ne pouvez pas valider cette demande");
        }
        
        request.setStatut("approuve");
        request.setValidationComment(comment);
        request.setValidationDate(LocalDateTime.now());
        request.setValidatedBy(authenticationService.getCurrentPersonnel().getPerson());
        
        return leaveRequestsRHRepository.save(request);
    }
    
    // Refuser une demande de congé
    public LeaveRequestsRH rejectLeaveRequest(Long requestId, String comment) {
        LeaveRequestsRH request = leaveRequestsRHRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande de congé non trouvée"));
        
        // Vérifier que la demande appartient à un membre de l'équipe
        List<PersonnelRH> teamMembers = getTeamMembers();
        boolean isTeamMember = teamMembers.stream()
                .anyMatch(member -> member.getId().equals(request.getPersonnel().getId()));
        
        if (!isTeamMember) {
            throw new RuntimeException("Vous ne pouvez pas refuser cette demande");
        }
        
        request.setStatut("refuse");
        request.setValidationComment(comment);
        request.setValidationDate(LocalDateTime.now());
        request.setValidatedBy(authenticationService.getCurrentPersonnel().getPerson());
        
        return leaveRequestsRHRepository.save(request);
    }
    
    // Statistiques simplifiées pour le tableau de bord
    public Map<String, Object> getManagerDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<PersonnelRH> teamMembers = getTeamMembers();
        List<LeaveRequestsRH> pendingRequests = getPendingLeaveRequests();
        
        stats.put("teamSize", teamMembers.size());
        stats.put("pendingLeaveRequests", pendingRequests.size());
        
        // Statistiques simulées pour les performances
        stats.put("averagePerformance", 75.5); // Valeur simulée
        stats.put("activeAlerts", 2); // Valeur simulée
        
        return stats;
    }
    
    // Données de performance simulées
    public Map<String, Object> getTeamPerformance(LocalDate startDate, LocalDate endDate) {
        List<PersonnelRH> teamMembers = getTeamMembers();
        
        Map<String, Object> performanceData = new HashMap<>();
        performanceData.put("teamMembers", teamMembers.size());
        performanceData.put("evaluationsCount", teamMembers.size() * 2); // Simulé
        
        // Scores simulés
        performanceData.put("averageScore", 78.2);
        
        // Distribution des scores simulée
        Map<String, Long> scoreDistribution = new HashMap<>();
        scoreDistribution.put("Excellent", 2L);
        scoreDistribution.put("Très bon", 5L);
        scoreDistribution.put("Bon", 8L);
        scoreDistribution.put("Satisfaisant", 3L);
        scoreDistribution.put("À améliorer", 1L);
        performanceData.put("scoreDistribution", scoreDistribution);
        
        return performanceData;
    }
    
    // Données de présence simulées
    public Map<String, Object> getTeamAttendance(LocalDate startDate, LocalDate endDate) {
        List<PersonnelRH> teamMembers = getTeamMembers();
        
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("totalDays", 30L); // Simulé
        attendanceData.put("teamSize", teamMembers.size());
        attendanceData.put("totalAbsences", 12L); // Simulé
        
        // Absences par employé simulées
        Map<String, Long> absencesByEmployee = new HashMap<>();
        for (PersonnelRH member : teamMembers) {
            String name = member.getPerson().getNom() + " " + member.getPerson().getPrenom();
            absencesByEmployee.put(name, (long) (Math.random() * 5)); // 0-4 absences simulées
        }
        attendanceData.put("absencesByEmployee", absencesByEmployee);
        
        return attendanceData;
    }
    
    // Alertes simulées
    public List<Map<String, Object>> getManagerAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        // Alerte simulée 1
        Map<String, Object> alert1 = new HashMap<>();
        alert1.put("id", 1L);
        alert1.put("typeAlerte", "Absence non justifiée");
        alert1.put("message", "Jean Dupont a 3 absences non justifiées ce mois");
        alert1.put("severite", "warning");
        alert1.put("personnelNom", "Jean Dupont");
        alerts.add(alert1);
        
        // Alerte simulée 2
        Map<String, Object> alert2 = new HashMap<>();
        alert2.put("id", 2L);
        alert2.put("typeAlerte", "Performance en baisse");
        alert2.put("message", "Marie Martin a une baisse de performance détectée");
        alert2.put("severite", "info");
        alert2.put("personnelNom", "Marie Martin");
        alerts.add(alert2);
        
        return alerts;
    }
}