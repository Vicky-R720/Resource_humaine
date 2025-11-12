package com.itu.statistique.repository;

import org.springframework.stereotype.Repository;
import com.itu.statistique.model.StatisticsDTO;
import com.itu.statistique.model.KPIData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Repository
public class RHStatisticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================
    // A. STATISTIQUES EFFECTIFS
    // ============================================

    /**
     * Répartition par genre (M/F)
     */
    public List<StatisticsDTO> getStatsByGenre() {
        String sql = "SELECT p.sexe as label, COUNT(*) as value " +
                "FROM personnel_rh p " +
                "WHERE p.statut = 'actif' " +
                "GROUP BY p.sexe";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Pyramide des âges (par tranches de 10 ans) - TOUTES les tranches affichées
     */
    public List<StatisticsDTO> getStatsByAgeGroups() {
        // Requête pour obtenir les données existantes
        String sql = "SELECT " +
                "CASE " +
                "  WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN 20 AND 29 THEN '20-29 ans' " +
                "  WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN 30 AND 39 THEN '30-39 ans' " +
                "  WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN 40 AND 49 THEN '40-49 ans' " +
                "  WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN 50 AND 59 THEN '50-59 ans' " +
                "  WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) >= 60 THEN '60+ ans' " +
                "  ELSE '<20 ans' " +
                "END as label, " +
                "COUNT(*) as value " +
                "FROM personnel_rh pr " +
                "JOIN person per ON pr.person_id = per.id " +
                "WHERE pr.statut = 'actif' " +
                "GROUP BY label";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        // Convertir en Map pour faciliter la recherche
        java.util.Map<String, Long> dataMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).longValue()));

        // Définir TOUTES les tranches d'âge dans l'ordre
        String[] allAgeGroups = { "<20 ans", "20-29 ans", "30-39 ans", "40-49 ans", "50-59 ans", "60+ ans" };

        // Créer la liste complète avec 0 pour les tranches vides
        List<StatisticsDTO> completeList = new java.util.ArrayList<>();
        for (String ageGroup : allAgeGroups) {
            Long value = dataMap.getOrDefault(ageGroup, 0L);
            completeList.add(new StatisticsDTO(ageGroup, value));
        }

        return completeList;
    }

    /**
     * Répartition par service (avec table post)
     */
    public List<StatisticsDTO> getStatsByService() {
        String sql = "SELECT COALESCE(po.name, 'Non défini') as label, COUNT(*) as value " +
                "FROM personnel_rh pr " +
                "LEFT JOIN post po ON pr.post_id = po.id " +
                "WHERE pr.statut = 'actif' " +
                "GROUP BY po.name " +
                "ORDER BY value DESC";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Répartition par type de contrat
     */
    public List<StatisticsDTO> getStatsByContractType() {
        String sql = "SELECT c.type_contrat as label, COUNT(*) as value " +
                "FROM contracts_rh c " +
                "WHERE c.statut = 'actif' " +
                "GROUP BY c.type_contrat " +
                "ORDER BY value DESC";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Répartition par tranche d'âge personnalisée
     */
    public List<StatisticsDTO> getStatsByAgeRange(Integer minAge, Integer maxAge) {
        String sql = "SELECT " +
                "CONCAT(:minAge, '-', :maxAge, ' ans') as label, " +
                "COUNT(*) as value " +
                "FROM personnel_rh pr " +
                "JOIN person per ON pr.person_id = per.id " +
                "WHERE pr.statut = 'actif' " +
                "AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN :minAge AND :maxAge";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("minAge", minAge);
        query.setParameter("maxAge", maxAge);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Total effectif actif
     */
    public Long getTotalEffectif() {
        String sql = "SELECT COUNT(*) FROM personnel_rh WHERE statut = 'actif'";
        Query query = entityManager.createNativeQuery(sql);
        return ((Number) query.getSingleResult()).longValue();
    }

    // ============================================
    // B. INDICATEURS RH (KPI)
    // ============================================

    /**
     * Calcul du taux de turnover
     * Formule: (Nombre de départs sur période / Effectif moyen) * 100
     */
    public BigDecimal getTurnoverRate(int year) {
        // Nombre de départs dans l'année
        String sqlDeparts = "SELECT COUNT(*) " +
                "FROM personnel_rh " +
                "WHERE statut = 'inactif' " +
                "AND EXTRACT(YEAR FROM date_sortie) = :year";

        Query queryDeparts = entityManager.createNativeQuery(sqlDeparts);
        queryDeparts.setParameter("year", year);
        Long departs = ((Number) queryDeparts.getSingleResult()).longValue();

        // Effectif moyen de l'année
        String sqlEffectifMoyen = "SELECT AVG(effectif) FROM (" +
                "  SELECT COUNT(*) as effectif " +
                "  FROM personnel_rh " +
                "  WHERE date_embauche <= MAKE_DATE(:year, 12, 31) " +
                "  AND (date_sortie IS NULL OR date_sortie >= MAKE_DATE(:year, 1, 1))" +
                ") as sub";

        Query queryEffectif = entityManager.createNativeQuery(sqlEffectifMoyen);
        queryEffectif.setParameter("year", year);
        BigDecimal effectifMoyen = (BigDecimal) queryEffectif.getSingleResult();

        if (effectifMoyen.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calcul: (départs / effectif moyen) * 100
        return BigDecimal.valueOf(departs)
                .divide(effectifMoyen, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calcul du taux d'absentéisme
     * Formule: (Jours d'absence / Jours travaillés théoriques) * 100
     */
    public BigDecimal getAbsenteeismRate(int year, int month) {
        // Nombre de jours d'absence dans le mois
        String sqlAbsences = "SELECT COUNT(*) " +
                "FROM attendance_rh " +
                "WHERE statut IN ('absent', 'conge') " +
                "AND EXTRACT(YEAR FROM date_pointage) = :year " +
                "AND EXTRACT(MONTH FROM date_pointage) = :month";

        Query queryAbsences = entityManager.createNativeQuery(sqlAbsences);
        queryAbsences.setParameter("year", year);
        queryAbsences.setParameter("month", month);
        Long absences = ((Number) queryAbsences.getSingleResult()).longValue();

        // Nombre de jours travaillés théoriques
        String sqlJoursTheoriques = "SELECT COUNT(*) " +
                "FROM attendance_rh " +
                "WHERE EXTRACT(YEAR FROM date_pointage) = :year " +
                "AND EXTRACT(MONTH FROM date_pointage) = :month";

        Query queryTheoriques = entityManager.createNativeQuery(sqlJoursTheoriques);
        queryTheoriques.setParameter("year", year);
        queryTheoriques.setParameter("month", month);
        Long joursTheoriques = ((Number) queryTheoriques.getSingleResult()).longValue();

        if (joursTheoriques == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(absences)
                .divide(BigDecimal.valueOf(joursTheoriques), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Ancienneté moyenne en années
     */
    public BigDecimal getAverageAnciennete() {
        String sql = "SELECT AVG(EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_embauche))) " +
                "FROM personnel_rh " +
                "WHERE statut = 'actif'";

        Query query = entityManager.createNativeQuery(sql);
        BigDecimal avg = (BigDecimal) query.getSingleResult();

        return avg != null ? avg.setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * Évolution du turnover sur 12 mois
     */
    public List<StatisticsDTO> getTurnoverEvolution() {
        String sql = "SELECT " +
                "TO_CHAR(date_sortie, 'YYYY-MM') as label, " +
                "COUNT(*) as value " +
                "FROM personnel_rh " +
                "WHERE statut = 'inactif' " +
                "AND date_sortie >= CURRENT_DATE - INTERVAL '12 months' " +
                "GROUP BY TO_CHAR(date_sortie, 'YYYY-MM') " +
                "ORDER BY label";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Évolution de l'absentéisme sur 12 mois
     */
    public List<StatisticsDTO> getAbsenteeismEvolution() {
        String sql = "SELECT " +
                "TO_CHAR(date_pointage, 'YYYY-MM') as label, " +
                "COUNT(CASE WHEN statut IN ('absent', 'conge') THEN 1 END) * 100.0 / COUNT(*) as value " +
                "FROM attendance_rh " +
                "WHERE date_pointage >= CURRENT_DATE - INTERVAL '12 months' " +
                "GROUP BY TO_CHAR(date_pointage, 'YYYY-MM') " +
                "ORDER BY label";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((BigDecimal) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Top 5 des employés les plus absents
     */
    public List<StatisticsDTO> getTopAbsentEmployees() {
        String sql = "SELECT " +
                "p.nom || ' ' || p.prenom as label, " +
                "COUNT(*) as value " +
                "FROM attendance_rh a " +
                "JOIN personnel_rh pr ON a.personnel_id = pr.id " +
                "JOIN person p ON pr.person_id = p.id " +
                "WHERE a.statut IN ('absent', 'conge') " +
                "AND a.date_pointage >= CURRENT_DATE - INTERVAL '6 months' " +
                "GROUP BY p.nom, p.prenom " +
                "ORDER BY value DESC " +
                "LIMIT 5";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Répartition des congés par type
     */
    public List<StatisticsDTO> getLeaveRequestsByType() {
        String sql = "SELECT " +
                "lt.name as label, " +
                "COUNT(*) as value " +
                "FROM leave_requests_rh lr " +
                "JOIN leave_types_rh lt ON lr.leave_type_id = lt.id " +
                "WHERE lr.statut = 'approuve' " +
                "AND EXTRACT(YEAR FROM lr.date_debut) = EXTRACT(YEAR FROM CURRENT_DATE) " +
                "GROUP BY lt.name " +
                "ORDER BY value DESC";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Score moyen des évaluations par département
     */
    public List<StatisticsDTO> getAveragePerformanceByDepartment() {
        String sql = "SELECT " +
                "po.name as label, " +
                "ROUND(AVG(pe.score_global), 1) as value " +
                "FROM performance_evaluations_rh pe " +
                "JOIN personnel_rh pr ON pe.personnel_id = pr.id " +
                "JOIN post po ON pr.post_id = po.id " +
                "WHERE pe.statut IN ('finalisee', 'en_cours') " +
                "GROUP BY po.name " +
                "ORDER BY value DESC";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((BigDecimal) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    public List<StatisticsDTO> getStatsByAgeGroupsCustom(int trancheSize, int minAge, int maxAge) {
        // Construction dynamique du CASE WHEN
        StringBuilder caseWhen = new StringBuilder("CASE ");
        java.util.List<String> allRanges = new java.util.ArrayList<>();

        for (int age = minAge; age < maxAge; age += trancheSize) {
            int endAge = age + trancheSize - 1;
            String label = age + "-" + endAge + " ans";
            allRanges.add(label);

            caseWhen.append(String.format(
                    "WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) BETWEEN %d AND %d THEN '%s' ",
                    age, endAge, label));
        }

        // Ajouter la tranche max+
        String labelMax = maxAge + "+ ans";
        allRanges.add(labelMax);
        caseWhen.append(String.format(
                "WHEN EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)) >= %d THEN '%s' ",
                maxAge, labelMax));

        // Ajouter la tranche < min
        String labelMin = "<" + minAge + " ans";
        allRanges.add(0, labelMin); // Ajouter au début
        caseWhen.append(String.format("ELSE '%s' ", labelMin));
        caseWhen.append("END as label");

        // Requête SQL complète
        String sql = "SELECT " + caseWhen.toString() + ", COUNT(*) as value " +
                "FROM personnel_rh pr " +
                "JOIN person per ON pr.person_id = per.id " +
                "WHERE pr.statut = 'actif' " +
                "GROUP BY label";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        // Convertir en Map
        java.util.Map<String, Long> dataMap = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).longValue()));

        // Créer la liste complète avec TOUTES les tranches (même celles à 0)
        List<StatisticsDTO> completeList = new java.util.ArrayList<>();
        for (String range : allRanges) {
            Long value = dataMap.getOrDefault(range, 0L);
            completeList.add(new StatisticsDTO(range, value));
        }

        return completeList;
    }

    // ============================================
    // À AJOUTER DANS RHStatisticsRepository.java
    // ============================================

    /**
     * Récupérer tous les âges des employés actifs
     * (pour le graphique interactif côté client)
     */
    public List<Integer> getAllEmployeeAges() {
        String sql = "SELECT EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance))::INTEGER as age " +
                "FROM personnel_rh pr " +
                "JOIN person per ON pr.person_id = per.id " +
                "WHERE pr.statut = 'actif' " +
                "ORDER BY age";

        Query query = entityManager.createNativeQuery(sql);
        List<Integer> ages = query.getResultList();

        return ages;
    }

    /**
     * Statistiques d'âge (min, max, moyenne, médiane)
     */
    public java.util.Map<String, Object> getAgeStatistics() {
        String sql = "SELECT " +
                "MIN(EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)))::INTEGER as min_age, " +
                "MAX(EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)))::INTEGER as max_age, " +
                "AVG(EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)))::NUMERIC(5,1) as avg_age, " +
                "PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY EXTRACT(YEAR FROM AGE(CURRENT_DATE, per.naissance)))::INTEGER as median_age "
                +
                "FROM personnel_rh pr " +
                "JOIN person per ON pr.person_id = per.id " +
                "WHERE pr.statut = 'actif'";

        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("minAge", result[0]);
        stats.put("maxAge", result[1]);
        stats.put("avgAge", result[2]);
        stats.put("medianAge", result[3]);

        return stats;
    }
}