--
-- PostgreSQL database dump
-- Base de données: Orinasa Malagasy - Fanamboarana zava-pisotro misy alikaola
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Tables structure
--

CREATE TABLE public.diploma (
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL,
    niveau integer
);

CREATE TABLE public.filiere (
    id bigserial PRIMARY KEY,
    name character varying(100) NOT NULL,
    niveau integer
);

--
-- Data for Name: diploma; Type: TABLE DATA
--

COPY public.diploma (id, name, niveau) FROM stdin;
1	Baccalauréat	1
2	Licence Agroalimentaire	4
3	Master Brasserie	7
4	Licence Œnologie	4
5	Master Distillation	7
6	BTS Biotechnologie	3
7	Licence Chimie	4
8	Master Management	7
9	BTS Commerce	3
10	Licence Marketing	4
11	Master Qualité	7
12	Licence Biochimie	4
13	Master Recherche	7
14	BTS Production	3
15	Licence Logistique	4
16	Master Export	7
17	Licence Sommellerie	4
18	BTS Viticulture	3
\.


--
-- Data for Name: filiere; Type: TABLE DATA
--

COPY public.filiere (id, name, niveau) FROM stdin;
1	BACC	1
2	BACC+1	2
3	BACC+2	3
4	Licence	4
5	BACC+4	5
6	BACC+5	6
7	Master	7
8	Doctorat	8
\.


--
-- Create other tables with Malagasy theme
--

CREATE TABLE public.contract_types (
    id bigserial PRIMARY KEY,
    name character varying(100) NOT NULL
);

CREATE TABLE public.sector (
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL
);

CREATE TABLE public.post (
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL,
    description text NOT NULL,
    missions text
);

CREATE TABLE public.person (
    id bigserial PRIMARY KEY,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    adresse text,
    naissance date,
    contact character varying(100),
    mdp character varying(300) DEFAULT 'utilisateur'::character varying,
    pdp character varying(300) DEFAULT 'img'::character varying
);

CREATE TABLE public.offers (
    id bigserial PRIMARY KEY,
    post_id bigint REFERENCES public.post(id),
    company_name character varying(255),
    location character varying(255) NOT NULL,
    contract_type_id bigint REFERENCES public.contract_types(id),
    required_profile text,
    experience_level character varying(100),
    diploma character varying(100),
    available_places integer DEFAULT 1,
    publication_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    filiere character varying(100)
);

CREATE TABLE public.appliance (
    id bigserial PRIMARY KEY,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    offer_id bigint REFERENCES public.offers(id),
    cv_link text,
    skills text,
    person_id bigint REFERENCES public.person(id),
    experiencelevel character varying(255) DEFAULT '4',
    traitement character varying(200) DEFAULT 'en_cours'::character varying
);

CREATE TABLE public.academical_qualification (
    id bigserial PRIMARY KEY,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    diploma_id bigint REFERENCES public.diploma(id),
    sector_id bigint REFERENCES public.sector(id),
    appliance_id bigint REFERENCES public.appliance(id),
    filiere_id bigint REFERENCES public.filiere(id)
);

CREATE TABLE public.notification (
    id bigserial PRIMARY KEY,
    person_id bigint REFERENCES public.person(id),
    message text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

--
-- Insert Malagasy data
--

-- Contract Types
COPY public.contract_types (id, name) FROM stdin;
1	CDI (Asa maharitra)
2	CDD (Asa fotoana)
3	Stage (Fampiofanana)
4	Alternance (Fianarana sy asa)
5	Intérim (Asa vonjimaika)
6	Freelance (Mpiasa indepandanty)
\.

-- Sectors
COPY public.sector (id, name) FROM stdin;
1	Agroalimentaire (Sakafo sy zava-pisotro)
2	Biotechnologie
3	Chimie
4	Commerce (Varotra)
5	Marketing
6	Qualité (Kalitao)
7	Production (Famokarana)
8	Logistique
9	Recherche et Développement (Fikarohana sy fampandrosoana)
10	Management (Fitantanana)
11	Vente (Fivarotana)
12	Export (Fanondranana)
\.

-- Posts (Toerana asa)
COPY public.post (id, name, description, missions) FROM stdin;
1	Mpamokatra toaka	Tompon'andraikitra amin'ny famokarana sy fanaraha-maso ny kalitaon'ny toaka. Mahay ny fomba fanaovana toaka tranainy sy maoderina.	Famokarana toaka, fanaraha-maso fermentation, distillation, fanendasana
2	Mpamokatra labiera	Manam-pahaizana manokana amin'ny famokarana labiera artisanale. Mahay ny fomba fanaovana maltage, brassage ary fermentation.	Famoronana recettes vaovao, fanaraha-maso ny fizotran'ny famokarana
3	Mpitantana cave	Manam-pahaizana momba ny fanaovana divay sy champagne. Tompon'andraikitra amin'ny fitehirizana sy ny kalitao.	Fanaraha-maso ny fahanasan'ny divay, fananganana champagne
4	Œnologue (Mpanandro divay)	Matihanina momba ny divay, hatramin'ny fanaovana divay ka hatramin'ny fivarotana azy. Mahay ny fitsapana sensorielle.	Fanasafiana divay, torohevitra momba ny fanaovana divay
5	Teknisiana kalitao	Tompon'andraikitra amin'ny fanaraha-maso sy fitsapana ara-pitsaboana ny zava-pisotro misy alikaola. Mahay ny normes ISO sy HACCP.	Fanatontosiana kalitao, fanaraha-maso laboratoara
6	Injeniera R&D	Fampandrosoana vokatra vaovao sy fanatsarana ny fomba famokarana. Innovation amin'ny biotechnologies.	Fikarohana, fampandrosoana vokatra vaovao
7	Mpitantana export	Fivarotana iraisam-pirenena ny vokatra. Fahaizana momba ny lalàna iraisam-pirenena sy fifampiraharahana.	Fikarohana tsena iraisam-pirenena, fifampiraharahana
8	Mpitantana varotra	Paikady varotra sy fampandrosoana ny fivarotana. Fitantanana ny ekipa varotra.	Famolavola paikady varotra, fitantanana mpivarotra
9	Mpitantana famokarana	Fitantanana ny rojo famokarana. Fanatsarana ny fahombiazana sy ny kalitao.	Fanaraha-maso ny famokarana, optimisation
10	Sommelier (Mpanolo-tsakafo)	Manam-pahaizana amin'ny fitsapana divay sy torohevitra momba ny vokatra. Fampiofanana ny ekipa varotra.	Fanasafiana divay, torohevitra amin'ny mpanjifa
\.

-- Persons (Olona)
COPY public.person (id, nom, prenom, adresse, naissance, contact, mdp, pdp) FROM stdin;
1	Rakoto	Jean	15 lalana ny mpamboly, Antananarivo	1985-03-15	jean.rakoto@mail.mg	utilisateur	img
2	Rasoa	Sophie	28 arabe ny labiera, Fianarantsoa	1990-07-22	sophie.rasoa@mail.mg	utilisateur	img
3	Randria	Pierre	42 lalana ny distillerie, Toamasina	1982-11-08	pierre.randria@mail.mg	utilisateur	img
4	Razafy	Marie	33 lalana ny laboratoara, Antsirabe	1988-04-12	marie.razafy@mail.mg	utilisateur	img
5	Andriamalala	Antoine	19 boulevard des Analyses, Toliara	1992-09-03	antoine.andriamalala@mail.mg	utilisateur	img
6	Rajaona	Camille	8 toerana varotra, Mahajanga	1987-06-18	camille.rajaona@mail.mg	utilisateur	img
7	Randrianarisoa	Thomas	52 lalana export, Nosy Be	1984-12-25	thomas.randrianarisoa@mail.mg	utilisateur	img
8	Ramanantsoa	Julie	14 arabe marketing, Antsiranana	1991-01-30	julie.ramanantsoa@mail.mg	utilisateur	img
9	Ravelonarivo	David	7 lalana innovation, Moramanga	1986-08-14	david.ravelonarivo@mail.mg	utilisateur	img
10	Rasolofoniaina	Emma	25 lalana fikarohana, Ambatolampy	1989-05-07	emma.rasolofoniaina@mail.mg	utilisateur	img
11	Randriamanantena	Alexandre	11 lalana fitantanana, Ambositra	1980-02-28	alexandre.randriamanantena@mail.mg	utilisateur	img
12	Razakanirina	Léa	36 arabe fitantanana, Manakara	1983-10-15	lea.razakanirina@mail.mg	utilisateur	img
13	Vonjiniaina	Ralevazaha	LOT 0910C61, Antananarivo	1995-09-05	ralevazaha@mail.mg	admin	img
\.

-- Offers (Tolotra asa)
COPY public.offers (id, post_id, company_name, location, contract_type_id, required_profile, experience_level, diploma, available_places, publication_date, filiere) FROM stdin;
1	1	Distillerie Artisanale Malagasy	Antananarivo	1	Mpamokatra toaka efa za-draharaha. Fahalalana momba ny fomba fanaovana toaka nentim-paharazana.	4	Agroalimentaire	1	2025-09-13 09:24:34.37703	Licence
2	2	Brasserie des Hauts-Plateaux	Antsirabe	1	Mpamokatra labiera ho an'ny famokarana labiera artisanale. Mahay ny fomba fanaovana maltage, brassage ary fermentation.	4	Brasserie	2	2025-09-15 09:24:34.37703	Licence
3	3	Champagne Malagasy	Fianarantsoa	1	Mpitantana cave ho an'ny fanaovana divay sy fananganana champagne. Mahay ny fomba fanaovana champagne.	4	Viticulture-Œnologie	1	2025-09-11 09:24:34.37703	Licence
4	5	Laboratoire Qualité AlcoMalagasy	Toamasina	1	Teknisiana kalitao ho an'ny fanaraha-maso sy fitsapana ny zava-pisotro misy alikaola. Mahay ny normes ISO sy HACCP.	4	Génie Biologique	3	2025-09-16 09:24:34.37703	Licence
5	7	Rhum Tropical Export	Nosy Be	1	Mpitantana export ho an'ny fivarotana rhums premium any ivelany. Ilaina ny teny anglisy sy espaniola.	4	Commerce International	1	2025-09-14 09:24:34.37703	Licence
6	6	Innovation Spirits Lab	Antananarivo	1	Injeniera R&D ho an'ny fampandrosoana zava-pisotro misy alikaola vaovao. Biotechnologies sy fermentation innovante.	4	Agroalimentaire	2	2025-09-12 09:24:34.37703	Licence
7	4	Vignobles Prestige Malagasy	Fianarantsoa	2	Œnologue ho an'ny fitarihana ny fanaovana divay. CDD 8 volana ho an'ny vanim-potoana vendanges.	4	Œnologie	1	2025-09-17 09:24:34.37703	Licence
8	9	Distillerie Premium Spirits	Toamasina	1	Mpitantana famokarana ho an'ny fanaraha-maso ny rojo famokarana ny toaka haut de gamme.	4	Agroalimentaire	1	2025-09-10 09:24:34.37703	Licence
9	10	Wine & Spirits Academy Malagasy	Antananarivo	3	Sommelier torohevitra ho an'ny fampiofanana sy fampandrosoana vokatra. Stage 6 volana misy mety ho asa.	4	Sommellerie	1	2025-09-15 09:24:34.37703	Licence
10	6	Orinasa Malagasy Fanamboarana	Antananarivo	3	Azo atao ho an'ny rehetra	0	Agroalimentaire	3	2025-09-19 23:37:58.105465	BACC+2
\.

--
-- Set sequence values
--

SELECT pg_catalog.setval('public.diploma_id_seq', 18, true);
SELECT pg_catalog.setval('public.filiere_id_seq', 8, true);
SELECT pg_catalog.setval('public.contract_types_id_seq', 6, true);
SELECT pg_catalog.setval('public.sector_id_seq', 12, true);
SELECT pg_catalog.setval('public.post_id_seq', 10, true);
SELECT pg_catalog.setval('public.person_id_seq', 13, true);
SELECT pg_catalog.setval('public.offers_id_seq', 10, true);

--
-- Create indexes
--

CREATE INDEX idx_academical_diploma_id ON public.academical_qualification USING btree (diploma_id);
CREATE INDEX idx_academical_sector_id ON public.academical_qualification USING btree (sector_id);
CREATE INDEX idx_appliance_offer_id ON public.appliance USING btree (offer_id);
CREATE INDEX idx_appliance_person_id ON public.appliance USING btree (person_id);
CREATE INDEX idx_notification_person ON public.notification USING btree (person_id);

--
-- PostgreSQL database dump complete
--


-- Création de la table des questions
CREATE TABLE qcm_questions (
    id SERIAL PRIMARY KEY,
    texte TEXT NOT NULL,
    categorie VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table des réponses
CREATE TABLE qcm_reponses (
    id SERIAL PRIMARY KEY,
    texte TEXT NOT NULL,
    est_correcte BOOLEAN DEFAULT FALSE,
    question_id INTEGER NOT NULL REFERENCES qcm_questions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);