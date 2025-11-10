-- =========================
-- 1. TABLES DE BASE
-- =========================
CREATE TABLE personne (
    id_personne      SERIAL PRIMARY KEY,
    nom              VARCHAR(100) NOT NULL,
    prenom           VARCHAR(100) NOT NULL,
    adresse          TEXT,
    naissance        DATE,
    contact          VARCHAR(100)
);

CREATE TABLE filiere (
    id_personne      SERIAL PRIMARY KEY,
    name              VARCHAR(100) NOT NULL
);

CREATE TABLE poste (
    id_poste         SERIAL PRIMARY KEY,
    nom              VARCHAR(100) NOT NULL
);

CREATE TABLE organisme_social (
    id_organisme     SERIAL PRIMARY KEY,
    nom              VARCHAR(150) NOT NULL,
    adresse          TEXT,
    contact          VARCHAR(100)
);

-- =========================
-- 2. RESSOURCES HUMAINES
-- =========================
CREATE TABLE personnel (
    id_personnel     SERIAL PRIMARY KEY,
    id_personne      INT NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    arrivee          DATE NOT NULL,
    id_poste         INT REFERENCES poste(id_poste)
);

-- =========================
-- 3. RECRUTEMENT
-- =========================
CREATE TABLE offre (
    id_offre         SERIAL PRIMARY KEY,
    description      TEXT,
    type_contrat     VARCHAR(100),
    mission_principale TEXT,
    profil_recherche TEXT,
    niveau_xp        VARCHAR(100),
    id_poste         INT REFERENCES poste(id_poste),
    nb_places        INT
);

CREATE TABLE candidature (
    id_candidature   SERIAL PRIMARY KEY,
    id_personne      INT NOT NULL REFERENCES personne(id_personne) ON DELETE CASCADE,
    date_candidature DATE NOT NULL,
    id_offre         INT NOT NULL REFERENCES offre(id_offre) ON DELETE CASCADE,
    diplomes         TEXT,
    competences      TEXT,
    cv               TEXT      -- chemin fichier ou texte base64
);

-- =========================
-- 4. EVALUATION & NOTES
-- =========================
CREATE TABLE evaluation (
    id_evaluation    SERIAL PRIMARY KEY,
    type             VARCHAR(50),
    bareme           TEXT        -- description du système de notation
);

CREATE TABLE note (
    id_note          SERIAL PRIMARY KEY,
    id_evaluation    INT NOT NULL REFERENCES evaluation(id_evaluation) ON DELETE CASCADE,
    id_candidature   INT NOT NULL REFERENCES candidature(id_candidature) ON DELETE CASCADE,
    valeur           NUMERIC(5,2)  -- note sur 100, par exemple
);

CREATE TABLE Post (
    id SERIAL PRIMARY KEY,  -- Identifiant unique auto-incrémenté
    name VARCHAR(255) NOT NULL,  -- Nom du poste
    description TEXT,  -- Description générale du poste
    missions TEXT  -- Missions principales associées au poste
);

CREATE TABLE contract_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE offers (
    id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    contract_type_id INTEGER NOT NULL,
    required_profile TEXT,
    experience_level VARCHAR(100),
    diploma VARCHAR(100),
    available_places INTEGER DEFAULT 1,
    publication_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Clés étrangères
    CONSTRAINT fk_offer_post 
        FOREIGN KEY (post_id) 
        REFERENCES post(id) 
        ON DELETE CASCADE,
        
    CONSTRAINT fk_offer_contract_type 
        FOREIGN KEY (contract_type_id) 
        REFERENCES contract_types(id) 
        ON DELETE CASCADE
);