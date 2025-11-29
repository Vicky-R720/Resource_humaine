package com.itu.gest_emp.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itu.gest_emp.service.*;
import com.itu.gest_emp.model.*;
import com.itu.gest_emp.repository.CandidateMatchingRepository;
import com.itu.gest_emp.repository.OfferRepository;
import com.itu.gest_emp.repository.PerformancePredictionRHRepository;
import com.itu.gest_emp.repository.PersonnelRHRepository;

import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    
    private final ManagerService managerService;
    private final AuthenticationService authenticationService;
    
    public ManagerController(ManagerService managerService, AuthenticationService authenticationService) {
        this.managerService = managerService;
        this.authenticationService = authenticationService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            Map<String, Object> dashboardStats = managerService.getManagerDashboardStats();
            List<Map<String, Object>> activeAlerts = managerService.getManagerAlerts();
            List<LeaveRequestsRH> pendingRequests = managerService.getPendingLeaveRequests();
            
            model.addAttribute("dashboardStats", dashboardStats);
            model.addAttribute("activeAlerts", activeAlerts);
            model.addAttribute("pendingRequests", pendingRequests);
            
            return "manager/dashboard";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/team")
    public String teamManagement(Model model) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            List<PersonnelRH> teamMembers = managerService.getTeamMembers();
            model.addAttribute("teamMembers", teamMembers);
            
            return "manager/team";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/leaves")
    public String leaveRequestsManagement(Model model) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            List<LeaveRequestsRH> pendingRequests = managerService.getPendingLeaveRequests();
            model.addAttribute("pendingRequests", pendingRequests);
            
            return "manager/leaves";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/leaves/approve/{id}")
    public String approveLeaveRequest(@PathVariable Long id, 
                                    @RequestParam String comment,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            managerService.approveLeaveRequest(id, comment);
            redirectAttributes.addFlashAttribute("success", "Demande de congé approuvée avec succès");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'approbation: " + e.getMessage());
        }
        return "redirect:/manager/leaves";
    }
    
    @PostMapping("/leaves/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Long id,
                                   @RequestParam String comment,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            managerService.rejectLeaveRequest(id, comment);
            redirectAttributes.addFlashAttribute("success", "Demande de congé refusée");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du refus: " + e.getMessage());
        }
        return "redirect:/manager/leaves";
    }
    
    @GetMapping("/performance")
    public String performanceTracking(Model model,
                                    @RequestParam(defaultValue = "2025-01-01") String startDate,
                                    @RequestParam(defaultValue = "2025-12-31") String endDate) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Map<String, Object> performanceData = managerService.getTeamPerformance(start, end);
            List<PersonnelRH> teamMembers = managerService.getTeamMembers();
            
            model.addAttribute("performanceData", performanceData);
            model.addAttribute("teamMembers", teamMembers);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            
            return "manager/performance";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/attendance")
    public String attendanceTracking(Model model,
                                   @RequestParam(defaultValue = "2025-01-01") String startDate,
                                   @RequestParam(defaultValue = "2025-12-31") String endDate) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            Map<String, Object> attendanceData = managerService.getTeamAttendance(start, end);
            List<PersonnelRH> teamMembers = managerService.getTeamMembers();
            
            model.addAttribute("attendanceData", attendanceData);
            model.addAttribute("teamMembers", teamMembers);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            
            return "manager/attendance";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/alerts/resolve/{id}")
    public String resolveAlert(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!authenticationService.isManager()) {
                return "redirect:/employee/self-service/dashboard";
            }
            
            // Pour l'instant, on simule juste la résolution
            redirectAttributes.addFlashAttribute("success", "Alerte résolue");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la résolution: " + e.getMessage());
        }
        return "redirect:/manager/dashboard";
    }
    
    // CORRECTION : Retourner PersonnelRH au lieu de Person
    @ModelAttribute("currentUser")
    public PersonnelRH getCurrentUser() {
        try {
            return authenticationService.getCurrentPersonnel();
        } catch (Exception e) {
            return null;
        }
    }


    
    
    @Autowired
    private TurnoverPredictionService turnoverService;
    
    @Autowired
    private AnomalyDetectionService anomalyService;
    
    @Autowired
    private CandidateMatchingService matchingService;
    
    @Autowired
    private PersonnelRHRepository personnelRepository;
    
    @Autowired
    private PerformancePredictionRHRepository predictionRepository;
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private CandidateMatchingRepository matchingRepository;
    
    @GetMapping("/predictions")
    public String predictionsDashboard(Model model) {
        // CORRECTION : Récupérer les prédictions depuis le repository
        List<PerformancePredictionRH> predictions = predictionRepository.findAll();
        model.addAttribute("predictions", predictions);
        
        // CORRECTION : Appeler les méthodes de calcul
        model.addAttribute("highRiskCount", calculateHighRiskCount(predictions));
        model.addAttribute("mediumRiskCount", calculateMediumRiskCount(predictions));
        model.addAttribute("lowRiskCount", calculateLowRiskCount(predictions));
        model.addAttribute("totalAnalyzed", predictions.size());
        
        return "manager/turnover-predictions";
    }
    
    @GetMapping("/anomalies")
    public String anomaliesDashboard(Model model,
                                   @RequestParam(required = false) String severite,
                                   @RequestParam(required = false) String statut,
                                   @RequestParam(required = false) String type) {
        
        // CORRECTION : Utiliser la nouvelle méthode de filtrage
        List<AnomalieDetection> anomalies = anomalyService.getAnomaliesFiltrees(severite, statut, type);
        model.addAttribute("anomalies", anomalies);
        
        // CORRECTION : Appeler les méthodes de comptage
        model.addAttribute("criticalCount", countBySeverite(anomalies, "critical"));
        model.addAttribute("highCount", countBySeverite(anomalies, "high"));
        model.addAttribute("mediumCount", countBySeverite(anomalies, "medium"));
        model.addAttribute("lowCount", countBySeverite(anomalies, "low"));
        model.addAttribute("resolvedCount", countByStatut(anomalies, "resolu"));
        model.addAttribute("totalCount", anomalies.size());
        
        return "manager/anomalies-detection";
    }
    
    @GetMapping("/recruitment")
    public String recruitmentDashboard(Model model) {
        // CORRECTION : Récupérer les offres depuis le repository
        List<Offer> offers = offerRepository.findAll();
        model.addAttribute("offers", offers);
        
        // CORRECTION : Récupérer les derniers matchings
        List<CandidateMatching> recentMatches = matchingRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("matches", recentMatches);
        
        // Ajouter les statistiques
        if (recentMatches != null && !recentMatches.isEmpty()) {
            model.addAttribute("topCandidateCount", countTopCandidates(recentMatches));
            model.addAttribute("goodCandidateCount", countGoodCandidates(recentMatches));
            model.addAttribute("averageCandidateCount", countAverageCandidates(recentMatches));
        } else {
            model.addAttribute("topCandidateCount", 0);
            model.addAttribute("goodCandidateCount", 0);
            model.addAttribute("averageCandidateCount", 0);
        }
        
        return "manager/candidate-matching";
    }
    
    // MÉTHODES HELPER POUR LES STATISTIQUES
    
    private long calculateHighRiskCount(List<PerformancePredictionRH> predictions) {
        return predictions.stream()
            .filter(p -> p.getScorePrediction() != null && 
                        p.getScorePrediction().compareTo(new BigDecimal("0.7")) > 0)
            .count();
    }
    
    private long calculateMediumRiskCount(List<PerformancePredictionRH> predictions) {
        return predictions.stream()
            .filter(p -> p.getScorePrediction() != null && 
                        p.getScorePrediction().compareTo(new BigDecimal("0.4")) > 0 &&
                        p.getScorePrediction().compareTo(new BigDecimal("0.7")) <= 0)
            .count();
    }
    
    private long calculateLowRiskCount(List<PerformancePredictionRH> predictions) {
        return predictions.stream()
            .filter(p -> p.getScorePrediction() != null && 
                        p.getScorePrediction().compareTo(new BigDecimal("0.4")) <= 0)
            .count();
    }
    
    private long countBySeverite(List<AnomalieDetection> anomalies, String severite) {
        return anomalies.stream()
            .filter(a -> severite.equals(a.getSeverite()))
            .count();
    }
    
    private long countByStatut(List<AnomalieDetection> anomalies, String statut) {
        return anomalies.stream()
            .filter(a -> statut.equals(a.getStatut()))
            .count();
    }
    
    private long countTopCandidates(List<CandidateMatching> matches) {
        return matches.stream()
            .filter(m -> m.getScoreTotal() != null && 
                        m.getScoreTotal().compareTo(new BigDecimal("80")) > 0)
            .count();
    }
    
    private long countGoodCandidates(List<CandidateMatching> matches) {
        return matches.stream()
            .filter(m -> m.getScoreTotal() != null && 
                        m.getScoreTotal().compareTo(new BigDecimal("60")) > 0 &&
                        m.getScoreTotal().compareTo(new BigDecimal("80")) <= 0)
            .count();
    }
    
    private long countAverageCandidates(List<CandidateMatching> matches) {
        return matches.stream()
            .filter(m -> m.getScoreTotal() != null && 
                        m.getScoreTotal().compareTo(new BigDecimal("60")) <= 0)
            .count();
    }
    
    // AUTRES MÉTHODES DU CONTRÔLEUR
    
    @PostMapping("/predictions/turnover/{personnelId}")
    public String predictTurnover(@PathVariable Long personnelId, RedirectAttributes redirectAttributes) {
        try {
            PerformancePredictionRH prediction = turnoverService.predictTurnoverRisk(personnelId);
            redirectAttributes.addFlashAttribute("success", 
                "Prédiction générée - Score: " + prediction.getScorePrediction().multiply(new BigDecimal(100)) + "%");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la prédiction");
        }
        return "redirect:/manager/predictions";
    }
    
    @PostMapping("/anomalies/detect/{personnelId}")
    public String detectAnomalies(@PathVariable Long personnelId, RedirectAttributes redirectAttributes) {
        try {
            List<AnomalieDetection> anomalies = anomalyService.detectAnomalies(personnelId);
            redirectAttributes.addFlashAttribute("success", 
                anomalies.size() + " anomalies détectées");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la détection");
        }
        return "redirect:/manager/anomalies";
    }
    
    @PostMapping("/recruitment/match")
    public String matchCandidates(@RequestParam Long offerId, Model model) {
        try {
            List<CandidateMatching> matches = matchingService.matchCandidatesWithOffer(offerId);
            model.addAttribute("matches", matches);
            model.addAttribute("offer", offerRepository.findById(offerId).orElse(null));
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du matching");
        }
        return "manager/candidate-matching";
    }
}
