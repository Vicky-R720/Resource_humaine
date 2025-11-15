
CREATE TABLE public.personnel_rh (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT UNIQUE REFERENCES public.person(id) ON DELETE CASCADE,
    post_id BIGINT REFERENCES public.post(id),
    matricule VARCHAR(50) UNIQUE NOT NULL,
    cin VARCHAR(50),
    cin_date_delivery DATE,
    cin_place_delivery VARCHAR(255),
    sexe VARCHAR(10),
    situation_familiale VARCHAR(50), 
    nombre_enfants INTEGER DEFAULT 0,
    nationalite VARCHAR(100) DEFAULT 'Malagasy',
    lieu_naissance VARCHAR(255),
    personne_urgence_nom VARCHAR(255),
    personne_urgence_contact VARCHAR(100),
    personne_urgence_lien VARCHAR(100),
    rib VARCHAR(100),
    banque VARCHAR(255),
    numero_cnaps VARCHAR(50),
    numero_ostie VARCHAR(50),
    date_embauche DATE NOT NULL,
    date_sortie DATE,
    motif_sortie TEXT,
    statut VARCHAR(50) DEFAULT 'actif',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE public.contracts_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_contrat VARCHAR(50) NOT NULL, 
    date_debut DATE NOT NULL,
    date_fin DATE,
    duree_essai_mois INTEGER DEFAULT 0,
    date_fin_essai DATE,
    is_essai_valide BOOLEAN DEFAULT FALSE,
    salaire_base DECIMAL(15,2),
    statut VARCHAR(50) DEFAULT 'actif',
    motif_fin TEXT,
    document_path VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.career_history_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    post_id BIGINT REFERENCES public.post(id),
    type_mouvement VARCHAR(50) NOT NULL, 
    ancien_poste VARCHAR(255),
    nouveau_poste VARCHAR(255),
    ancien_salaire DECIMAL(15,2),
    nouveau_salaire DECIMAL(15,2),
    date_mouvement DATE NOT NULL,
    motif TEXT,
    created_by BIGINT REFERENCES public.person(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Documents_RH (Gestion documents RH)
CREATE TABLE public.documents_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_document VARCHAR(100) NOT NULL, -- CIN, diplome, attestation, contrat, medical, autre
    nom_document VARCHAR(255) NOT NULL,
    description TEXT,
    file_path VARCHAR(500) NOT NULL,
    date_expiration DATE,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by BIGINT REFERENCES public.person(id),
    verified_at TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- Table Leave_Types_RH (Types de congés)
CREATE TABLE public.leave_types_rh (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    max_days_per_year INTEGER,
    requires_justification BOOLEAN DEFAULT FALSE,
    is_paid BOOLEAN DEFAULT TRUE,
    color VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.leave_balance_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    leave_type_id BIGINT REFERENCES public.leave_types_rh(id),
    annee INTEGER NOT NULL,
    solde_initial DECIMAL(5,2) DEFAULT 0,
    solde_acquis DECIMAL(5,2) DEFAULT 0,
    solde_pris DECIMAL(5,2) DEFAULT 0,
    solde_restant DECIMAL(5,2) DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, leave_type_id, annee)
);

-- Table Leave_Requests_RH (Demandes de congés - utilise notification)
CREATE TABLE public.leave_requests_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    leave_type_id BIGINT REFERENCES public.leave_types_rh(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nombre_jours DECIMAL(5,2) NOT NULL,
    motif TEXT,
    justificatif_path VARCHAR(500),
    statut VARCHAR(50) DEFAULT 'en_attente', -- en_attente, approuve, refuse, annule
    validated_by BIGINT REFERENCES public.person(id),
    validation_date TIMESTAMP,
    validation_comment TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 3: GESTION DU TEMPS ET PRÉSENCES
-- ============================================================

-- Table Attendance_RH (Feuille de présence/pointage)
CREATE TABLE public.attendance_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    date_pointage DATE NOT NULL,
    heure_arrivee TIME,
    heure_depart TIME,
    heure_pause_debut TIME,
    heure_pause_fin TIME,
    duree_travail_minutes INTEGER,
    statut VARCHAR(50) DEFAULT 'present', -- present, absent, retard, conge, maladie
    type_pointage VARCHAR(50) DEFAULT 'manuel', -- manuel, badgeuse, mobile
    commentaire TEXT,
    validated_by BIGINT REFERENCES public.person(id),
    is_validated BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, date_pointage)
);

-- Table Overtime_RH (Heures supplémentaires)
CREATE TABLE public.overtime_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    attendance_id BIGINT REFERENCES public.attendance_rh(id),
    date_hs DATE NOT NULL,
    nombre_heures DECIMAL(5,2) NOT NULL,
    type_hs VARCHAR(50), -- jour_normal, dimanche, jours_ferie, nuit
    taux_majoration DECIMAL(5,2) DEFAULT 1.0,
    montant_hs DECIMAL(15,2),
    statut VARCHAR(50) DEFAULT 'en_attente', -- en_attente, approuve, refuse, paye
    validated_by BIGINT REFERENCES public.person(id),
    validation_date TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 4: GESTION DE LA PAIE
-- ============================================================

-- Table Salary_Parameters_RH (Paramètres de paie)
CREATE TABLE public.salary_parameters_rh (
    id BIGSERIAL PRIMARY KEY,
    nom_parametre VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    valeur DECIMAL(10,4) NOT NULL,
    type VARCHAR(50), -- pourcentage, montant_fixe
    categorie VARCHAR(50), -- cnaps, ostie, irsa, prime, indemnite
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.salary_parameters_rh 
ADD COLUMN seuil_min DECIMAL(15,2) DEFAULT 0,
ADD COLUMN seuil_max DECIMAL(15,2) DEFAULT NULL;

-- Table Salary_Components_RH (Composantes salaire par employé)
CREATE TABLE public.salary_components_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_composante VARCHAR(100) NOT NULL,
    montant DECIMAL(15,2) NOT NULL,
    is_recurring BOOLEAN DEFAULT TRUE,
    date_debut DATE,
    date_fin DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Payslips_RH (Bulletins de paie)
CREATE TABLE public.payslips_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    mois INTEGER NOT NULL,
    annee INTEGER NOT NULL,
    salaire_base DECIMAL(15,2) NOT NULL,
    total_primes DECIMAL(15,2) DEFAULT 0,
    total_indemnites DECIMAL(15,2) DEFAULT 0,
    heures_supplementaires DECIMAL(15,2) DEFAULT 0,
    total_brut DECIMAL(15,2) NOT NULL,
    cnaps_employee DECIMAL(15,2) DEFAULT 0,
    ostie_employee DECIMAL(15,2) DEFAULT 0,
    irsa DECIMAL(15,2) DEFAULT 0,
    avances DECIMAL(15,2) DEFAULT 0,
    autres_retenues DECIMAL(15,2) DEFAULT 0,
    total_retenues DECIMAL(15,2) NOT NULL,
    net_a_payer DECIMAL(15,2) NOT NULL,
    cnaps_employer DECIMAL(15,2) DEFAULT 0,
    ostie_employer DECIMAL(15,2) DEFAULT 0,
    statut VARCHAR(50) DEFAULT 'brouillon', -- brouillon, valide, paye
    date_paiement DATE,
    mode_paiement VARCHAR(50), -- virement, especes, cheque
    pdf_path VARCHAR(500),
    created_by BIGINT REFERENCES public.person(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, mois, annee)
);

-- ============================================================
-- SECTION 5: GESTION DES COMPÉTENCES ET FORMATIONS
-- ============================================================

-- Table Competences_RH (Référentiel de compétences)
CREATE TABLE public.competences_rh (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    categorie VARCHAR(100), -- technique, soft_skills, management, langue
    niveau_requis INTEGER DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Competences_Mapping_RH (Compétences par employé)
CREATE TABLE public.competences_mapping_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    competence_id BIGINT REFERENCES public.competences_rh(id),
    niveau_actuel INTEGER DEFAULT 1,
    date_evaluation DATE,
    validated_by BIGINT REFERENCES public.person(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, competence_id)
);

-- Table Formations_RH (Formations)
CREATE TABLE public.formations_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    titre VARCHAR(255) NOT NULL,
    organisme VARCHAR(255),
    type_formation VARCHAR(50), -- interne, externe, e_learning, certification
    date_debut DATE,
    date_fin DATE,
    duree_heures INTEGER,
    cout DECIMAL(15,2),
    statut VARCHAR(50) DEFAULT 'planifiee', -- planifiee, en_cours, terminee, annulee
    certificat_obtenu BOOLEAN DEFAULT FALSE,
    certificat_path VARCHAR(500),
    competence_visee BIGINT REFERENCES public.competences_rh(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 6: GESTION DES PERFORMANCES
-- ============================================================

-- Table Performance_Evaluations_RH (Évaluations de performance)
CREATE TABLE public.performance_evaluations_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    evaluateur_id BIGINT REFERENCES public.person(id),
    periode VARCHAR(50) NOT NULL,
    date_evaluation DATE NOT NULL,
    score_global DECIMAL(5,2),
    score_qualite_travail DECIMAL(5,2),
    score_productivite DECIMAL(5,2),
    score_assiduite DECIMAL(5,2),
    score_travail_equipe DECIMAL(5,2),
    score_initiative DECIMAL(5,2),
    points_forts TEXT,
    points_amelioration TEXT,
    objectifs_periode_suivante TEXT,
    commentaire_evaluateur TEXT,
    commentaire_employe TEXT,
    statut VARCHAR(50) DEFAULT 'brouillon', -- brouillon, valide, signe
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 7: SELF-SERVICE EMPLOYÉ
-- ============================================================

-- Table Employee_Requests_RH (Demandes employés - utilise notification)
CREATE TABLE public.employee_requests_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_demande VARCHAR(100) NOT NULL, -- attestation_travail, attestation_salaire, remboursement, avance
    description TEXT,
    montant_demande DECIMAL(15,2),
    justificatif_path VARCHAR(500),
    statut VARCHAR(50) DEFAULT 'en_attente', -- en_attente, approuve, refuse, traite
    processed_by BIGINT REFERENCES public.person(id),
    processing_date TIMESTAMP,
    processing_comment TEXT,
    document_genere_path VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 8: PORTAIL MANAGER
-- ============================================================

-- Table Manager_Dashboard_RH (Tableau de bord manager - utilise notification)
CREATE TABLE public.manager_dashboard_rh (
    id BIGSERIAL PRIMARY KEY,
    manager_id BIGINT REFERENCES public.person(id) ON DELETE CASCADE,
    personnel_id BIGINT REFERENCES public.personnel_rh(id),
    type_alerte VARCHAR(100),
    message TEXT,
    severite VARCHAR(20) DEFAULT 'info', -- info, warning, critical
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 9: AUTOMATISATION ET IA
-- ============================================================

-- Table Documents_Templates_RH (Templates documents auto-générés)
CREATE TABLE public.documents_templates_rh (
    id BIGSERIAL PRIMARY KEY,
    type_document VARCHAR(100) NOT NULL,
    nom_template VARCHAR(255) NOT NULL,
    template_content TEXT NOT NULL,
    variables_disponibles TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Chatbot_Conversations_RH (Historique chatbot RH)
CREATE TABLE public.chatbot_conversations_rh (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT REFERENCES public.person(id),
    session_id VARCHAR(255) NOT NULL,
    question TEXT NOT NULL,
    reponse TEXT NOT NULL,
    categorie VARCHAR(100),
    satisfaction_score INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Anomalies_Detection_RH (Détection anomalies)
CREATE TABLE public.anomalies_detection_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id),
    type_anomalie VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    donnees_detectees TEXT,
    severite VARCHAR(20) DEFAULT 'medium',
    statut VARCHAR(50) DEFAULT 'detecte', -- detecte, investigate, resolu, faux_positif
    investigated_by BIGINT REFERENCES public.person(id),
    investigation_date TIMESTAMP,
    resolution_comment TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table Performance_Predictions_RH (Prédictions IA)
CREATE TABLE public.performance_predictions_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_prediction VARCHAR(100) NOT NULL, -- turnover_risk, performance_trend, formation_recommandee
    score_prediction DECIMAL(5,2),
    confidence_level DECIMAL(5,2),
    facteurs_cles TEXT,
    recommandations TEXT,
    date_prediction DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 10: CONFORMITÉ ET AUDIT
-- ============================================================

-- Table Audit_Log_RH (Journalisation des actions)
CREATE TABLE public.audit_log_rh (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES public.person(id),
    action VARCHAR(100) NOT NULL, -- CREATE, UPDATE, DELETE, VIEW, EXPORT
    table_name VARCHAR(100) NOT NULL,
    record_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- CREATE INDEXES (Nouvelles tables uniquement)
-- ============================================================

-- Personnel_RH indexes
CREATE INDEX idx_personnel_rh_person ON public.personnel_rh(person_id);
CREATE INDEX idx_personnel_rh_post ON public.personnel_rh(post_id);
CREATE INDEX idx_personnel_rh_matricule ON public.personnel_rh(matricule);
CREATE INDEX idx_personnel_rh_statut ON public.personnel_rh(statut);

-- Contracts_RH indexes
CREATE INDEX idx_contracts_rh_personnel ON public.contracts_rh(personnel_id);
CREATE INDEX idx_contracts_rh_statut ON public.contracts_rh(statut);

-- Leave_Requests_RH indexes
CREATE INDEX idx_leave_requests_personnel ON public.leave_requests_rh(personnel_id);
CREATE INDEX idx_leave_requests_statut ON public.leave_requests_rh(statut);
CREATE INDEX idx_leave_requests_dates ON public.leave_requests_rh(date_debut, date_fin);

-- Attendance_RH indexes
CREATE INDEX idx_attendance_personnel ON public.attendance_rh(personnel_id);
CREATE INDEX idx_attendance_date ON public.attendance_rh(date_pointage);

-- Payslips_RH indexes
CREATE INDEX idx_payslips_personnel ON public.payslips_rh(personnel_id);
CREATE INDEX idx_payslips_period ON public.payslips_rh(annee, mois);
CREATE INDEX idx_payslips_statut ON public.payslips_rh(statut);

-- Performance_Evaluations_RH indexes
CREATE INDEX idx_performance_personnel ON public.performance_evaluations_rh(personnel_id);
CREATE INDEX idx_performance_evaluateur ON public.performance_evaluations_rh(evaluateur_id);

-- Audit_Log_RH indexes
CREATE INDEX idx_audit_user ON public.audit_log_rh(user_id);
CREATE INDEX idx_audit_table ON public.audit_log_rh(table_name);
CREATE INDEX idx_audit_created ON public.audit_log_rh(created_at);

-- ============================================================
-- COMMENTAIRES SUR LES TABLES
-- ============================================================

COMMENT ON TABLE public.personnel_rh IS 'Extension de person pour données RH complètes';
COMMENT ON TABLE public.contracts_rh IS 'Contrats de travail et suivi des périodes d''essai';
COMMENT ON TABLE public.leave_requests_rh IS 'Demandes de congés avec workflow de validation (utilise notification)';
COMMENT ON TABLE public.attendance_rh IS 'Pointage et feuille de présence des employés';
COMMENT ON TABLE public.payslips_rh IS 'Bulletins de paie avec calculs CNAPS, OSTIE, IRSA';
COMMENT ON TABLE public.performance_evaluations_rh IS 'Evaluations periodiques des employes';
COMMENT ON TABLE public.employee_requests_rh IS 'Self-service: demandes d''attestations, avances, etc. (utilise notification)';
COMMENT ON TABLE public.manager_dashboard_rh IS 'Alertes et tableaux de bord pour managers (utilise notification)';
COMMENT ON TABLE public.chatbot_conversations_rh IS 'Historique conversations chatbot RH (IA)';
COMMENT ON TABLE public.anomalies_detection_rh IS 'Détection automatique d''anomalies (IA)';
COMMENT ON TABLE public.audit_log_rh IS 'Journalisation complète pour conformité et audit';

-- ============================================================
-- TOTAL: 21 NOUVELLES TABLES RH
-- ============================================================