-- ============================================================

-- ============================================================
-- 1. PERSONNEL_RH (Fiches employes)
-- ============================================================
INSERT INTO public.personnel_rh (
    person_id, post_id, matricule, cin, cin_date_delivery, cin_place_delivery, 
    sexe, situation_familiale, nombre_enfants, lieu_naissance,
    personne_urgence_nom, personne_urgence_contact, personne_urgence_lien,
    rib, banque, numero_cnaps, numero_ostie, date_embauche, statut
) VALUES
(1, 1, 'EMP001', '101234567890', '2005-03-10', 'Antananarivo', 'M', 'Marie(e)', 2, 'Antananarivo', 'Rakoto Marie', '034 12 345 67', 'epouse', '00012345678901234567', 'BNI Madagascar', 'CNAPS001', 'OSTIE001', '2010-01-15', 'actif'),
(2, 2, 'EMP002', '101234567891', '2010-06-15', 'Fianarantsoa', 'F', 'Celibataire', 0, 'Fianarantsoa', 'Rasoa Paul', '033 23 456 78', 'Frère', '00012345678901234568', 'BOA Madagascar', 'CNAPS002', 'OSTIE002', '2015-03-20', 'actif'),
(3, 9, 'EMP003', '101234567892', '2002-10-20', 'Toamasina', 'M', 'Marie(e)', 3, 'Toamasina', 'Randria Alice', '032 34 567 89', 'epouse', '00012345678901234569', 'BFV-SG', 'CNAPS003', 'OSTIE003', '2008-06-10', 'actif'),
(4, 5, 'EMP004', '101234567893', '2008-04-05', 'Antsirabe', 'F', 'Divorce(e)', 1, 'Antsirabe', 'Razafy Jean', '034 45 678 90', 'Ex-conjoint', '00012345678901234570', 'BNI Madagascar', 'CNAPS004', 'OSTIE004', '2012-09-01', 'actif'),
(5, 6, 'EMP005', '101234567894', '2012-08-25', 'Toliara', 'M', 'Celibataire', 0, 'Toliara', 'Andriamalala Jeanne', '033 56 789 01', 'Mère', '00012345678901234571', 'BOA Madagascar', 'CNAPS005', 'OSTIE005', '2018-02-15', 'actif'),
(6, 8, 'EMP006', '101234567895', '2007-06-10', 'Mahajanga', 'F', 'Marie(e)', 1, 'Mahajanga', 'Rajaona Marc', '032 67 890 12', 'epoux', '00012345678901234572', 'BFV-SG', 'CNAPS006', 'OSTIE006', '2013-11-20', 'actif'),
(7, 7, 'EMP007', '101234567896', '2004-12-15', 'Nosy Be', 'M', 'Marie(e)', 2, 'Nosy Be', 'Randrianarisoa Sophie', '034 78 901 23', 'epouse', '00012345678901234573', 'BNI Madagascar', 'CNAPS007', 'OSTIE007', '2011-05-10', 'actif'),
(8, 10, 'EMP008', '101234567897', '2011-01-20', 'Antsiranana', 'F', 'Celibataire', 0, 'Antsiranana', 'Ramanantsoa Pierre', '033 89 012 34', 'Père', '00012345678901234574', 'BOA Madagascar', 'CNAPS008', 'OSTIE008', '2016-08-25', 'actif'),
(9, 6, 'EMP009', '101234567898', '2006-08-05', 'Moramanga', 'M', 'Marie(e)', 1, 'Moramanga', 'Ravelonarivo Claire', '032 90 123 45', 'epouse', '00012345678901234575', 'BFV-SG', 'CNAPS009', 'OSTIE009', '2014-04-15', 'actif'),
(10, 6, 'EMP010', '101234567899', '2009-05-01', 'Ambatolampy', 'F', 'Celibataire', 0, 'Ambatolampy', 'Rasolofoniaina Jacques', '034 01 234 56', 'Frère', '00012345678901234576', 'BNI Madagascar', 'CNAPS010', 'OSTIE010', '2017-10-05', 'actif');

-- ============================================================
-- 2. CONTRACTS_RH (Contrats)
-- ============================================================
INSERT INTO public.contracts_rh (
    personnel_id, type_contrat, date_debut, date_fin, duree_essai_mois, 
    date_fin_essai, is_essai_valide, salaire_base, statut
) VALUES
(1, 'CDI', '2010-01-15', NULL, 3, '2010-04-15', TRUE, 1500000.00, 'actif'),
(2, 'CDI', '2015-03-20', NULL, 2, '2015-05-20', TRUE, 1200000.00, 'actif'),
(3, 'CDI', '2008-06-10', NULL, 3, '2008-09-10', TRUE, 2500000.00, 'actif'),
(4, 'CDI', '2012-09-01', NULL, 2, '2012-11-01', TRUE, 1800000.00, 'actif'),
(5, 'CDD', '2018-02-15', '2026-02-14', 3, '2018-05-15', TRUE, 1400000.00, 'actif'),
(6, 'CDI', '2013-11-20', NULL, 3, '2014-02-20', TRUE, 2200000.00, 'actif'),
(7, 'CDI', '2011-05-10', NULL, 2, '2011-07-10', TRUE, 1900000.00, 'actif'),
(8, 'CDD', '2016-08-25', '2026-08-24', 2, '2016-10-25', TRUE, 1100000.00, 'actif'),
(9, 'CDI', '2014-04-15', NULL, 3, '2014-07-15', TRUE, 1600000.00, 'actif'),
(10, 'Stage', '2017-10-05', '2025-04-05', 1, '2017-11-05', TRUE, 800000.00, 'actif');

-- ============================================================
-- 3. LEAVE_TYPES_RH (Types de conges)
-- ============================================================
INSERT INTO public.leave_types_rh (id, name, description, max_days_per_year, requires_justification, is_paid, color) VALUES
(1, 'Conge paye', 'Conge annuel paye standard', 30, FALSE, TRUE, '#4CAF50'),
(2, 'Conge maladie', 'Conge pour raison de sante', 15, TRUE, TRUE, '#FF9800'),
(3, 'Conge maternite', 'Conge maternite (14 semaines)', 98, TRUE, TRUE, '#E91E63'),
(4, 'Conge paternite', 'Conge paternite (3 jours)', 3, FALSE, TRUE, '#2196F3'),
(5, 'Conge exceptionnel', 'evenements familiaux (mariage, decès)', 5, TRUE, TRUE, '#9C27B0'),
(6, 'Conge sans solde', 'Conge sans remuneration', NULL, TRUE, FALSE, '#757575'),
(7, 'RTT', 'Recuperation temps de travail', 10, FALSE, TRUE, '#00BCD4'),
(8, 'Formation', 'Absence pour formation professionnelle', NULL, TRUE, TRUE, '#FF5722');

-- ============================================================
-- 4. LEAVE_BALANCE_RH (Soldes de conges 2025)
-- ============================================================
INSERT INTO public.leave_balance_rh (personnel_id, leave_type_id, annee, solde_initial, solde_acquis, solde_pris, solde_restant) VALUES
(1, 1, 2025, 5.0, 30.0, 12.0, 23.0),
(1, 2, 2025, 0.0, 15.0, 3.0, 12.0),
(2, 1, 2025, 3.0, 30.0, 8.0, 25.0),
(2, 2, 2025, 0.0, 15.0, 1.0, 14.0),
(3, 1, 2025, 8.0, 30.0, 15.0, 23.0),
(3, 2, 2025, 0.0, 15.0, 5.0, 10.0),
(4, 1, 2025, 2.0, 30.0, 10.0, 22.0),
(4, 2, 2025, 0.0, 15.0, 2.0, 13.0),
(5, 1, 2025, 0.0, 30.0, 5.0, 25.0),
(5, 2, 2025, 0.0, 15.0, 0.0, 15.0),
(6, 1, 2025, 4.0, 30.0, 14.0, 20.0),
(7, 1, 2025, 6.0, 30.0, 18.0, 18.0),
(8, 1, 2025, 1.0, 30.0, 7.0, 24.0),
(9, 1, 2025, 3.0, 30.0, 11.0, 22.0),
(10, 1, 2025, 0.0, 15.0, 3.0, 12.0);

-- ============================================================
-- 5. LEAVE_REQUESTS_RH (Demandes de conges)
-- ============================================================
INSERT INTO public.leave_requests_rh (personnel_id, leave_type_id, date_debut, date_fin, nombre_jours, motif, statut, validated_by, validation_date) VALUES
(1, 1, '2025-12-20', '2025-12-31', 10.0, 'Vacances de fin d''annee', 'approuve', 13, '2025-11-01 10:30:00'),
(2, 1, '2025-08-01', '2025-08-15', 15.0, 'Conge ete', 'approuve', 13, '2025-07-10 14:20:00'),
(3, 2, '2025-09-10', '2025-09-12', 3.0, 'Grippe saisonnière', 'approuve', 13, '2025-09-11 09:00:00'),
(4, 1, '2025-11-20', '2025-11-25', 5.0, 'Conge personnel', 'en_attente', NULL, NULL),
(5, 1, '2025-12-15', '2025-12-20', 5.0, 'Fêtes de fin d''annee', 'en_attente', NULL, NULL),
(6, 5, '2025-10-05', '2025-10-06', 2.0, 'Mariage', 'approuve', 13, '2025-09-20 11:45:00');

-- ============================================================
-- 6. SALARY_PARAMETERS_RH (Paramètres de paie Madagascar)
-- ============================================================
INSERT INTO public.salary_parameters_rh (nom_parametre, description, valeur, type, categorie, date_debut_validite, is_active) VALUES
('CNAPS_EMPLOYEE', 'Cotisation CNAPS employe', 1.00, 'pourcentage', 'cnaps', '2025-01-01', TRUE),
('CNAPS_EMPLOYER', 'Cotisation CNAPS employeur', 13.00, 'pourcentage', 'cnaps', '2025-01-01', TRUE),
('OSTIE_EMPLOYEE', 'Cotisation OSTIE employe', 1.00, 'pourcentage', 'ostie', '2025-01-01', TRUE),
('OSTIE_EMPLOYER', 'Cotisation OSTIE employeur', 5.00, 'pourcentage', 'ostie', '2025-01-01', TRUE),
('IRSA_TAUX_1', 'IRSA tranche 1 (0-350000)', 0.00, 'pourcentage', 'irsa', '2025-01-01', TRUE),
('IRSA_TAUX_2', 'IRSA tranche 2 (350001-400000)', 5.00, 'pourcentage', 'irsa', '2025-01-01', TRUE),
('IRSA_TAUX_3', 'IRSA tranche 3 (400001-500000)', 10.00, 'pourcentage', 'irsa', '2025-01-01', TRUE),
('IRSA_TAUX_4', 'IRSA tranche 4 (500001-600000)', 15.00, 'pourcentage', 'irsa', '2025-01-01', TRUE),
('IRSA_TAUX_5', 'IRSA tranche 5 (>600000)', 20.00, 'pourcentage', 'irsa', '2025-01-01', TRUE),
('PRIME_ANCIENNETE', 'Prime d''anciennete par annee', 5000.00, 'montant_fixe', 'prime', '2025-01-01', TRUE),
('INDEMNITE_TRANSPORT', 'Indemnite de transport mensuelle', 50000.00, 'montant_fixe', 'indemnite', '2025-01-01', TRUE),
('ALLOCATION_FAMILIALE', 'Allocation familiale par enfant', 20000.00, 'montant_fixe', 'indemnite', '2025-01-01', TRUE);

-- ============================================================
-- 7. SALARY_COMPONENTS_RH (Composantes salaire)
-- ============================================================
INSERT INTO public.salary_components_rh (personnel_id, type_composante, montant, is_recurring) VALUES
(1, 'prime_anciennete', 75000.00, TRUE),
(1, 'indemnite_transport', 50000.00, TRUE),
(1, 'allocation_familiale', 40000.00, TRUE),
(2, 'prime_anciennete', 50000.00, TRUE),
(2, 'indemnite_transport', 50000.00, TRUE),
(3, 'prime_anciennete', 85000.00, TRUE),
(3, 'indemnite_transport', 50000.00, TRUE),
(3, 'allocation_familiale', 60000.00, TRUE),
(4, 'prime_anciennete', 65000.00, TRUE),
(4, 'indemnite_transport', 50000.00, TRUE),
(4, 'allocation_familiale', 20000.00, TRUE),
(6, 'prime_anciennete', 60000.00, TRUE),
(6, 'indemnite_transport', 50000.00, TRUE),
(6, 'allocation_familiale', 20000.00, TRUE);

-- ============================================================
-- 8. COMPETENCES_RH (Referentiel de competences)
-- ============================================================
INSERT INTO public.competences_rh (nom, description, categorie, niveau_requis) VALUES
('Gestion de la paie', 'Maîtrise des calculs de paie et charges sociales', 'technique', 4),
('Droit du travail Madagascar', 'Connaissance Code du travail malgache', 'technique', 3),
('Excel avance', 'Tableaux croises dynamiques, macros VBA', 'technique', 4),
('Communication interpersonnelle', 'Capacite à echanger efficacement', 'soft_skills', 3),
('Gestion du stress', 'Gestion des situations de pression', 'soft_skills', 3),
('Leadership', 'Capacite à diriger une equipe', 'management', 4),
('Prise de decision', 'Analyse et decisions strategiques', 'management', 4),
('Anglais professionnel', 'Niveau B2 minimum', 'langue', 3),
('Français professionnel', 'Niveau C1 minimum', 'langue', 4),
('Logiciels RH (SIRH)', 'Utilisation outils de gestion RH', 'technique', 3);

-- ============================================================
-- 9. COMPETENCES_MAPPING_RH (Competences par employe)
-- ============================================================
INSERT INTO public.competences_mapping_rh (personnel_id, competence_id, niveau_actuel, date_evaluation, validated_by) VALUES
(1, 1, 4, '2025-01-15', 13),
(1, 2, 3, '2025-01-15', 13),
(1, 3, 4, '2025-01-15', 13),
(2, 4, 4, '2025-02-20', 13),
(2, 5, 3, '2025-02-20', 13),
(3, 6, 5, '2025-03-10', 13),
(3, 7, 4, '2025-03-10', 13),
(4, 1, 3, '2025-04-05', 13),
(4, 3, 4, '2025-04-05', 13),
(6, 8, 4, '2025-05-12', 13),
(6, 9, 5, '2025-05-12', 13);

-- ============================================================
-- 10. DOCUMENTS_TEMPLATES_RH (Templates)
-- ============================================================
INSERT INTO public.documents_templates_rh (type_document, nom_template, template_content, variables_disponibles, is_active) VALUES
('contrat_cdi', 'Contrat CDI Standard', '<h1>CONTRAT DE TRAVAIL À DUReE INDeTERMINeE</h1><p>Entre la societe {{company_name}} et {{employee_name}}, matricule {{matricule}}...</p>', '{"company_name":"Nom entreprise","employee_name":"Nom employe","matricule":"Matricule","post":"Poste","salary":"Salaire","start_date":"Date debut"}', TRUE),
('attestation_travail', 'Attestation de travail', '<h1>ATTESTATION DE TRAVAIL</h1><p>Je soussigne(e), certifie que {{employee_name}}, ne(e) le {{birth_date}}, travaille dans notre entreprise depuis le {{start_date}} en qualite de {{post}}.</p>', '{"employee_name":"Nom employe","birth_date":"Date naissance","start_date":"Date embauche","post":"Poste"}', TRUE),
('attestation_salaire', 'Attestation de salaire', '<h1>ATTESTATION DE SALAIRE</h1><p>Nous certifions que {{employee_name}}, matricule {{matricule}}, perçoit un salaire mensuel brut de {{salary}} Ariary.</p>', '{"employee_name":"Nom employe","matricule":"Matricule","salary":"Salaire brut"}', TRUE);

-- ============================================================
-- SET SEQUENCE VALUES
-- ============================================================
SELECT pg_catalog.setval('public.personnel_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.contracts_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.leave_types_rh_id_seq', 8, true);
SELECT pg_catalog.setval('public.salary_parameters_rh_id_seq', 12, true);
SELECT pg_catalog.setval('public.competences_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.documents_templates_rh_id_seq', 3, true);

-- ============================================================
-- FIN DES DONNeES RH
-- ============================================================

update offers set date_expiration='2026-01-01';

-- Insérer des postes dans la table post
INSERT INTO public.post (id, name, description, missions) VALUES
(1, 'Directeur Général', 'Responsable de la direction générale', 'Gestion stratégique, Supervision des départements'),
(2, 'Responsable RH', 'Gestion des ressources humaines', 'Recrutement, Formation, Paie'),
(3, 'Chef de Projet', 'Gestion de projets informatiques', 'Planification, Coordination, Suivi'),
(4, 'Développeur Senior', 'Développement applications', 'Codage, Revue de code, Mentorat'),
(5, 'Développeur', 'Développement applications', 'Codage, Tests'),
(6, 'Analyste', 'Analyse des besoins', 'Spécifications, Documentation'),
(7, 'Commercial', 'Vente et relation client', 'Prospection, Négociation'),
(8, 'Comptable', 'Gestion comptable', 'Comptabilité, Fiscalité'),
(9, 'Responsable IT', 'Gestion infrastructure IT', 'Réseau, Sécurité, Maintenance'),
(10, 'Stagiaire', 'Apprentissage professionnel', 'Support, Formation');

-------------------------------------

-- =====================================================
-- DONNÉES DE TEST POUR LE SYSTÈME RH
-- =====================================================

-- 1. DONNÉES ATTENDANCE_RH (Pointages sur les 3 derniers mois)
-- Génération de pointages réalistes avec quelques absences

-- Octobre 2025 (mois complet)
INSERT INTO performance_evaluations_rh (
    personnel_id, evaluateur_id, periode, date_evaluation,
    score_global, score_qualite_travail, score_productivite, score_assiduite,
    score_travail_equipe, score_initiative,
    points_forts, points_amelioration, objectifs_periode_suivante,
    commentaire_evaluateur, commentaire_employe, statut
) VALUES
-- Evaluations 2024 (annee complete)
(
    1, 13, '2024-S2', '2024-12-15',
    85, 90, 85, 95, 80, 85,
    'Excellente ponctualite, maîtrise technique remarquable, bon esprit d''equipe',
    'Developper les competences en gestion de projet, ameliorer la communication avec les autres services',
    'Piloter 2 projets majeurs, former les nouveaux collaborateurs',
    'Collaborateur fiable et competent. Pret pour plus de responsabilites.',
    'Merci pour cette evaluation. J''aimerais suivre une formation en management.',
    'finalisee'
),
(
    2, 13, '2024-S2', '2024-12-15',
    75, 80, 70, 65, 85, 75,
    'Bonne maîtrise des outils RH, excellentes relations interpersonnelles',
    'Ameliorer la ponctualite et la gestion du temps, reduire les absences',
    'Respecter les horaires, participer au projet digitalisation RH',
    'Bon potentiel mais doit ameliorer son assiduite.',
    'Je comprends les remarques et vais faire des efforts sur la ponctualite.',
    'finalisee'
),
(
    3, 13, '2024-S2', '2024-12-15',
    92, 95, 90, 98, 88, 90,
    'Excellence operationnelle, leadership naturel, tres grande fiabilite',
    'Deleguer davantage pour se concentrer sur la strategie',
    'Prendre en charge la coordination de l''equipe production, optimiser les processus',
    'Collaborateur exceptionnel. Element cle de l''entreprise.',
    'Tres motive pour continuer a contribuer au developpement de l''entreprise.',
    'finalisee'
),
(
    4, 13, '2024-S2', '2024-12-15',
    80, 85, 78, 80, 82, 75,
    'Rigueur dans le controle qualite, bonne analyse des problemes',
    'Etre plus proactive dans la proposition de solutions, renforcer l''autonomie',
    'Mettre en place 3 nouveaux indicateurs qualite, former l''equipe aux nouvelles normes',
    'Bonne collaboratrice, qualite du travail satisfaisante.',
    'Je souhaite developper mes competences en audit qualite.',
    'finalisee'
),
(
    5, 13, '2024-S2', '2024-12-15',
    78, 75, 80, 75, 85, 80,
    'Creativite dans la R&D, bonnes idees innovantes, esprit d''equipe',
    'Ameliorer le respect des delais, mieux structurer les projets',
    'Finaliser 2 projets de developpement produit, documenter les processus',
    'Bon potentiel d''innovation mais doit ameliorer son organisation.',
    'Je vais travailler sur la gestion de mon temps.',
    'finalisee'
),

-- Evaluations 2025-S1 (premier semestre)
(
    1, 13, '2025-S1', '2025-06-20',
    88, 92, 86, 95, 85, 88,
    'Progression notable en leadership, gestion de projet exemplaire',
    'Continuer a developper les competences manageriales',
    'Manager une equipe de 3 personnes, piloter le projet transformation digitale',
    'Excellente evolution. Pret pour un poste de responsabilite.',
    'Tres motive par les nouvelles responsabilites.',
    'finalisee'
),
(
    2, 13, '2025-S1', '2025-06-20',
    78, 82, 75, 70, 88, 78,
    'Amelioration de l''assiduite par rapport au S2 2024, bonne integration',
    'Maintenir les efforts sur la ponctualite',
    'Atteindre 95% de presence, automatiser 3 processus RH',
    'Progression positive. Encourager les efforts.',
    'Content des progres realises, vais continuer dans cette voie.',
    'finalisee'
),
(
    3, 13, '2025-S1', '2025-06-20',
    94, 96, 93, 98, 90, 92,
    'Leadership confirme, capacite a gerer les crises, mentor pour l''equipe',
    'Aucune remarque negative',
    'Developper la strategie production a 3 ans, optimiser les couts de 15%',
    'Collaborateur d''excellence. Pilier de l''entreprise.',
    'Heureux de contribuer a la croissance de l''entreprise.',
    'finalisee'
),

-- Evaluations en cours (2025-S2)
(
    6, 13, '2025-S2', '2025-11-05',
    82, 85, 80, 90, 80, 78,
    'Excellente gestion commerciale, bon relationnel client',
    'Developper les competences en negociation B2B',
    'Augmenter le CA de 20%, recruter 10 nouveaux clients',
    'Bonne performance commerciale.',
    NULL,
    'en_cours'
),
(
    7, 13, '2025-S2', '2025-11-05',
    86, 88, 85, 92, 84, 82,
    'Maitrise des marches export, excellents resultats a l''international',
    'Diversifier les zones geographiques',
    'Ouvrir 2 nouveaux marches export, augmenter les volumes de 25%',
    'Tres bon travail a l''export.',
    NULL,
    'en_cours'
);


-- =====================================================
-- VÉRIFICATIONS
-- =====================================================

-- Vérifier les données insérées
SELECT 'ATTENDANCE_RH' as table_name, COUNT(*) as nb_lignes FROM attendance_rh
UNION ALL
SELECT 'PERFORMANCE_EVALUATIONS_RH', COUNT(*) FROM performance_evaluations_rh
UNION ALL
SELECT 'LEAVE_REQUESTS_RH', COUNT(*) FROM leave_requests_rh
UNION ALL
SELECT 'PERSONNEL_RH (inactifs)', COUNT(*) FROM personnel_rh WHERE statut = 'inactif';

-- Statistiques de présence par employé
SELECT 
    pr.matricule,
    p.nom,
    COUNT(*) as nb_pointages,
    SUM(CASE WHEN a.statut = 'present' THEN 1 ELSE 0 END) as presents,
    SUM(CASE WHEN a.statut = 'absent' THEN 1 ELSE 0 END) as absents,
    SUM(CASE WHEN a.statut = 'retard' THEN 1 ELSE 0 END) as retards,
    ROUND(AVG(a.duree_travail_minutes), 0) as duree_moyenne_minutes
FROM attendance_rh a
JOIN personnel_rh pr ON a.personnel_id = pr.id
JOIN person p ON pr.person_id = p.id
GROUP BY pr.matricule, p.nom
ORDER BY pr.matricule;