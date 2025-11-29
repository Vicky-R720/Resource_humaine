package com.itu.gest_emp.modules.statistique.repository;

import org.springframework.stereotype.Repository;

import com.itu.gest_emp.modules.statistique.model.CompetenceMatrixDTO;
import com.itu.gest_emp.modules.statistique.model.FormationSuggestionDTO;
import com.itu.gest_emp.modules.statistique.model.StatisticsDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.BigInteger;

@Repository
public class CompetencesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // ============================================
    // RÉFÉRENTIEL COMPÉTENCES
    // ============================================

    /**
     * Répartition des compétences par catégorie
     */
    public List<StatisticsDTO> getCompetencesByCategory() {
        String sql = "SELECT " +
                "  CASE categorie " +
                "    WHEN 'technique' THEN 'Techniques' " +
                "    WHEN 'soft_skills' THEN 'Soft Skills' " +
                "    WHEN 'management' THEN 'Management' " +
                "    WHEN 'langue' THEN 'Langues' " +
                "    ELSE 'Autre' " +
                "  END as label, " +
                "  COUNT(*) as value " +
                "FROM competences_rh " +
                "GROUP BY categorie " +
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
     * Top 10 compétences les plus demandées (niveau requis élevé)
     */
    public List<StatisticsDTO> getTopRequiredCompetences() {
        String sql = "SELECT " +
                "  nom as label, " +
                "  niveau_requis as value " +
                "FROM competences_rh " +
                "ORDER BY niveau_requis DESC, nom " +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Top 10 compétences les plus répandues dans l'entreprise
     */
    public List<StatisticsDTO> getMostCommonCompetences() {
        String sql = "SELECT " +
                "  c.nom as label, " +
                "  COUNT(cm.id) as value " +
                "FROM competences_rh c " +
                "JOIN competences_mapping_rh cm ON c.id = cm.competence_id " +
                "GROUP BY c.nom " +
                "ORDER BY value DESC " +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    // ============================================
    // COMPÉTENCES DES EMPLOYÉS
    // ============================================

    /**
     * Distribution des niveaux de compétences
     */
    public List<StatisticsDTO> getCompetenceLevelsDistribution() {
        String sql = "SELECT " +
                "  'Niveau ' || niveau_actuel as label, " +
                "  COUNT(*) as value " +
                "FROM competences_mapping_rh " +
                "GROUP BY niveau_actuel " +
                "ORDER BY niveau_actuel";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Niveau moyen de compétences par employé
     */
    public List<StatisticsDTO> getAverageCompetenceLevelByEmployee() {
        String sql = "SELECT " +
                "  p.nom || ' ' || p.prenom as label, " +
                "  ROUND(AVG(cm.niveau_actuel), 1) as value " +
                "FROM personnel_rh pr " +
                "JOIN person p ON pr.person_id = p.id " +
                "JOIN competences_mapping_rh cm ON pr.id = cm.personnel_id " +
                "WHERE pr.statut = 'actif' " +
                "GROUP BY p.nom, p.prenom " +
                "ORDER BY value DESC " +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((BigDecimal) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Compétences par catégorie pour un employé spécifique
     */
    public Map<String, List<Map<String, Object>>> getEmployeeCompetencesByCategory(Long personnelId) {
        String sql = "SELECT " +
                "  c.categorie, " +
                "  c.nom, " +
                "  cm.niveau_actuel, " +
                "  c.niveau_requis, " +
                "  cm.date_evaluation " +
                "FROM competences_mapping_rh cm " +
                "JOIN competences_rh c ON cm.competence_id = c.id " +
                "WHERE cm.personnel_id = :personnelId " +
                "ORDER BY c.categorie, c.nom";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("personnelId", personnelId);

        List<Object[]> results = query.getResultList();

        Map<String, List<Map<String, Object>>> grouped = new HashMap<>();

        for (Object[] row : results) {
            String category = (String) row[0];
            Map<String, Object> comp = new HashMap<>();
            comp.put("nom", row[1]);
            comp.put("niveau_actuel", row[2]);
            comp.put("niveau_requis", row[3]);
            comp.put("date_evaluation", row[4]);

            grouped.computeIfAbsent(category, k -> new java.util.ArrayList<>()).add(comp);
        }

        return grouped;
    }

    // ============================================
    // MATRICE COMPÉTENCES PAR POSTE
    // ============================================

    /**
     * Matrice: Compétences moyennes par poste
     */
    @SuppressWarnings("unchecked")
    public List<CompetenceMatrixDTO> getCompetenceMatrixByPost() {
        String sql = """
                    SELECT
                        po.name AS poste,
                        c.nom AS competence,
                        c.categorie AS categorie,
                        ROUND(AVG(cm.niveau_actuel), 1) AS niveau_moyen,
                        COUNT(cm.id) AS nb_personnes
                    FROM post po
                    JOIN personnel_rh pr ON po.id = pr.post_id
                    JOIN competences_mapping_rh cm ON pr.id = cm.personnel_id
                    JOIN competences_rh c ON cm.competence_id = c.id
                    WHERE pr.statut = 'actif'
                    GROUP BY po.name, c.nom, c.categorie
                    ORDER BY po.name, c.categorie, niveau_moyen DESC
                """;

        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();

        List<CompetenceMatrixDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            String poste = (String) row[0];
            String competence = (String) row[1];
            String categorie = (String) row[2];

            // Gestion des types selon la BDD (PostgreSQL → BigDecimal / MySQL → Double)
            double niveauMoyen = 0.0;
            Object niveauObj = row[3];
            if (niveauObj instanceof BigDecimal) {
                niveauMoyen = ((BigDecimal) niveauObj).doubleValue();
            } else if (niveauObj instanceof Number) {
                niveauMoyen = ((Number) niveauObj).doubleValue();
            }

            int nbPersonnes = 0;
            Object nbObj = row[4];
            if (nbObj instanceof BigInteger) {
                nbPersonnes = ((BigInteger) nbObj).intValue();
            } else if (nbObj instanceof Number) {
                nbPersonnes = ((Number) nbObj).intValue();
            }

            dtos.add(new CompetenceMatrixDTO(poste, competence, categorie, niveauMoyen, nbPersonnes));
        }

        return dtos;
    }

    /**
     * Compétences les plus fortes par poste
     */
    public Map<String, List<Map<String, Object>>> getTopCompetencesByPost() {
        String sql = "WITH ranked_competences AS ( " +
                "  SELECT " +
                "    po.name as poste, " +
                "    c.nom as competence, " +
                "    ROUND(AVG(cm.niveau_actuel), 1) as niveau_moyen, " +
                "    ROW_NUMBER() OVER (PARTITION BY po.name ORDER BY AVG(cm.niveau_actuel) DESC) as rank " +
                "  FROM post po " +
                "  JOIN personnel_rh pr ON po.id = pr.post_id " +
                "  JOIN competences_mapping_rh cm ON pr.id = cm.personnel_id " +
                "  JOIN competences_rh c ON cm.competence_id = c.id " +
                "  WHERE pr.statut = 'actif' " +
                "  GROUP BY po.name, c.nom " +
                ") " +
                "SELECT poste, competence, niveau_moyen " +
                "FROM ranked_competences " +
                "WHERE rank <= 5 " +
                "ORDER BY poste, rank";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        Map<String, List<Map<String, Object>>> grouped = new HashMap<>();

        for (Object[] row : results) {
            String poste = (String) row[0];
            Map<String, Object> comp = new HashMap<>();
            comp.put("competence", row[1]);
            comp.put("niveau_moyen", row[2]);

            grouped.computeIfAbsent(poste, k -> new java.util.ArrayList<>()).add(comp);
        }

        return grouped;
    }

    /**
     * Gaps de compétences (écart niveau actuel vs requis)
     */
    public List<Map<String, Object>> getCompetenceGaps() {
        String sql = "SELECT " +
                "  p.nom || ' ' || p.prenom as employe, " +
                "  c.nom as competence, " +
                "  cm.niveau_actuel, " +
                "  c.niveau_requis, " +
                "  (c.niveau_requis - cm.niveau_actuel) as gap " +
                "FROM competences_mapping_rh cm " +
                "JOIN competences_rh c ON cm.competence_id = c.id " +
                "JOIN personnel_rh pr ON cm.personnel_id = pr.id " +
                "JOIN person p ON pr.person_id = p.id " +
                "WHERE pr.statut = 'actif' " +
                "  AND cm.niveau_actuel < c.niveau_requis " +
                "ORDER BY gap DESC, p.nom " +
                "LIMIT 20";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> {
                    Map<String, Object> gap = new HashMap<>();
                    gap.put("employe", result[0]);
                    gap.put("competence", result[1]);
                    gap.put("niveau_actuel", result[2]);
                    gap.put("niveau_requis", result[3]);
                    gap.put("gap", result[4]);
                    return gap;
                })
                .collect(Collectors.toList());
    }

    // ============================================
    // FORMATIONS
    // ============================================

    /**
     * Formations par statut
     */
    public List<StatisticsDTO> getFormationsByStatus() {
        String sql = "SELECT " +
                "  CASE statut " +
                "    WHEN 'en_cours' THEN 'En cours' " +
                "    WHEN 'planifiee' THEN 'Planifiée' " +
                "    WHEN 'terminee' THEN 'Terminée' " +
                "    WHEN 'annulee' THEN 'Annulée' " +
                "    ELSE 'Autre' " +
                "  END as label, " +
                "  COUNT(*) as value " +
                "FROM formations_rh " +
                "GROUP BY statut " +
                "ORDER BY " +
                "  CASE statut " +
                "    WHEN 'en_cours' THEN 1 " +
                "    WHEN 'planifiee' THEN 2 " +
                "    WHEN 'terminee' THEN 3 " +
                "    ELSE 4 " +
                "  END";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Budget formation total et par statut
     */
    public Map<String, BigDecimal> getFormationBudget() {
        String sql = "SELECT " +
                "  statut, " +
                "  SUM(cout) as total " +
                "FROM formations_rh " +
                "GROUP BY statut";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        Map<String, BigDecimal> budget = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Object[] row : results) {
            String statut = (String) row[0];
            BigDecimal montant = (BigDecimal) row[1];
            budget.put(statut, montant);
            total = total.add(montant);
        }

        budget.put("total", total);
        return budget;
    }

    /**
     * Heures de formation par employé
     */
    public List<StatisticsDTO> getFormationHoursByEmployee() {
        String sql = "SELECT " +
                "  p.nom || ' ' || p.prenom as label, " +
                "  SUM(f.duree_heures) as value " +
                "FROM formations_rh f " +
                "JOIN personnel_rh pr ON f.personnel_id = pr.id " +
                "JOIN person p ON pr.person_id = p.id " +
                "WHERE f.statut IN ('terminee', 'en_cours') " +
                "GROUP BY p.nom, p.prenom " +
                "ORDER BY value DESC " +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    /**
     * Taux de certification (formations avec certificat / total terminées)
     */
    public BigDecimal getCertificationRate() {
        String sql = "SELECT " +
                "  COUNT(CASE WHEN certificat_obtenu = true THEN 1 END) * 100.0 / " +
                "  NULLIF(COUNT(CASE WHEN statut = 'terminee' THEN 1 END), 0) as taux " +
                "FROM formations_rh";

        Query query = entityManager.createNativeQuery(sql);
        BigDecimal rate = (BigDecimal) query.getSingleResult();

        return rate != null ? rate.setScale(1, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    /**
     * Compétences ciblées par les formations
     */
    public List<StatisticsDTO> getFormationsByTargetedCompetence() {
        String sql = "SELECT " +
                "  c.nom as label, " +
                "  COUNT(f.id) as value " +
                "FROM formations_rh f " +
                "JOIN competences_rh c ON f.competence_visee = c.id " +
                "GROUP BY c.nom " +
                "ORDER BY value DESC " +
                "LIMIT 10";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> new StatisticsDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()))
                .collect(Collectors.toList());
    }

    // ============================================
    // SUGGESTIONS DE FORMATION (NOUVEAU)
    // ============================================
    
    /**
     * Détection des gaps et suggestions de formations
     * Retourne tous les gaps avec formations recommandées
     */
    public List<FormationSuggestionDTO> getFormationSuggestions() {
        String sql = "SELECT " +
                    "  pr.id as personnel_id, " +
                    "  p.nom || ' ' || p.prenom as employe_nom, " +
                    "  pr.matricule, " +
                    "  po.name as poste, " +
                    "  c.id as competence_id, " +
                    "  c.nom as competence_nom, " +
                    "  c.categorie, " +
                    "  cm.niveau_actuel, " +
                    "  c.niveau_requis, " +
                    "  (c.niveau_requis - cm.niveau_actuel) as gap_level, " +
                    "  CASE " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) >= 3 THEN 4 " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) = 2 THEN 3 " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) = 1 THEN 2 " +
                    "    ELSE 1 " +
                    "  END as priority_score, " +
                    "  CASE " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) >= 3 THEN 'critique' " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) = 2 THEN 'élevé' " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) = 1 THEN 'moyen' " +
                    "    ELSE 'bas' " +
                    "  END as business_impact, " +
                    "  CASE " +
                    "    WHEN c.categorie IN ('technique', 'management') AND (c.niveau_requis - cm.niveau_actuel) >= 2 THEN 'haute' " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) >= 3 THEN 'urgente' " +
                    "    WHEN (c.niveau_requis - cm.niveau_actuel) = 2 THEN 'moyenne' " +
                    "    ELSE 'faible' " +
                    "  END as urgence, " +
                    "  f.id as formation_id, " +
                    "  f.titre as formation_titre, " +
                    "  f.organisme, " +
                    "  f.type_formation, " +
                    "  f.duree_heures, " +
                    "  f.cout, " +
                    "  f.date_debut, " +
                    "  f.date_fin, " +
                    "  f.statut as statut_formation " +
                    "FROM personnel_rh pr " +
                    "JOIN person p ON pr.person_id = p.id " +
                    "JOIN post po ON pr.post_id = po.id " +
                    "JOIN competences_mapping_rh cm ON pr.id = cm.personnel_id " +
                    "JOIN competences_rh c ON cm.competence_id = c.id " +
                    "LEFT JOIN formations_rh f ON c.id = f.competence_visee AND f.statut = 'disponible' " +
                    "WHERE pr.statut = 'actif' " +
                    "  AND cm.niveau_actuel < c.niveau_requis " +
                    "ORDER BY priority_score DESC, gap_level DESC, pr.matricule";
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(result -> {
                FormationSuggestionDTO dto = new FormationSuggestionDTO();
                dto.setPersonnelId(((Number) result[0]).longValue());
                dto.setEmployeNom((String) result[1]);
                dto.setMatricule((String) result[2]);
                dto.setPoste((String) result[3]);
                dto.setCompetenceId(((Number) result[4]).longValue());
                dto.setCompetenceNom((String) result[5]);
                dto.setCategorie((String) result[6]);
                dto.setNiveauActuel((Integer) result[7]);
                dto.setNiveauRequis((Integer) result[8]);
                dto.setGapLevel((Integer) result[9]);
                dto.setPriorityScore((Integer) result[10]);
                dto.setBusinessImpact((String) result[11]);
                dto.setUrgence((String) result[12]);
                
                // Formation recommandée (peut être null)
                if (result[13] != null) {
                    dto.setFormationId(((Number) result[13]).longValue());
                    dto.setFormationTitre((String) result[14]);
                    dto.setOrganisme((String) result[15]);
                    dto.setTypeFormation((String) result[16]);
                    dto.setDureeHeures((Integer) result[17]);
                    dto.setCout((BigDecimal) result[18]);
                    dto.setDateDebut(result[19] != null ? ((java.sql.Date) result[19]).toLocalDate() : null);
                    dto.setDateFin(result[20] != null ? ((java.sql.Date) result[20]).toLocalDate() : null);
                    dto.setStatutFormation((String) result[21]);
                    
                    // Calculer le score de match
                    int matchScore = calculateMatchScore(dto.getGapLevel(), dto.getTypeFormation());
                    dto.setMatchScore(matchScore);
                    dto.setRecommendation(getRecommendationLabel(matchScore));
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Suggestions priorisées (top 20 critiques)
     */
    public List<FormationSuggestionDTO> getTopPrioritySuggestions(int limit) {
        List<FormationSuggestionDTO> all = getFormationSuggestions();
        
        return all.stream()
            .filter(s -> s.getPriorityScore() >= 3) // Élevé et Critique seulement
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Suggestions pour un employé spécifique
     */
    public List<FormationSuggestionDTO> getSuggestionsForEmployee(Long personnelId) {
        List<FormationSuggestionDTO> all = getFormationSuggestions();
        
        return all.stream()
            .filter(s -> s.getPersonnelId().equals(personnelId))
            .collect(Collectors.toList());
    }
    
    /**
     * Suggestions groupées par priorité
     */
    public Map<String, List<FormationSuggestionDTO>> getSuggestionsGroupedByPriority() {
        List<FormationSuggestionDTO> all = getFormationSuggestions();
        
        Map<String, List<FormationSuggestionDTO>> grouped = new HashMap<>();
        grouped.put("Critique", new java.util.ArrayList<>());
        grouped.put("Élevé", new java.util.ArrayList<>());
        grouped.put("Moyen", new java.util.ArrayList<>());
        grouped.put("Faible", new java.util.ArrayList<>());
        
        for (FormationSuggestionDTO dto : all) {
            grouped.get(dto.getPriorityLabel()).add(dto);
        }
        
        return grouped;
    }
    
    /**
     * Budget estimé pour combler tous les gaps
     */
    public Map<String, Object> getFormationBudgetEstimate() {
        String sql = "SELECT " +
                    "  COUNT(DISTINCT fp.personnel_id) as nb_personnes, " +
                    "  COUNT(fp.id) as nb_gaps, " +
                    "  COUNT(CASE WHEN f.id IS NOT NULL THEN 1 END) as nb_formations_disponibles, " +
                    "  COALESCE(SUM(f.cout), 0) as budget_total, " +
                    "  COALESCE(SUM(CASE WHEN fp.priority_score = 4 THEN f.cout END), 0) as budget_critique, " +
                    "  COALESCE(SUM(CASE WHEN fp.priority_score = 3 THEN f.cout END), 0) as budget_eleve, " +
                    "  COALESCE(SUM(f.duree_heures), 0) as heures_totales " +
                    "FROM formation_priorities_rh fp " +
                    "LEFT JOIN formations_rh f ON fp.competence_id = f.competence_visee AND f.statut = 'disponible' " +
                    "WHERE fp.status = 'identifié'";
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        Map<String, Object> budget = new HashMap<>();
        budget.put("nbPersonnes", ((Number) result[0]).intValue());
        budget.put("nbGaps", ((Number) result[1]).intValue());
        budget.put("nbFormationsDisponibles", ((Number) result[2]).intValue());
        budget.put("budgetTotal", (BigDecimal) result[3]);
        budget.put("budgetCritique", (BigDecimal) result[4]);
        budget.put("budgetEleve", (BigDecimal) result[5]);
        budget.put("heuresTotales", ((Number) result[6]).intValue());
        
        return budget;
    }
    
    /**
     * Statistiques de couverture formation
     */
    public Map<String, Long> getFormationCoverageStats() {
        String sql = "SELECT " +
                    "  COUNT(*) FILTER (WHERE f.id IS NOT NULL) as avec_formation, " +
                    "  COUNT(*) FILTER (WHERE f.id IS NULL) as sans_formation, " +
                    "  COUNT(*) as total " +
                    "FROM formation_priorities_rh fp " +
                    "LEFT JOIN formations_rh f ON fp.competence_id = f.competence_visee AND f.statut = 'disponible'";
        
        Query query = entityManager.createNativeQuery(sql);
        Object[] result = (Object[]) query.getSingleResult();
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("avecFormation", ((Number) result[0]).longValue());
        stats.put("sansFormation", ((Number) result[1]).longValue());
        stats.put("total", ((Number) result[2]).longValue());
        
        return stats;
    }
    
    // Helpers privés
    private int calculateMatchScore(Integer gapLevel, String typeFormation) {
        if (gapLevel == null) return 50;
        
        int baseScore = 50;
        
        // Score basé sur le gap
        baseScore += (gapLevel * 15);
        
        // Bonus pour type formation
        if ("externe".equals(typeFormation)) baseScore += 10;
        if ("hybride".equals(typeFormation)) baseScore += 5;
        
        return Math.min(baseScore, 100);
    }
    
    private String getRecommendationLabel(int matchScore) {
        if (matchScore >= 85) return "Fortement recommandé";
        if (matchScore >= 70) return "Recommandé";
        if (matchScore >= 55) return "À considérer";
        return "Optionnel";
    }
}