-- TABLE Matière
CREATE TABLE matiere (
    id_matiere SERIAL PRIMARY KEY,
    nom_matiere VARCHAR(50) NOT NULL,
    credit INT NOT NULL
);

-- TABLE Classe (Semestre)
CREATE TABLE classe (
    id_classe SERIAL PRIMARY KEY,
    semestre VARCHAR(50) NOT NULL
);

-- TABLE Promotion
CREATE TABLE promotion (
    id_promotion SERIAL PRIMARY KEY,
    nom_promotion VARCHAR(50) NOT NULL
);

-- TABLE Etudiant
CREATE TABLE etudiant (
    id_etudiant SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    id_promotion INT REFERENCES promotion(id_promotion),
    id_classe INT REFERENCES classe(id_classe)
);

-- TABLE des Notes
CREATE TABLE note (
    id_note SERIAL PRIMARY KEY,
    id_matiere INT REFERENCES matiere(id_matiere),
    id_etudiant INT REFERENCES etudiant(id_etudiant),
    note NUMERIC(5,2),
    session VARCHAR(50)
);

-- TABLE Classe - Élève (si un élève peut être dans plusieurs classes)
CREATE TABLE classe_eleve (
    id_classe INT REFERENCES classe(id_classe),
    id_etudiant INT REFERENCES etudiant(id_etudiant),
    PRIMARY KEY(id_classe, id_etudiant)
);
