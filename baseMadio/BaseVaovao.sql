-- ============================================================
-- SECTION 1: TYPES PERSONNALISÉS
-- ============================================================

CREATE TYPE secteur_activite_enum AS ENUM ('agricole', 'non_agricole', 'commun');
CREATE TYPE statut_contrat_enum AS ENUM ('actif', 'termine', 'suspendu', 'resilie');
CREATE TYPE statut_demande_enum AS ENUM ('en_attente', 'approuve', 'refuse', 'annule', 'traite');
CREATE TYPE statut_general_enum AS ENUM ('actif', 'inactif', 'brouillon', 'valide', 'paye');
CREATE TYPE type_pointage_enum AS ENUM ('manuel', 'badgeuse', 'mobile');
CREATE TYPE type_formation_enum AS ENUM ('interne', 'externe', 'e_learning', 'certification');
CREATE TYPE severite_enum AS ENUM ('info', 'warning', 'critical', 'medium');

-- ============================================================
-- SECTION 2: GESTION DU PERSONNEL ET CONTRATS
-- ============================================================

CREATE TABLE public.personnel_rh (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT UNIQUE NOT NULL REFERENCES public.person(id) ON DELETE CASCADE,
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
    statut statut_general_enum DEFAULT 'actif',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.contract_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    description TEXT,
    duree_legale_mois INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.contracts_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    contract_type_id BIGINT NOT NULL REFERENCES public.contract_types(id) ON DELETE RESTRICT,
    date_debut DATE NOT NULL,
    date_fin DATE,
    duree_essai_mois INTEGER DEFAULT 0,
    date_fin_essai DATE,
    is_essai_valide BOOLEAN DEFAULT FALSE,
    salaire_base NUMERIC(15, 2),
    statut statut_contrat_enum DEFAULT 'actif',
    motif_fin TEXT,
    document_path VARCHAR(500),
    duree_preavis INTEGER,
    indemnite_preavis NUMERIC(15, 2) DEFAULT 0,
    retenue_preavis NUMERIC(15, 2) DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_dates_coherentes CHECK (date_fin IS NULL OR date_fin >= date_debut),
    CONSTRAINT chk_date_essai_coherente CHECK (date_fin_essai IS NULL OR date_fin_essai >= date_debut),
    CONSTRAINT chk_duree_essai_positive CHECK (duree_essai_mois >= 0),
    CONSTRAINT chk_salaire_positif CHECK (salaire_base IS NULL OR salaire_base >= 0)
);

CREATE TABLE public.career_history_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
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

CREATE TABLE public.documents_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_document VARCHAR(100) NOT NULL,
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

-- ============================================================
-- SECTION 3: GESTION DES CONGÉS ET ABSENCES
-- ============================================================

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
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    leave_type_id BIGINT NOT NULL REFERENCES public.leave_types_rh(id),
    annee INTEGER NOT NULL,
    solde_initial DECIMAL(5,2) DEFAULT 0,
    solde_acquis DECIMAL(5,2) DEFAULT 0,
    solde_pris DECIMAL(5,2) DEFAULT 0,
    solde_restant DECIMAL(5,2) DEFAULT 0,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, leave_type_id, annee)
);

CREATE TABLE public.leave_requests_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    leave_type_id BIGINT NOT NULL REFERENCES public.leave_types_rh(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nombre_jours DECIMAL(5,2) NOT NULL,
    motif TEXT,
    justificatif_path VARCHAR(500),
    statut statut_demande_enum DEFAULT 'en_attente',
    validated_by BIGINT REFERENCES public.person(id),
    validation_date TIMESTAMP,
    validation_comment TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 4: GESTION DU TEMPS ET PRÉSENCES
-- ============================================================

CREATE TABLE public.attendance_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    date_pointage DATE NOT NULL,
    heure_arrivee TIME,
    heure_depart TIME,
    heure_pause_debut TIME,
    heure_pause_fin TIME,
    duree_travail_minutes INTEGER,
    statut VARCHAR(50) DEFAULT 'present',
    type_pointage type_pointage_enum DEFAULT 'manuel',
    commentaire TEXT,
    validated_by BIGINT REFERENCES public.person(id),
    is_validated BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, date_pointage)
);
CREATE TABLE retard_deduction (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL,
    date_pointage DATE NOT NULL,
    minutes_retard INT NOT NULL,
    jours_couverts DECIMAL(6,2) DEFAULT 0, -- retard couvert par congé
    montant_deduit DECIMAL(12,2) DEFAULT 0, -- montant déduit du salaire
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.type_hs (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    taux_majoration DECIMAL(5,2),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.overtime_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    attendance_id BIGINT REFERENCES public.attendance_rh(id),
    date_hs DATE NOT NULL,
    nombre_heures DECIMAL(5,2) NOT NULL,
    type_hs_id BIGINT NOT NULL REFERENCES public.type_hs(id),
    taux_majoration DECIMAL(5,2) DEFAULT 1.0,
    montant_hs DECIMAL(15,2),
    statut statut_demande_enum DEFAULT 'en_attente',
    validated_by BIGINT REFERENCES public.person(id),
    validation_date TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 5: GESTION DE LA PAIE
-- ============================================================

CREATE TABLE public.company_info_rh (
    id BIGSERIAL PRIMARY KEY,
    nom_entreprise VARCHAR(255) NOT NULL,
    secteur_activite secteur_activite_enum NOT NULL DEFAULT 'non_agricole',
    numero_cnaps VARCHAR(50),
    numero_ostie VARCHAR(50),
    numero_nif VARCHAR(50),
    numero_stat VARCHAR(50),
    adresse TEXT,
    email VARCHAR(255),
    telephone VARCHAR(100),
    logo_path VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE public.salary_parameters_rh (
    id BIGSERIAL PRIMARY KEY,
    nom_parametre VARCHAR(100) NOT NULL,
    description TEXT,
    valeur DECIMAL(10,4) NOT NULL,
    type VARCHAR(50),
    categorie VARCHAR(50),
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(nom_parametre,date_debut_validite)
);

-- ============================================================
-- TABLE CNAPS
-- ============================================================
CREATE TABLE cnaps (
    id SERIAL PRIMARY KEY,
    secteur_activite secteur_activite_enum DEFAULT 'non_agricole', -- 'agricole', 'non_agricole'
    heures_mensuel DECIMAL(10,4) NOT NULL,
    taux_employeur DECIMAL(5,4) NOT NULL,
    taux_employe DECIMAL(5,4) NOT NULL,
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    reference_legale TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- TABLE OSTIE
-- ============================================================
CREATE TABLE ostie (
    id SERIAL PRIMARY KEY,
    taux_employeur DECIMAL(5,4) NOT NULL,
    taux_employe DECIMAL(5,4) NOT NULL,
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    reference_legale TEXT,
    actif BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- TABLE IRSA (Barème progressif)
-- ============================================================
CREATE TABLE irsa (
    id SERIAL PRIMARY KEY,
    numero_tranche INT NOT NULL,
    seuil_min DECIMAL(15,4) NOT NULL,
    seuil_max DECIMAL(15,4),
    limite DECIMAL(15,4),
    taux DECIMAL(5,4) NOT NULL,
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    reference_legale TEXT,
    actif BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_tranche UNIQUE (numero_tranche, date_debut_validite)
);

-- ============================================================
-- TABLE MAJORATIONS
-- ============================================================
CREATE TABLE majorations_heures (
    id SERIAL PRIMARY KEY,
    type_majoration VARCHAR(50) NOT NULL, -- 'weekend', 'nuit', 'jour_ferie'
    taux_majoration DECIMAL(5,4) NOT NULL,
    heure_debut TIME, -- Pour 'nuit' : 22:00
    heure_fin TIME,   -- Pour 'nuit' : 05:00
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE,
    actif BOOLEAN DEFAULT TRUE
);

CREATE TABLE public.salary_components_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_composante VARCHAR(100) NOT NULL,
    montant DECIMAL(15,2) NOT NULL,
    is_recurring BOOLEAN DEFAULT TRUE,
    date_debut DATE,
    date_fin DATE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table des bulletins de paie
CREATE TABLE public.payslips_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    mois INTEGER NOT NULL CHECK (mois BETWEEN 1 AND 12),
    annee INTEGER NOT NULL,
    
    salaire_base DECIMAL(15,2) NOT NULL,
    total_primes DECIMAL(15,2) DEFAULT 0,
    total_indemnites DECIMAL(15,2) DEFAULT 0,
    heures_supplementaires DECIMAL(15,2) DEFAULT 0,
    absences DECIMAL(5,2) DEFAULT 0,
    
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
    
    salaire_net DECIMAL(15,2),
    autres_indemnites DECIMAL(15,2) DEFAULT 0,
    
    statut statut_general_enum DEFAULT 'brouillon',
    date_paiement DATE,
    mode_paiement VARCHAR(50),
    pdf_path VARCHAR(500),
    
    created_by BIGINT REFERENCES public.person(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(personnel_id, mois, annee)
);

-- Table des lignes de paie
CREATE TABLE public.payslip_lines_rh (
    id BIGSERIAL PRIMARY KEY,
    payslip_id BIGINT NOT NULL REFERENCES public.payslips_rh(id) ON DELETE CASCADE,
    
    code VARCHAR(50) NOT NULL,      -- SB, HS25, INDT, COT_CNAPS, etc.
    label VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,      -- GAIN, RETENUE, EMPLOYER
    
    quantite DECIMAL(15,2) DEFAULT 1,
    taux DECIMAL(15,2) DEFAULT 0,
    montant DECIMAL(15,2) NOT NULL,
    
    ordre INTEGER DEFAULT 0
);

CREATE INDEX idx_payslip_lines_rh_payslip_id ON public.payslip_lines_rh(payslip_id);




-- ============================================================
-- SECTION 6: GESTION DES COMPÉTENCES ET FORMATIONS
-- ============================================================

CREATE TABLE public.competences_rh (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    categorie VARCHAR(100),
    niveau_requis INTEGER DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.competences_mapping_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    competence_id BIGINT NOT NULL REFERENCES public.competences_rh(id),
    niveau_actuel INTEGER DEFAULT 1,
    date_evaluation DATE,
    validated_by BIGINT REFERENCES public.person(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(personnel_id, competence_id)
);

CREATE TABLE public.formations_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    titre VARCHAR(255) NOT NULL,
    organisme VARCHAR(255),
    type_formation type_formation_enum,
    date_debut DATE,
    date_fin DATE,
    duree_heures INTEGER,
    cout DECIMAL(15,2),
    statut statut_general_enum DEFAULT 'planifiee',
    certificat_obtenu BOOLEAN DEFAULT FALSE,
    certificat_path VARCHAR(500),
    competence_visee BIGINT REFERENCES public.competences_rh(id),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 7: GESTION DES PERFORMANCES
-- ============================================================

CREATE TABLE public.performance_evaluations_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
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
    statut statut_general_enum DEFAULT 'brouillon',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 8: SELF-SERVICE EMPLOYÉ
-- ============================================================

CREATE TABLE public.employee_requests_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_demande VARCHAR(100) NOT NULL,
    description TEXT,
    montant_demande DECIMAL(15,2),
    justificatif_path VARCHAR(500),
    statut statut_demande_enum DEFAULT 'en_attente',
    processed_by BIGINT REFERENCES public.person(id),
    processing_date TIMESTAMP,
    processing_comment TEXT,
    document_genere_path VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 9: PORTAIL MANAGER
-- ============================================================

CREATE TABLE public.manager_dashboard_rh (
    id BIGSERIAL PRIMARY KEY,
    manager_id BIGINT NOT NULL REFERENCES public.person(id) ON DELETE CASCADE,
    personnel_id BIGINT REFERENCES public.personnel_rh(id),
    type_alerte VARCHAR(100),
    message TEXT,
    severite severite_enum DEFAULT 'info',
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 10: AUTOMATISATION ET IA
-- ============================================================

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

CREATE TABLE public.anomalies_detection_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT REFERENCES public.personnel_rh(id),
    type_anomalie VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    donnees_detectees TEXT,
    severite severite_enum DEFAULT 'medium',
    statut statut_demande_enum DEFAULT 'detecte',
    investigated_by BIGINT REFERENCES public.person(id),
    investigation_date TIMESTAMP,
    resolution_comment TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.performance_predictions_rh (
    id BIGSERIAL PRIMARY KEY,
    personnel_id BIGINT NOT NULL REFERENCES public.personnel_rh(id) ON DELETE CASCADE,
    type_prediction VARCHAR(100) NOT NULL,
    score_prediction DECIMAL(5,2),
    confidence_level DECIMAL(5,2),
    facteurs_cles TEXT,
    recommandations TEXT,
    date_prediction DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SECTION 11: CONFORMITÉ ET AUDIT
-- ============================================================

CREATE TABLE public.audit_log_rh (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES public.person(id),
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    record_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- INDEXES
-- ============================================================

-- Indexes pour personnel_rh
CREATE INDEX idx_personnel_rh_person ON public.personnel_rh(person_id);
CREATE INDEX idx_personnel_rh_post ON public.personnel_rh(post_id);
CREATE INDEX idx_personnel_rh_matricule ON public.personnel_rh(matricule);
CREATE INDEX idx_personnel_rh_statut ON public.personnel_rh(statut);

-- Indexes pour contracts_rh
CREATE INDEX idx_contracts_rh_personnel ON public.contracts_rh(personnel_id);
CREATE INDEX idx_contracts_rh_type ON public.contracts_rh(contract_type_id);
CREATE INDEX idx_contracts_rh_statut ON public.contracts_rh(statut);
CREATE INDEX idx_contracts_rh_dates ON public.contracts_rh(date_debut, date_fin);

-- Indexes pour leave_requests_rh
CREATE INDEX idx_leave_requests_personnel ON public.leave_requests_rh(personnel_id);
CREATE INDEX idx_leave_requests_type ON public.leave_requests_rh(leave_type_id);
CREATE INDEX idx_leave_requests_statut ON public.leave_requests_rh(statut);
CREATE INDEX idx_leave_requests_dates ON public.leave_requests_rh(date_debut, date_fin);

-- Indexes pour attendance_rh
CREATE INDEX idx_attendance_personnel ON public.attendance_rh(personnel_id);
CREATE INDEX idx_attendance_date ON public.attendance_rh(date_pointage);
CREATE INDEX idx_attendance_statut ON public.attendance_rh(statut);

-- Indexes pour overtime_rh
CREATE INDEX idx_overtime_personnel ON public.overtime_rh(personnel_id);
CREATE INDEX idx_overtime_attendance ON public.overtime_rh(attendance_id);
CREATE INDEX idx_overtime_date ON public.overtime_rh(date_hs);

-- Indexes pour salary_parameters_rh
CREATE INDEX idx_salary_params_secteur ON public.salary_parameters_rh(secteur_activite, is_active);
CREATE INDEX idx_salary_params_categorie ON public.salary_parameters_rh(categorie, secteur_activite);
CREATE INDEX idx_salary_params_dates ON public.salary_parameters_rh(date_debut_validite, date_fin_validite);

-- Indexes pour payslips_rh
CREATE INDEX idx_payslips_personnel ON public.payslips_rh(personnel_id);
CREATE INDEX idx_payslips_period ON public.payslips_rh(annee, mois);
CREATE INDEX idx_payslips_statut ON public.payslips_rh(statut);

-- Indexes pour competences_mapping_rh
CREATE INDEX idx_competences_mapping_personnel ON public.competences_mapping_rh(personnel_id);
CREATE INDEX idx_competences_mapping_competence ON public.competences_mapping_rh(competence_id);

-- Indexes pour performance_evaluations_rh
CREATE INDEX idx_performance_personnel ON public.performance_evaluations_rh(personnel_id);
CREATE INDEX idx_performance_evaluateur ON public.performance_evaluations_rh(evaluateur_id);
CREATE INDEX idx_performance_date ON public.performance_evaluations_rh(date_evaluation);

-- Indexes pour audit_log_rh
CREATE INDEX idx_audit_user ON public.audit_log_rh(user_id);
CREATE INDEX idx_audit_table ON public.audit_log_rh(table_name);
CREATE INDEX idx_audit_created ON public.audit_log_rh(created_at);

-- ============================================================
-- COMMENTAIRES
-- ============================================================

COMMENT ON TABLE public.personnel_rh IS 'Extension de person pour données RH complètes';
COMMENT ON TABLE public.contracts_rh IS 'Contrats de travail et suivi des périodes d''essai';
COMMENT ON TABLE public.leave_requests_rh IS 'Demandes de congés avec workflow de validation';
COMMENT ON TABLE public.attendance_rh IS 'Pointage et feuille de présence des employés';
COMMENT ON TABLE public.payslips_rh IS 'Bulletins de paie avec calculs CNAPS, OSTIE, IRSA';
COMMENT ON TABLE public.performance_evaluations_rh IS 'Évaluations périodiques des employés';
COMMENT ON TABLE public.employee_requests_rh IS 'Self-service: demandes d''attestations, avances, etc.';
COMMENT ON TABLE public.manager_dashboard_rh IS 'Alertes et tableaux de bord pour managers';
COMMENT ON TABLE public.chatbot_conversations_rh IS 'Historique conversations chatbot RH (IA)';
COMMENT ON TABLE public.anomalies_detection_rh IS 'Détection automatique d''anomalies (IA)';
COMMENT ON TABLE public.audit_log_rh IS 'Journalisation complète pour conformité et audit';