package com.itu.gest_emp.statistique.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itu.gest_emp.statistique.repository.CompetencesRepository;
import com.itu.gest_emp.statistique.model.CompetenceMatrixDTO;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import com.itu.gest_emp.statistique.model.FormationSuggestionDTO;

@Controller
@RequestMapping("/rh/competences")
public class CompetencesController {
    
    @Autowired
    private CompetencesRepository competencesRepository;
    
    /**
     * Dashboard principal des compétences
     */
    @GetMapping("/dashboard")
    public String showCompetencesDashboard(Model model) {
        System.out.println("=== ACCÈS AU DASHBOARD COMPÉTENCES ===");
        
        try {
            // RÉFÉRENTIEL COMPÉTENCES
            model.addAttribute("competencesByCategory", 
                competencesRepository.getCompetencesByCategory());
            
            model.addAttribute("topRequiredCompetences", 
                competencesRepository.getTopRequiredCompetences());
            
            model.addAttribute("mostCommonCompetences", 
                competencesRepository.getMostCommonCompetences());
            
            // NIVEAU DES COMPÉTENCES
            model.addAttribute("competenceLevels", 
                competencesRepository.getCompetenceLevelsDistribution());
            
            model.addAttribute("avgLevelByEmployee", 
                competencesRepository.getAverageCompetenceLevelByEmployee());
            
            // GAPS DE COMPÉTENCES
            model.addAttribute("competenceGaps", 
                competencesRepository.getCompetenceGaps());
            
            // FORMATIONS
            model.addAttribute("formationsByStatus", 
                competencesRepository.getFormationsByStatus());
            
            model.addAttribute("formationBudget", 
                competencesRepository.getFormationBudget());
            
            model.addAttribute("formationHours", 
                competencesRepository.getFormationHoursByEmployee());
            
            model.addAttribute("certificationRate", 
                competencesRepository.getCertificationRate());
            
            model.addAttribute("formationsByCompetence", 
                competencesRepository.getFormationsByTargetedCompetence());
            
            System.out.println("✅ Dashboard compétences chargé");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "rh/competences-dashboard";
    }
    
    /**
     * Cartographie des compétences (vue globale)
     */
    @GetMapping("/cartographie")
    public String showCartographie(Model model) {
        System.out.println("=== CARTOGRAPHIE DES COMPÉTENCES ===");
        
        try {
            model.addAttribute("competencesByCategory", 
                competencesRepository.getCompetencesByCategory());
            
            model.addAttribute("mostCommonCompetences", 
                competencesRepository.getMostCommonCompetences());
            
            model.addAttribute("competenceLevels", 
                competencesRepository.getCompetenceLevelsDistribution());
            
            System.out.println("✅ Cartographie chargée");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "rh/competences-cartographie";
    }
    
    /**
     * Matrice compétences par poste
     */
    @GetMapping("/matrice")
    public String showMatrice(Model model) {
        System.out.println("=== MATRICE COMPÉTENCES PAR POSTE ===");
        
        try {
            // Récupérer la matrice complète
            List<CompetenceMatrixDTO> matrix = 
                competencesRepository.getCompetenceMatrixByPost();
            
            model.addAttribute("competenceMatrix", matrix);
            
            // Top compétences par poste
            Map<String, List<Map<String, Object>>> topByPost = 
                competencesRepository.getTopCompetencesByPost();
            
            model.addAttribute("topCompetencesByPost", topByPost);
            
            System.out.println("✅ Matrice chargée - " + matrix.size() + " lignes");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "rh/competences-matrice";
    }
    
    /**
     * Vue détaillée pour un employé spécifique
     */
    @GetMapping("/employee/{personnelId}")
    public String showEmployeeCompetences(
            @PathVariable Long personnelId, 
            Model model) {
        
        System.out.println("=== COMPÉTENCES EMPLOYÉ ID: " + personnelId + " ===");
        
        try {
            Map<String, List<Map<String, Object>>> competences = 
                competencesRepository.getEmployeeCompetencesByCategory(personnelId);
            
            model.addAttribute("employeeCompetences", competences);
            model.addAttribute("personnelId", personnelId);
            
            System.out.println("✅ Compétences employé chargées");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "rh/competences-employee-detail";
    }
    
    /**
     * API REST - Données JSON pour graphiques dynamiques
     */
    @GetMapping("/api/data")
    @ResponseBody
    public Map<String, Object> getCompetencesData() {
        Map<String, Object> data = new java.util.HashMap<>();
        
        data.put("byCategory", competencesRepository.getCompetencesByCategory());
        data.put("levels", competencesRepository.getCompetenceLevelsDistribution());
        data.put("mostCommon", competencesRepository.getMostCommonCompetences());
        data.put("gaps", competencesRepository.getCompetenceGaps());
        
        return data;
    }
    
    /**
     * API REST - Matrice au format JSON
     */
    @GetMapping("/api/matrice")
    @ResponseBody
    public Map<String, Object> getMatriceData() {
        Map<String, Object> data = new java.util.HashMap<>();
        
        data.put("matrix", competencesRepository.getCompetenceMatrixByPost());
        data.put("topByPost", competencesRepository.getTopCompetencesByPost());
        
        return data;
    }
    
    /**
     * Export CSV de la matrice
     */
    @GetMapping("/export/matrice-csv")
    public void exportMatriceCSV(jakarta.servlet.http.HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"matrice_competences.csv\"");
            
            java.io.PrintWriter writer = response.getWriter();
            
            // En-têtes
            writer.println("Poste,Compétence,Catégorie,Niveau Moyen,Nb Personnes");
            
            // Données
            List<CompetenceMatrixDTO> matrix = 
                competencesRepository.getCompetenceMatrixByPost();
            
            for (CompetenceMatrixDTO item : matrix) {
                writer.printf("%s,%s,%s,%.1f,%d%n",
                    item.getPoste(),
                    item.getCompetence(),
                    item.getCategorie(),
                    item.getNiveauMoyen(),
                    item.getNbPersonnes()
                );
            }
            
            writer.flush();
            System.out.println("✅ Export CSV matrice réalisé");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR EXPORT: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Export CSV des gaps de compétences
     */
    @GetMapping("/export/gaps-csv")
    public void exportGapsCSV(jakarta.servlet.http.HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"gaps_competences.csv\"");
            
            java.io.PrintWriter writer = response.getWriter();
            
            // En-têtes
            writer.println("Employé,Compétence,Niveau Actuel,Niveau Requis,Gap");
            
            // Données
            List<Map<String, Object>> gaps = competencesRepository.getCompetenceGaps();
            
            for (Map<String, Object> gap : gaps) {
                writer.printf("%s,%s,%s,%s,%s%n",
                    gap.get("employe"),
                    gap.get("competence"),
                    gap.get("niveau_actuel"),
                    gap.get("niveau_requis"),
                    gap.get("gap")
                );
            }
            
            writer.flush();
            System.out.println("✅ Export CSV gaps réalisé");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR EXPORT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================
    // SUGGESTIONS DE FORMATION (NOUVEAU)
    // ============================================
    
    /**
     * Page dédiée aux suggestions de formation
     */
    @GetMapping("/suggestions")
    public String showFormationSuggestions(Model model) {
        System.out.println("=== SUGGESTIONS DE FORMATION ===");
        
        try {
            // Toutes les suggestions
            List<FormationSuggestionDTO> suggestions = 
                competencesRepository.getFormationSuggestions();
            
            model.addAttribute("suggestions", suggestions);
            
            // Suggestions groupées par priorité
            Map<String, List<FormationSuggestionDTO>> grouped = 
                competencesRepository.getSuggestionsGroupedByPriority();
            
            model.addAttribute("suggestionsByPriority", grouped);
            
            // Top 20 prioritaires
            List<FormationSuggestionDTO> topPriority = 
                competencesRepository.getTopPrioritySuggestions(20);
            
            model.addAttribute("topPrioritySuggestions", topPriority);
            
            // Budget estimé
            Map<String, Object> budgetEstimate = 
                competencesRepository.getFormationBudgetEstimate();
            
            model.addAttribute("budgetEstimate", budgetEstimate);
            
            // Statistiques couverture
            Map<String, Long> coverage = 
                competencesRepository.getFormationCoverageStats();
            
            model.addAttribute("coverageStats", coverage);
            
            System.out.println("✅ Suggestions chargées: " + suggestions.size());
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "rh/formation-suggestions";
    }
    
    /**
     * Suggestions pour un employé spécifique
     */
    @GetMapping("/suggestions/employee/{personnelId}")
    public String showEmployeeSuggestions(
            @PathVariable Long personnelId,
            Model model) {
        
        System.out.println("=== SUGGESTIONS POUR EMPLOYÉ ID: " + personnelId + " ===");
        
        try {
            List<FormationSuggestionDTO> suggestions = 
                competencesRepository.getSuggestionsForEmployee(personnelId);
            
            model.addAttribute("suggestions", suggestions);
            model.addAttribute("personnelId", personnelId);
            
            // Calculer le budget nécessaire pour cet employé
            BigDecimal budgetEmploye = suggestions.stream()
                .filter(s -> s.getCout() != null)
                .map(FormationSuggestionDTO::getCout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            model.addAttribute("budgetEmploye", budgetEmploye);
            
            System.out.println("✅ Suggestions employé: " + suggestions.size());
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "rh/employee-formation-suggestions";
    }
    
    /**
     * API REST - Toutes les suggestions en JSON
     */
    @GetMapping("/api/suggestions")
    @ResponseBody
    public Map<String, Object> getSuggestionsData(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long personnelId) {
        
        Map<String, Object> data = new java.util.HashMap<>();
        
        List<FormationSuggestionDTO> suggestions;
        
        if (personnelId != null) {
            suggestions = competencesRepository.getSuggestionsForEmployee(personnelId);
        } else if (priority != null) {
            suggestions = competencesRepository.getFormationSuggestions().stream()
                .filter(s -> s.getPriorityLabel().equalsIgnoreCase(priority))
                .collect(java.util.stream.Collectors.toList());
        } else {
            suggestions = competencesRepository.getFormationSuggestions();
        }
        
        data.put("suggestions", suggestions);
        data.put("total", suggestions.size());
        data.put("budget", competencesRepository.getFormationBudgetEstimate());
        data.put("coverage", competencesRepository.getFormationCoverageStats());
        
        return data;
    }
    
    /**
     * Export CSV des suggestions
     */
    @GetMapping("/export/suggestions-csv")
    public void exportSuggestionsCSV(jakarta.servlet.http.HttpServletResponse response) {
        try {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"suggestions_formation.csv\"");
            
            java.io.PrintWriter writer = response.getWriter();
            
            // En-têtes
            writer.println("Matricule,Employé,Poste,Compétence,Gap,Priorité,Urgence,Formation Recommandée,Organisme,Coût,Durée (h),Début,Fin");
            
            // Données
            List<FormationSuggestionDTO> suggestions = 
                competencesRepository.getFormationSuggestions();
            
            for (FormationSuggestionDTO s : suggestions) {
                writer.printf("%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    s.getMatricule(),
                    s.getEmployeNom(),
                    s.getPoste(),
                    s.getCompetenceNom(),
                    s.getGapLevel(),
                    s.getPriorityLabel(),
                    s.getUrgence(),
                    s.getFormationTitre() != null ? s.getFormationTitre() : "Non disponible",
                    s.getOrganisme() != null ? s.getOrganisme() : "",
                    s.getCout() != null ? s.getCout().toString() : "",
                    s.getDureeHeures() != null ? s.getDureeHeures().toString() : "",
                    s.getDateDebut() != null ? s.getDateDebut().toString() : "",
                    s.getDateFin() != null ? s.getDateFin().toString() : ""
                );
            }
            
            writer.flush();
            System.out.println("✅ Export CSV suggestions réalisé");
            
        } catch (Exception e) {
            System.out.println("❌ ERREUR EXPORT: " + e.getMessage());
            e.printStackTrace();
        }
    }
}