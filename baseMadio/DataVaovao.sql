--
-- PostgreSQL Database - Donnees RH
-- Base de donnees: Orinasa Malagasy - Donnees pour les nouvelles tables RH
--

SET client_encoding = 'UTF8';

-- ============================================================
-- DONNeES POUR LES NOUVELLES TABLES RH
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
-- Direction
(1, 1, 'EMP001', '101234567890', '2005-03-10', 'Antananarivo', 'M', 'Marie(e)', 2, 'Antananarivo', 'Rakoto Marie', '034 12 345 67', 'epouse', '00012345678901234567', 'BNI Madagascar', 'CNAPS001', 'OSTIE001', '2010-01-15', 'actif'),
(2, 2, 'EMP002', '101234567891', '2010-06-15', 'Fianarantsoa', 'F', 'Celibataire', 0, 'Fianarantsoa', 'Rasoa Paul', '033 23 456 78', 'Frere', '00012345678901234568', 'BOA Madagascar', 'CNAPS002', 'OSTIE002', '2015-03-20', 'actif'),
(3, 9, 'EMP003', '101234567892', '2002-10-20', 'Toamasina', 'M', 'Marie(e)', 3, 'Toamasina', 'Randria Alice', '032 34 567 89', 'epouse', '00012345678901234569', 'BFV-SG', 'CNAPS003', 'OSTIE003', '2008-06-10', 'actif'),

-- Cadres
(4, 5, 'EMP004', '101234567893', '2008-04-05', 'Antsirabe', 'F', 'Divorce(e)', 1, 'Antsirabe', 'Razafy Jean', '034 45 678 90', 'Ex-conjoint', '00012345678901234570', 'BNI Madagascar', 'CNAPS004', 'OSTIE004', '2012-09-01', 'actif'),
(5, 6, 'EMP005', '101234567894', '2012-08-25', 'Toliara', 'M', 'Celibataire', 0, 'Toliara', 'Andriamalala Jeanne', '033 56 789 01', 'Mere', '00012345678901234571', 'BOA Madagascar', 'CNAPS005', 'OSTIE005', '2018-02-15', 'actif'),
(6, 8, 'EMP006', '101234567895', '2007-06-10', 'Mahajanga', 'F', 'Marie(e)', 1, 'Mahajanga', 'Rajaona Marc', '032 67 890 12', 'epoux', '00012345678901234572', 'BFV-SG', 'CNAPS006', 'OSTIE006', '2013-11-20', 'actif'),
(7, 7, 'EMP007', '101234567896', '2004-12-15', 'Nosy Be', 'M', 'Marie(e)', 2, 'Nosy Be', 'Randrianarisoa Sophie', '034 78 901 23', 'epouse', '00012345678901234573', 'BNI Madagascar', 'CNAPS007', 'OSTIE007', '2011-05-10', 'actif'),

-- Employes
(8, 10, 'EMP008', '101234567897', '2011-01-20', 'Antsiranana', 'F', 'Celibataire', 0, 'Antsiranana', 'Ramanantsoa Pierre', '033 89 012 34', 'Pere', '00012345678901234574', 'BOA Madagascar', 'CNAPS008', 'OSTIE008', '2016-08-25', 'actif'),
(9, 6, 'EMP009', '101234567898', '2006-08-05', 'Moramanga', 'M', 'Marie(e)', 1, 'Moramanga', 'Ravelonarivo Claire', '032 90 123 45', 'epouse', '00012345678901234575', 'BFV-SG', 'CNAPS009', 'OSTIE009', '2014-04-15', 'actif'),
(10, 6, 'EMP010', '101234567899', '2009-05-01', 'Ambatolampy', 'F', 'Celibataire', 0, 'Ambatolampy', 'Rasolofoniaina Jacques', '034 01 234 56', 'Frere', '00012345678901234576', 'BNI Madagascar', 'CNAPS010', 'OSTIE010', '2017-10-05', 'actif');

-- ============================================================
-- 2. DOCUMENTS_RH
-- ============================================================

INSERT INTO public.documents_rh (personnel_id, type_document, nom_document, description, file_path, date_expiration, is_verified, verified_by) VALUES
-- Employe 1-3 (Direction)
(1, 'CIN', 'CIN_EMP001.pdf', 'Carte d''identite nationale', '/files/EMP001/CIN_EMP001.pdf', NULL, TRUE, 2),
(1, 'Contrat', 'Contrat_EMP001.pdf', 'Contrat de travail CDI', '/files/EMP001/Contrat_EMP001.pdf', NULL, TRUE, 2),
(2, 'CIN', 'CIN_EMP002.pdf', 'Carte d''identite nationale', '/files/EMP002/CIN_EMP002.pdf', NULL, TRUE, 1),
(2, 'Diplome', 'Diplome_EMP002.pdf', 'Diplome de master', '/files/EMP002/Diplome_EMP002.pdf', NULL, TRUE, 1),
(3, 'CIN', 'CIN_EMP003.pdf', 'Carte d''identite nationale', '/files/EMP003/CIN_EMP003.pdf', NULL, TRUE, 1),

-- Employe 4-7 (Cadres)
(4, 'CIN', 'CIN_EMP004.pdf', 'Carte d''identite nationale', '/files/EMP004/CIN_EMP004.pdf', NULL, TRUE, 1),
(4, 'Contrat', 'Contrat_EMP004.pdf', 'Contrat de travail CDI', '/files/EMP004/Contrat_EMP004.pdf', NULL, TRUE, 1),
(5, 'Diplome', 'Diplome_EMP005.pdf', 'Diplome universitaire', '/files/EMP005/Diplome_EMP005.pdf', NULL, TRUE, 1),
(5, 'Contrat', 'Contrat_EMP005.pdf', 'Contrat de travail CDD', '/files/EMP005/Contrat_EMP005.pdf', '2026-02-14', TRUE, 1),
(6, 'CIN', 'CIN_EMP006.pdf', 'Carte d''identite nationale', '/files/EMP006/CIN_EMP006.pdf', NULL, TRUE, 2),
(6, 'Medical', 'CertificatMedical_EMP006.pdf', 'Certificat medical annuel', '/files/EMP006/CertificatMedical_EMP006.pdf', '2026-06-30', TRUE, 2),
(7, 'Contrat', 'Contrat_EMP007.pdf', 'Contrat de travail CDI', '/files/EMP007/Contrat_EMP007.pdf', NULL, TRUE, 2),

-- Employe 8-10 (Employes)
(8, 'CIN', 'CIN_EMP008.pdf', 'Carte d''identite nationale', '/files/EMP008/CIN_EMP008.pdf', NULL, TRUE, 3),
(8, 'Contrat', 'Contrat_EMP008.pdf', 'Contrat de travail CDD', '/files/EMP008/Contrat_EMP008.pdf', '2026-08-24', TRUE, 3),
(8, 'Diplome', 'Diplome_EMP008.pdf', 'Diplome universite', '/files/EMP008/Diplome_EMP008.pdf', NULL, TRUE, 3),
(9, 'CIN', 'CIN_EMP009.pdf', 'Carte d''identite nationale', '/files/EMP009/CIN_EMP009.pdf', NULL, TRUE, 3),
(9, 'Medical', 'CertificatMedical_EMP009.pdf', 'Certificat medical annuel', '/files/EMP009/CertificatMedical_EMP009.pdf', '2026-04-30', TRUE, 3),
(10, 'Contrat', 'Contrat_EMP010.pdf', 'Contrat de travail Stage', '/files/EMP010/Contrat_EMP010.pdf', '2025-04-05', TRUE, 3);

-- ============================================================
-- 3. CONTRACTS_RH (Nouvelle structure)
-- ============================================================

INSERT INTO public.contracts_rh (
    personnel_id, contract_type_id, date_debut, date_fin, duree_essai_mois, 
    date_fin_essai, is_essai_valide, salaire_base, statut, 
    motif_fin, document_path, duree_preavis, indemnite_preavis, retenue_preavis
) VALUES

-- ========== CDI - Direction ==========
-- DG - CDI avec periode d'essai de 6 mois (cadre superieur)
(1, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2010-01-15', NULL, 6, '2010-07-15', TRUE, 5500000.00, 'actif', 
 NULL, '/documents/contracts/EMP001_CDI_2010.pdf', 90, 0, 0),

-- DRH - CDI avec periode d'essai de 4 mois
(2, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2015-03-20', NULL, 4, '2015-07-20', TRUE, 4200000.00, 'actif', 
 NULL, '/documents/contracts/EMP002_CDI_2015.pdf', 60, 0, 0),

-- DAF - CDI avec periode d'essai de 6 mois
(3, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2008-06-10', NULL, 6, '2008-12-10', TRUE, 4800000.00, 'actif', 
 NULL, '/documents/contracts/EMP003_CDI_2008.pdf', 90, 0, 0),

-- ========== CDI - Cadres ==========
-- Chef de projet informatique
(4, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2012-09-01', NULL, 3, '2012-12-01', TRUE, 3200000.00, 'actif', 
 NULL, '/documents/contracts/EMP004_CDI_2012.pdf', 60, 0, 0),

-- Responsable comptabilite
(6, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2013-11-20', NULL, 3, '2014-02-20', TRUE, 2800000.00, 'actif', 
 NULL, '/documents/contracts/EMP006_CDI_2013.pdf', 60, 0, 0),

-- Responsable commercial
(7, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2011-05-10', NULL, 3, '2011-08-10', TRUE, 3500000.00, 'actif', 
 NULL, '/documents/contracts/EMP007_CDI_2011.pdf', 60, 0, 0),

-- Chef de service RH
(9, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2014-04-15', NULL, 3, '2014-07-15', TRUE, 2600000.00, 'actif', 
 NULL, '/documents/contracts/EMP009_CDI_2014.pdf', 30, 0, 0),

-- ========== CDD - Contrats temporaires ==========
-- Developpeur web - CDD de 12 mois
(5, (SELECT id FROM public.contract_types WHERE code = 'CDD'), 
 '2024-02-15', '2025-02-14', 2, '2024-04-15', TRUE, 2400000.00, 'actif', 
 NULL, '/documents/contracts/EMP005_CDD_2024.pdf', 15, 0, 0),

-- Assistant comptable - CDD de 18 mois (renouvellement)
(8, (SELECT id FROM public.contract_types WHERE code = 'CDD'), 
 '2023-08-25', '2025-02-24', 1, '2023-09-25', TRUE, 1800000.00, 'actif', 
 NULL, '/documents/contracts/EMP008_CDD_2023.pdf', 15, 0, 0),

-- ========== Stage ==========
-- Stagiaire RH - Stage de 6 mois
(10, (SELECT id FROM public.contract_types WHERE code = 'STAGE'), 
 '2024-10-05', '2025-04-05', 0, NULL, FALSE, 600000.00, 'actif', 
 NULL, '/documents/contracts/EMP010_STAGE_2024.pdf', 0, 0, 0),

-- ========== Contrats supplementaires (exemples varies) ==========

-- Apprenti developpeur - Contrat d'apprentissage 24 mois
(11, (SELECT id FROM public.contract_types WHERE code = 'APPRENTISSAGE'), 
 '2023-09-01', '2025-08-31', 2, '2023-11-01', TRUE, 1200000.00, 'actif', 
 NULL, '/documents/contracts/EMP011_APPRENTISSAGE_2023.pdf', 7, 0, 0),

-- Commercial junior - Contrat de professionnalisation 12 mois
(12, (SELECT id FROM public.contract_types WHERE code = 'PROFESSIONNALISATION'), 
 '2024-01-10', '2025-01-09', 1, '2024-02-10', TRUE, 1500000.00, 'actif', 
 NULL, '/documents/contracts/EMP012_PRO_2024.pdf', 7, 0, 0),

-- Agent d'entretien - CDD saisonnier 6 mois
(13, (SELECT id FROM public.contract_types WHERE code = 'SAISONNIER'), 
 '2024-11-01', '2025-04-30', 0, NULL, FALSE, 900000.00, 'actif', 
 NULL, '/documents/contracts/EMP013_SAISONNIER_2024.pdf', 0, 0, 0),

-- Secretaire - CDD de remplacement (conge maternite)
(14, (SELECT id FROM public.contract_types WHERE code = 'CDD_REMPLACEMENT'), 
 '2024-09-15', '2025-03-15', 1, '2024-10-15', TRUE, 1600000.00, 'actif', 
 NULL, '/documents/contracts/EMP014_REMPLACEMENT_2024.pdf', 7, 0, 0),

-- Interimaire - Mission de 3 mois
(15, (SELECT id FROM public.contract_types WHERE code = 'INTERIM'), 
 '2024-12-01', '2025-02-28', 0, NULL, FALSE, 1400000.00, 'actif', 
 NULL, '/documents/contracts/EMP015_INTERIM_2024.pdf', 0, 0, 0),

-- ========== Contrats termines (historique) ==========

-- Ancien developpeur - CDD termine
(16, (SELECT id FROM public.contract_types WHERE code = 'CDD'), 
 '2022-01-10', '2023-12-31', 2, '2022-03-10', TRUE, 2200000.00, 'termine', 
 'Fin de contrat a echeance normale', '/documents/contracts/EMP016_CDD_2022.pdf', 15, 0, 0),

-- Ancien stagiaire - Stage termine
(17, (SELECT id FROM public.contract_types WHERE code = 'STAGE'), 
 '2023-03-01', '2023-08-31', 0, NULL, FALSE, 500000.00, 'termine', 
 'Fin de stage', '/documents/contracts/EMP017_STAGE_2023.pdf', 0, 0, 0),

-- CDI resilie - Demission
(18, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2019-06-15', '2024-03-31', 3, '2019-09-15', TRUE, 2000000.00, 'resilie', 
 'Demission du salarie', '/documents/contracts/EMP018_CDI_2019.pdf', 30, 0, 2000000.00),

-- CDI suspendu (conge parental)
(19, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2020-04-01', NULL, 2, '2020-06-01', TRUE, 1800000.00, 'suspendu', 
 'Conge parental d''education', '/documents/contracts/EMP019_CDI_2020.pdf', 30, 0, 0),

-- ========== Contrats avec periode d'essai en cours ==========

-- Nouveau manager - En periode d'essai
(20, (SELECT id FROM public.contract_types WHERE code = 'CDI'), 
 '2024-11-01', NULL, 4, '2025-03-01', FALSE, 3000000.00, 'actif', 
 NULL, '/documents/contracts/EMP020_CDI_2024.pdf', 60, 0, 0);
-- ============================================================
-- MISE A JOUR DES SeQUENCES
-- ============================================================

SELECT pg_catalog.setval('public.contracts_rh_id_seq', 10, true);
-- ============================================================
-- 4. LEAVE_TYPES_RH (Types de conges)
-- ============================================================

INSERT INTO public.leave_types_rh (id, name, description, max_days_per_year, requires_justification, is_paid, color) VALUES
(1, 'Conge paye', 'Conge annuel paye standard', 30, FALSE, TRUE, '#4CAF50'),
(2, 'Conge maladie', 'Conge pour raison de sante', 15, TRUE, TRUE, '#FF9800'),
(3, 'Conge maternite', 'Conge maternite (14 semaines)', 98, TRUE, TRUE, '#E91E63'),
(4, 'Conge paternite', 'Conge paternite (3 jours)', 3, FALSE, TRUE, '#2196F3'),
(5, 'Conge exceptionnel', 'evenements familiaux (mariage, deces)', 5, TRUE, TRUE, '#9C27B0'),
(6, 'Conge sans solde', 'Conge sans remuneration', NULL, TRUE, FALSE, '#757575'),
(7, 'RTT', 'Recuperation temps de travail', 10, FALSE, TRUE, '#00BCD4'),
(8, 'Formation', 'Absence pour formation professionnelle', NULL, TRUE, TRUE, '#FF5722');

INSERT INTO public.leave_types_rh 
(id, name, description, max_days_per_year, requires_justification, is_paid, color) 
VALUES
(9, 'Absence non autorisee', 'Absence injustifiee / non autorisee', NULL, TRUE, FALSE, '#F44336');

-- ============================================================
-- 5. LEAVE_BALANCE_RH (Soldes de conges 2025)
-- ============================================================

INSERT INTO public.leave_balance_rh (personnel_id, leave_type_id, annee, solde_initial, solde_acquis, solde_pris, solde_restant) VALUES
-- Conges payes (type 1)
(1, 1, 2025, 5.0, 30.0, 12.0, 23.0),
(2, 1, 2025, 3.0, 30.0, 8.0, 25.0),
(3, 1, 2025, 8.0, 30.0, 15.0, 23.0),
(4, 1, 2025, 2.0, 30.0, 10.0, 22.0),
(5, 1, 2025, 0.0, 30.0, 5.0, 25.0),
(6, 1, 2025, 4.0, 30.0, 14.0, 20.0),
(7, 1, 2025, 6.0, 30.0, 18.0, 18.0),
(8, 1, 2025, 1.0, 30.0, 7.0, 24.0),
(9, 1, 2025, 3.0, 30.0, 11.0, 22.0),
(10, 1, 2025, 0.0, 15.0, 3.0, 12.0), -- Stagiaire : 15 jours seulement

-- Conges maladie (type 2)
(1, 2, 2025, 0.0, 15.0, 3.0, 12.0),
(2, 2, 2025, 0.0, 15.0, 1.0, 14.0),
(3, 2, 2025, 0.0, 15.0, 5.0, 10.0),
(4, 2, 2025, 0.0, 15.0, 2.0, 13.0),
(5, 2, 2025, 0.0, 15.0, 0.0, 15.0),
(10, 2, 2025, 0.0, 10.0, 1.0, 9.0); -- Stagiaire : 10 jours maladie

-- ============================================================
-- 6. LEAVE_REQUESTS_RH (Demandes de conges)
-- ============================================================

INSERT INTO public.leave_requests_rh (personnel_id, leave_type_id, date_debut, date_fin, nombre_jours, motif, statut, validated_by, validation_date) VALUES
(1, 1, '2025-12-20', '2025-12-31', 10.0, 'Vacances de fin d''annee', 'approuve', 6, '2025-11-01 10:30:00'),
(2, 1, '2025-08-01', '2025-08-15', 15.0, 'Conge ete', 'approuve', 6, '2025-07-10 14:20:00'),
(3, 2, '2025-09-10', '2025-09-12', 3.0, 'Grippe saisonniere', 'approuve', 6, '2025-09-11 09:00:00'),
(4, 1, '2025-11-20', '2025-11-25', 5.0, 'Conge personnel', 'en_attente', NULL, NULL),
(5, 1, '2025-12-15', '2025-12-20', 5.0, 'Fetes de fin d''annee', 'en_attente', NULL, NULL),
(6, 5, '2025-10-05', '2025-10-06', 2.0, 'Mariage', 'approuve', 6, '2025-09-20 11:45:00');


ALTER TABLE cnaps 
    ALTER COLUMN heures_mensuel TYPE DECIMAL(7,4),
    ALTER COLUMN taux_employeur TYPE DECIMAL(5,2),
    ALTER COLUMN taux_employe TYPE DECIMAL(5,2);

ALTER TABLE irsa
    ALTER COLUMN seuil_min  TYPE BIGINT,
    ALTER COLUMN seuil_max  TYPE BIGINT USING seuil_max::BIGINT,
    ALTER COLUMN limite     TYPE BIGINT,
    ALTER COLUMN taux       TYPE DECIMAL(5,2);


INSERT INTO public.salary_parameters_rh 
(nom_parametre, description, valeur, type, categorie, date_debut_validite)
VALUES
('smig', 'Salaire Minimum Interprofessionnel Garanti', 350000.0000, 'montant_fixe', 'smig', '2024-01-01');

-- CNAPS AGRICOLE
INSERT INTO cnaps 
(secteur_activite, heures_mensuel, taux_employeur, taux_employe, date_debut_validite)
VALUES
('agricole', 200.0000, 8.0000, 1.0000, '2024-01-01');

-- CNAPS NON AGRICOLE
INSERT INTO cnaps 
(secteur_activite, heures_mensuel, taux_employeur, taux_employe, date_debut_validite)
VALUES
('non_agricole', 173.3300, 13.0000, 1.0000, '2024-01-01');

INSERT INTO ostie 
(taux_employeur, taux_employe, date_debut_validite)
VALUES
(5.0000, 1.0000, '2024-01-01');


INSERT INTO irsa 
(numero_tranche, seuil_min, seuil_max, limite, taux, date_debut_validite)
VALUES
(1, 0,       350000, 0,     0.00, '2024-01-01'),
(2, 350001,  400000, 50000,  5.00, '2024-01-01'),
(3, 400001,  500000, 100000, 10.00, '2024-01-01'),
(4, 500001,  600000, 100000, 15.00, '2024-01-01'),
(5, 600001,  4000000,100000, 20.00, '2024-01-01'),
(6, 4000001,  NULL,0.00, 25.00, '2024-01-01');



-- 8. SALARY_COMPONENTS_RH (Composantes salariales individuelles)
-- ============================================================

INSERT INTO public.salary_components_rh (personnel_id, type_composante, montant, is_recurring) VALUES
-- Prime anciennete (calculee selon annees d'anciennete)
(1, 'prime_anciennete', 75000.00, TRUE), -- 15 ans * 5000
(2, 'prime_anciennete', 50000.00, TRUE), -- 10 ans * 5000
(3, 'prime_anciennete', 85000.00, TRUE), -- 17 ans * 5000
(4, 'prime_anciennete', 65000.00, TRUE), -- 13 ans * 5000
(6, 'prime_anciennete', 60000.00, TRUE), -- 12 ans * 5000
(7, 'prime_anciennete', 70000.00, TRUE), -- 14 ans * 5000
(9, 'prime_anciennete', 55000.00, TRUE), -- 11 ans * 5000

-- Indemnite transport (pour tous)
(1, 'indemnite_transport', 50000.00, TRUE),
(2, 'indemnite_transport', 50000.00, TRUE),
(3, 'indemnite_transport', 50000.00, TRUE),
(4, 'indemnite_transport', 50000.00, TRUE),
(5, 'indemnite_transport', 50000.00, TRUE),
(6, 'indemnite_transport', 50000.00, TRUE),
(7, 'indemnite_transport', 50000.00, TRUE),
(8, 'indemnite_transport', 50000.00, TRUE),
(9, 'indemnite_transport', 50000.00, TRUE),
(10, 'indemnite_transport', 50000.00, TRUE),

-- Allocation familiale (pour ceux avec enfants)
(1, 'allocation_familiale', 40000.00, TRUE), -- 2 enfants
(3, 'allocation_familiale', 60000.00, TRUE), -- 3 enfants
(4, 'allocation_familiale', 20000.00, TRUE), -- 1 enfant
(6, 'allocation_familiale', 20000.00, TRUE), -- 1 enfant
(7, 'allocation_familiale', 40000.00, TRUE); -- 2 enfants

-- ============================================================
-- 9. COMPETENCES_RH (Referentiel de competences)
-- ============================================================

INSERT INTO public.competences_rh (nom, description, categorie, niveau_requis) VALUES
('Gestion de la paie', 'Maitrise des calculs de paie et charges sociales', 'technique', 4),
('Droit du travail Madagascar', 'Connaissance Code du travail malgache', 'technique', 3),
('Excel avance', 'Tableaux croises dynamiques, macros VBA', 'technique', 4),
('Communication interpersonnelle', 'Capacite a echanger efficacement', 'soft_skills', 3),
('Gestion du stress', 'Gestion des situations de pression', 'soft_skills', 3),
('Leadership', 'Capacite a diriger une equipe', 'management', 4),
('Prise de decision', 'Analyse et decisions strategiques', 'management', 4),
('Anglais professionnel', 'Niveau B2 minimum', 'langue', 3),
('Francais professionnel', 'Niveau C1 minimum', 'langue', 4),
('Logiciels RH (SIRH)', 'Utilisation outils de gestion RH', 'technique', 3);
-- ============================================================
-- 10. COMPETENCES_MAPPING_RH (Competences par employe)
-- ============================================================

INSERT INTO public.competences_mapping_rh (personnel_id, competence_id, niveau_actuel, date_evaluation, validated_by) VALUES
-- Directeur (EMP001)
(1, 1, 4, '2025-01-15', 13),
(1, 2, 3, '2025-01-15', 13),
(1, 3, 4, '2025-01-15', 13),
(1, 6, 5, '2025-01-15', 13),

-- RH (EMP002)
(2, 4, 4, '2025-02-20', 13),
(2, 5, 3, '2025-02-20', 13),
(2, 10, 4, '2025-02-20', 13),

-- Directeur Commercial (EMP003)
(3, 6, 5, '2025-03-10', 13),
(3, 7, 4, '2025-03-10', 13),
(3, 8, 4, '2025-03-10', 13),
(3, 9, 5, '2025-03-10', 13),

-- Cadres
(4, 1, 3, '2025-04-05', 13),
(4, 3, 4, '2025-04-05', 13),
(6, 8, 4, '2025-05-12', 13),
(6, 9, 5, '2025-05-12', 13);


-- ============================================================
-- MISE A JOUR DES SeQUENCES
-- ============================================================

SELECT pg_catalog.setval('public.personnel_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.contracts_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.leave_types_rh_id_seq', 8, true);
SELECT pg_catalog.setval('public.salary_parameters_rh_id_seq', 12, true);
SELECT pg_catalog.setval('public.competences_rh_id_seq', 10, true);
SELECT pg_catalog.setval('public.documents_templates_rh_id_seq', 3, true);


INSERT INTO type_hs (code, description, taux_majoration) VALUES
('normal', 'Heure supplementaire en jour normal', NULL),
('dimanche', 'Travail le dimanche (weekend)', 1.00),
('nuit', 'Travail de nuit entre 22h et 5h', 0.30),
('ferie', 'Travail un jour ferie', 2.00);



INSERT INTO public.company_info_rh (nom_entreprise, secteur_activite)
VALUES ('Votre Entreprise', 'non_agricole');

COMMENT ON TABLE public.company_info_rh IS 'Informations generales de l''entreprise (singleton - 1 seule ligne)';
COMMENT ON COLUMN public.salary_parameters_rh.secteur_activite IS 'Secteur concerne: agricole, non_agricole, ou commun (applicable a tous)';

-- ============================================================
-- FIN DES DONNeES RH
-- ============================================================