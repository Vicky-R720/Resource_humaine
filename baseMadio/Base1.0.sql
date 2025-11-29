-- Diploma Table
CREATE TABLE public.diploma (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    niveau INTEGER
);

-- Filiere Table (Academic Tracks)
CREATE TABLE public.filiere (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    niveau INTEGER
);

-- Contract Types Table
CREATE TABLE IF NOT EXISTS public.contract_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL,
    requires_end_date BOOLEAN NOT NULL DEFAULT TRUE,
    duree_max_mois INTEGER,
    has_trial_period BOOLEAN NOT NULL DEFAULT TRUE,
    duree_essai_max_mois INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- Sector Table
CREATE TABLE public.sector (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE categorie_personnel (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    niveau INTEGER NOT NULL  -- 1: ouvrier, 2: employé, 3:TAM, 4:cadre, 5:dirigeant
);





-- Person Table
CREATE TABLE public.person (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse TEXT,
    naissance DATE,
    contact VARCHAR(100)
);

CREATE TABLE utilisateur (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT UNIQUE REFERENCES person(id) ON DELETE CASCADE,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYE',
    actif BOOLEAN DEFAULT TRUE
);


CREATE TABLE service (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    manager_id BIGINT REFERENCES utilisateur(id)
);

CREATE TABLE equipe (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    service_id BIGINT NOT NULL REFERENCES service(id),
    manager_id BIGINT REFERENCES utilisateur(id)
);



-- Post Table (Job Positions)
CREATE TABLE post (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    missions TEXT,
    categorie_id BIGINT REFERENCES categorie_personnel(id),
    equipe_id BIGINT REFERENCES equipe(id)  -- <<< ICI
);

-- Offers Table (Job Offers)
CREATE TABLE public.offers (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES public.post(id),
    company_name VARCHAR(255),
    location VARCHAR(255) NOT NULL,
    contract_type_id BIGINT REFERENCES public.contract_types(id),
    required_profile TEXT,
    experience_level VARCHAR(100),
    diploma VARCHAR(100),
    available_places INTEGER DEFAULT 1,
    publication_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    filiere VARCHAR(100)
);

-- Appliance Table (Job Applications)
CREATE TABLE public.appliance (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    offer_id BIGINT REFERENCES public.offers(id),
    cv_link TEXT,
    skills TEXT,
    person_id BIGINT REFERENCES public.person(id),
    experiencelevel VARCHAR(255) DEFAULT '4',
    traitement VARCHAR(200) DEFAULT 'en_cours'
);

-- Academic Qualification Table
CREATE TABLE public.academical_qualification (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    diploma_id BIGINT REFERENCES public.diploma(id),
    sector_id BIGINT REFERENCES public.sector(id),
    appliance_id BIGINT REFERENCES public.appliance(id),
    filiere_id BIGINT REFERENCES public.filiere(id)
);

-- Notification Table
CREATE TABLE public.notification (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT REFERENCES public.person(id),
    message TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- QCM Questions Table
CREATE TABLE public.qcm_questions (
    id SERIAL PRIMARY KEY,
    texte TEXT NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- QCM Answers Table
CREATE TABLE public.qcm_reponses (
    id SERIAL PRIMARY KEY,
    texte TEXT NOT NULL,
    est_correcte BOOLEAN DEFAULT FALSE,
    question_id INTEGER NOT NULL REFERENCES qcm_questions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




-- Table Notification RH
CREATE TABLE public.notification_rh (
    id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL REFERENCES public.utilisateur(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    related_entity_type VARCHAR(100),
    related_entity_id BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITHOUT TIME ZONE
);

-- Index pour optimiser les requêtes
CREATE INDEX idx_notification_rh_recipient ON public.notification_rh USING btree (recipient_id);
CREATE INDEX idx_notification_rh_is_read ON public.notification_rh USING btree (is_read);
CREATE INDEX idx_notification_rh_type ON public.notification_rh USING btree (type);
CREATE INDEX idx_notification_rh_created_at ON public.notification_rh USING btree (created_at DESC);
CREATE INDEX idx_notification_rh_related_entity ON public.notification_rh USING btree (related_entity_type, related_entity_id);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE public.notification_rh IS 'Table de gestion des notifications pour le système RH';
COMMENT ON COLUMN public.notification_rh.recipient_id IS 'Référence vers l''utilisateur destinataire';
COMMENT ON COLUMN public.notification_rh.title IS 'Titre de la notification';
COMMENT ON COLUMN public.notification_rh.message IS 'Contenu du message de notification';
COMMENT ON COLUMN public.notification_rh.type IS 'Type de notification: leave_request, leave_approval, leave_rejection, alert, etc.';
COMMENT ON COLUMN public.notification_rh.is_read IS 'Indique si la notification a été lue';
COMMENT ON COLUMN public.notification_rh.related_entity_type IS 'Type d''entité associée: LeaveRequest, LeaveBalance, etc.';
COMMENT ON COLUMN public.notification_rh.related_entity_id IS 'ID de l''entité associée';
COMMENT ON COLUMN public.notification_rh.expires_at IS 'Date d''expiration de la notification (optionnelle)';