package com.itu.statistique.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itu.statistique.repository.RHStatisticsRepository;
import com.itu.statistique.model.KPIData;
import com.itu.statistique.model.StatisticsDTO;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/rh/statistics")
public class StatisticsController {

    @Autowired
    private RHStatisticsRepository statisticsRepository;

    /**
     * Dashboard principal avec tous les indicateurs
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        System.out.println("=== ACCÈS AU DASHBOARD COMPLET ===");

        try {
            // A. STATISTIQUES EFFECTIFS
            model.addAttribute("statsGenre", statisticsRepository.getStatsByGenre());
            model.addAttribute("statsAge", statisticsRepository.getStatsByAgeGroups());
            model.addAttribute("statsService", statisticsRepository.getStatsByService());
            model.addAttribute("statsContrat", statisticsRepository.getStatsByContractType());
            model.addAttribute("totalEffectif", statisticsRepository.getTotalEffectif());

            // B. INDICATEURS KPI
            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();

            // Turnover
            BigDecimal turnoverRate = statisticsRepository.getTurnoverRate(currentYear);
            KPIData turnover = new KPIData(
                    "Taux de Turnover " + currentYear,
                    turnoverRate,
                    "%",
                    getTrend(turnoverRate, new BigDecimal("10")), // Seuil à 10%
                    "fas fa-exchange-alt");
            model.addAttribute("kpiTurnover", turnover);

            // Absentéisme
            BigDecimal absenteeismRate = statisticsRepository.getAbsenteeismRate(currentYear, currentMonth);
            KPIData absenteeism = new KPIData(
                    "Taux d'Absentéisme",
                    absenteeismRate,
                    "%",
                    getTrend(absenteeismRate, new BigDecimal("5")), // Seuil à 5%
                    "fas fa-user-clock");
            model.addAttribute("kpiAbsenteeism", absenteeism);

            // Ancienneté
            BigDecimal avgAnciennete = statisticsRepository.getAverageAnciennete();
            KPIData anciennete = new KPIData(
                    "Ancienneté Moyenne",
                    avgAnciennete,
                    "ans",
                    "stable",
                    "fas fa-calendar-check");
            model.addAttribute("kpiAnciennete", anciennete);

            // C. GRAPHIQUES D'ÉVOLUTION
            model.addAttribute("turnoverEvolution", statisticsRepository.getTurnoverEvolution());
            model.addAttribute("absenteeismEvolution", statisticsRepository.getAbsenteeismEvolution());

            // D. ANALYSES DÉTAILLÉES
            model.addAttribute("topAbsentEmployees", statisticsRepository.getTopAbsentEmployees());
            model.addAttribute("leavesByType", statisticsRepository.getLeaveRequestsByType());
            model.addAttribute("performanceByDept", statisticsRepository.getAveragePerformanceByDepartment());

            System.out.println("✅ Dashboard chargé avec succès");

        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors du chargement des données: " + e.getMessage());
        }

        return "rh/statistics-dashboard-complete";
    }

    /**
     * Filtre par tranche d'âge personnalisée
     */
    @PostMapping("/filter-age")
    public String filterByAge(
            @RequestParam("minAge") Integer minAge,
            @RequestParam("maxAge") Integer maxAge,
            Model model) {

        System.out.println("=== FILTRAGE PAR ÂGE: " + minAge + "-" + maxAge + " ===");

        try {
            // Recharger toutes les données
            model.addAttribute("statsGenre", statisticsRepository.getStatsByGenre());
            model.addAttribute("statsAge", statisticsRepository.getStatsByAgeRange(minAge, maxAge));
            model.addAttribute("statsService", statisticsRepository.getStatsByService());
            model.addAttribute("statsContrat", statisticsRepository.getStatsByContractType());
            model.addAttribute("totalEffectif", statisticsRepository.getTotalEffectif());

            model.addAttribute("minAge", minAge);
            model.addAttribute("maxAge", maxAge);

            System.out.println("✅ Filtrage appliqué");

        } catch (Exception e) {
            System.out.println("❌ ERREUR FILTRAGE: " + e.getMessage());
            e.printStackTrace();
        }

        return "rh/statistics-dashboard-complete";
    }

    /**
     * API REST pour récupérer les KPI en JSON
     */
    @GetMapping("/api/kpi")
    @ResponseBody
    public java.util.Map<String, Object> getKPIData() {
        java.util.Map<String, Object> kpiData = new java.util.HashMap<>();

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        kpiData.put("turnoverRate", statisticsRepository.getTurnoverRate(currentYear));
        kpiData.put("absenteeismRate", statisticsRepository.getAbsenteeismRate(currentYear, currentMonth));
        kpiData.put("avgAnciennete", statisticsRepository.getAverageAnciennete());
        kpiData.put("totalEffectif", statisticsRepository.getTotalEffectif());

        return kpiData;
    }

    /**
     * Détermine la tendance (up/down/stable) par rapport à un seuil
     */
    private String getTrend(BigDecimal value, BigDecimal threshold) {
        if (value.compareTo(threshold) > 0) {
            return "up"; // Mauvais si > seuil
        } else if (value.compareTo(threshold.multiply(new BigDecimal("0.8"))) < 0) {
            return "down"; // Bon si bien en dessous
        }
        return "stable";
    }

    /**
     * Export des statistiques en CSV
     */
    @GetMapping("/export/csv")
    public void exportToCSV(jakarta.servlet.http.HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"statistiques_rh.csv\"");

            java.io.PrintWriter writer = response.getWriter();

            // En-têtes
            writer.println("Indicateur,Valeur,Unité");

            // Données
            writer.println("Effectif Total," + statisticsRepository.getTotalEffectif() + ",personnes");
            writer.println("Taux Turnover," + statisticsRepository.getTurnoverRate(LocalDate.now().getYear()) + ",%");
            writer.println("Ancienneté Moyenne," + statisticsRepository.getAverageAnciennete() + ",ans");

            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/age-pyramid")
    public String showAgePyramid(Model model) {
        System.out.println("=== PYRAMIDE DES ÂGES INTERACTIVE ===");

        try {
            // Récupérer tous les âges
            List<Integer> employeeAges = statisticsRepository.getAllEmployeeAges();
            model.addAttribute("employeeAges", employeeAges);

            // Statistiques d'âge
            java.util.Map<String, Object> ageStats = statisticsRepository.getAgeStatistics();
            model.addAttribute("ageStats", ageStats);

            // Pyramide par défaut (tranches de 10 ans)
            model.addAttribute("statsAge", statisticsRepository.getStatsByAgeGroups());

            System.out.println("✅ Âges récupérés: " + employeeAges);
            System.out.println("✅ Stats: " + ageStats);

        } catch (Exception e) {
            System.out.println("❌ ERREUR: " + e.getMessage());
            e.printStackTrace();
        }

        return "rh/age-pyramid-interactive";
    }

    /**
     * API pour récupérer la pyramide avec des paramètres personnalisés
     */
    @GetMapping("/api/age-pyramid")
    @ResponseBody
    public List<StatisticsDTO> getAgePyramidCustom(
            @RequestParam(defaultValue = "10") int trancheSize,
            @RequestParam(defaultValue = "20") int minAge,
            @RequestParam(defaultValue = "70") int maxAge) {

        return statisticsRepository.getStatsByAgeGroupsCustom(trancheSize, minAge, maxAge);
    }
}