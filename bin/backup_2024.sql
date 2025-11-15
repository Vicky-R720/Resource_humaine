--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5 (Debian 17.5-1)
-- Dumped by pg_dump version 17.5 (Debian 17.5-1)

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: academical_qualification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.academical_qualification (
    id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    diploma_id bigint NOT NULL,
    sector_id bigint NOT NULL,
    appliance_id bigint NOT NULL
);


ALTER TABLE public.academical_qualification OWNER TO postgres;

--
-- Name: academical_qualification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.academical_qualification_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.academical_qualification_id_seq OWNER TO postgres;

--
-- Name: academical_qualification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.academical_qualification_id_seq OWNED BY public.academical_qualification.id;


--
-- Name: appliance; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.appliance (
    id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    offer_id bigint NOT NULL,
    cv_link text,
    skills text,
    person_id bigint NOT NULL,
    experiencelevel character varying(50) DEFAULT 4
);


ALTER TABLE public.appliance OWNER TO postgres;

--
-- Name: appliance_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.appliance_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.appliance_id_seq OWNER TO postgres;

--
-- Name: appliance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.appliance_id_seq OWNED BY public.appliance.id;


--
-- Name: contract_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.contract_types (
    id bigint NOT NULL,
    name character varying(100) NOT NULL
);


ALTER TABLE public.contract_types OWNER TO postgres;

--
-- Name: contract_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.contract_types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.contract_types_id_seq OWNER TO postgres;

--
-- Name: contract_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.contract_types_id_seq OWNED BY public.contract_types.id;


--
-- Name: diploma; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diploma (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.diploma OWNER TO postgres;

--
-- Name: diploma_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.diploma_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.diploma_id_seq OWNER TO postgres;

--
-- Name: diploma_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.diploma_id_seq OWNED BY public.diploma.id;


--
-- Name: evaluation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.evaluation (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.evaluation OWNER TO postgres;

--
-- Name: evaluation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.evaluation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.evaluation_id_seq OWNER TO postgres;

--
-- Name: evaluation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.evaluation_id_seq OWNED BY public.evaluation.id;


--
-- Name: offers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offers (
    id bigint NOT NULL,
    post_id bigint,
    company_name character varying(255) NOT NULL,
    location character varying(255) NOT NULL,
    contract_type_id bigint,
    required_profile text,
    experience_level character varying(100),
    diploma character varying(100),
    available_places integer DEFAULT 1,
    publication_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.offers OWNER TO postgres;

--
-- Name: offers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.offers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.offers_id_seq OWNER TO postgres;

--
-- Name: offers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.offers_id_seq OWNED BY public.offers.id;


--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    id bigint NOT NULL,
    nom character varying(100) NOT NULL,
    prenom character varying(100) NOT NULL,
    adresse text,
    naissance date,
    contact character varying(100)
);


ALTER TABLE public.person OWNER TO postgres;

--
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.person_id_seq OWNER TO postgres;

--
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.person_id_seq OWNED BY public.person.id;


--
-- Name: post; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.post (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    description text NOT NULL,
    missions text
);


ALTER TABLE public.post OWNER TO postgres;

--
-- Name: post_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.post_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.post_id_seq OWNER TO postgres;

--
-- Name: post_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.post_id_seq OWNED BY public.post.id;


--
-- Name: sector; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sector (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.sector OWNER TO postgres;

--
-- Name: sector_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sector_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sector_id_seq OWNER TO postgres;

--
-- Name: sector_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sector_id_seq OWNED BY public.sector.id;


--
-- Name: academical_qualification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academical_qualification ALTER COLUMN id SET DEFAULT nextval('public.academical_qualification_id_seq'::regclass);


--
-- Name: appliance id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appliance ALTER COLUMN id SET DEFAULT nextval('public.appliance_id_seq'::regclass);


--
-- Name: contract_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract_types ALTER COLUMN id SET DEFAULT nextval('public.contract_types_id_seq'::regclass);


--
-- Name: diploma id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diploma ALTER COLUMN id SET DEFAULT nextval('public.diploma_id_seq'::regclass);


--
-- Name: evaluation id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation ALTER COLUMN id SET DEFAULT nextval('public.evaluation_id_seq'::regclass);


--
-- Name: offers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers ALTER COLUMN id SET DEFAULT nextval('public.offers_id_seq'::regclass);


--
-- Name: person id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person ALTER COLUMN id SET DEFAULT nextval('public.person_id_seq'::regclass);


--
-- Name: post id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.post ALTER COLUMN id SET DEFAULT nextval('public.post_id_seq'::regclass);


--
-- Name: sector id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sector ALTER COLUMN id SET DEFAULT nextval('public.sector_id_seq'::regclass);


--
-- Data for Name: academical_qualification; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.academical_qualification (id, created_at, diploma_id, sector_id, appliance_id) FROM stdin;
1	2025-09-18 09:24:34.399214	2	1	1
2	2025-09-18 09:24:34.399214	2	7	2
3	2025-09-18 09:24:34.431172	11	1	3
4	2025-09-18 09:24:34.4436	9	7	4
5	2025-09-18 09:24:34.4436	9	7	5
6	2025-09-18 09:24:34.454625	7	6	6
7	2025-09-18 09:24:34.465739	12	3	7
8	2025-09-18 09:24:34.465739	12	6	7
9	2025-09-18 09:24:34.476831	8	5	8
10	2025-09-18 09:24:34.476831	8	4	8
11	2025-09-18 09:24:34.48786	3	12	9
12	2025-09-18 09:24:34.498992	11	9	10
13	2025-09-18 09:24:34.510053	13	9	11
14	2025-09-18 09:24:34.510053	13	2	11
15	2025-09-18 09:24:34.521166	8	5	12
16	2025-09-18 09:24:34.532175	10	10	13
17	2025-09-18 09:24:34.543208	10	10	14
18	2025-09-18 09:24:34.543208	10	6	14
\.


--
-- Data for Name: appliance; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.appliance (id, created_at, offer_id, cv_link, skills, person_id, experiencelevel) FROM stdin;
1	2025-09-18 09:24:34.388241	1	https://cv-storage.com/jean_dubois_cv.pdf	Fermentation alcoolique, Contrôle qualité, Gestion production, Normes HACCP, Conduite équipements industriels	1	4
2	2025-09-18 09:24:34.388241	1	https://cv-storage.com/pierre_leroy_cv.pdf	Distillation, Vieillissement spiritueux, Assemblage, Dégustation, Gestion stocks	3	4
3	2025-09-18 09:24:34.388241	2	https://cv-storage.com/sophie_martin_cv.pdf	Brassage, Maltage, Analyse microbiologique, ISO 22000, Maintenance préventive	2	4
4	2025-09-18 09:24:34.388241	2	https://cv-storage.com/jean_dubois_cv.pdf	Fermentation bière, Houblonnage, Contrôle température, Nettoyage équipements	1	4
5	2025-09-18 09:24:34.388241	3	https://cv-storage.com/pierre_leroy_cv.pdf	Assemblage vins, Méthode champenoise, Riddage, Dégorgement, Dégustation experte	3	4
6	2025-09-18 09:24:34.388241	4	https://cv-storage.com/marie_bernard_cv.pdf	Contrôle qualité, Analyses physicochimiques, Microbiologie alimentaire, Audit qualité, Certification BIO	4	4
7	2025-09-18 09:24:34.388241	4	https://cv-storage.com/antoine_petit_cv.pdf	Chromatographie, Spectroscopie, Validation méthodes, Métrologie, Laboratoire accrédité	5	4
8	2025-09-18 09:24:34.388241	5	https://cv-storage.com/camille_robert_cv.pdf	Négociation commerciale, Gestion grands comptes, Marketing produit, Export international, Anglais courant	6	4
9	2025-09-18 09:24:34.388241	5	https://cv-storage.com/thomas_richard_cv.pdf	Développement export, Réglementation internationale, Logistique, Espagnol natif, Prospection	7	4
10	2025-09-18 09:24:34.388241	6	https://cv-storage.com/david_simon_cv.pdf	Développement produits, Innovation, Biotechnologies, Fermentation, Brevets	9	4
11	2025-09-18 09:24:34.388241	6	https://cv-storage.com/emma_laurent_cv.pdf	Recherche appliquée, Biochimie, Enzymologie, Publications scientifiques, Projets collaboratifs	10	4
12	2025-09-18 09:24:34.388241	2	https://cv-storage.com/julie_moreau_cv.pdf	Marketing brasserie, Communication produit, Événements bière, Réseaux sociaux	8	4
13	2025-09-18 09:24:34.388241	3	https://cv-storage.com/alexandre_michel_cv.pdf	Management cave, Gestion équipes, Optimisation production, Lean management	11	4
14	2025-09-18 09:24:34.388241	4	https://cv-storage.com/lea_garcia_cv.pdf	Direction qualité, Audit certifications, Gestion laboratoire, Formation équipes	12	4
\.


--
-- Data for Name: contract_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.contract_types (id, name) FROM stdin;
1	CDI
2	CDD
3	Stage
4	Alternance
5	Intérim
6	Freelance
\.


--
-- Data for Name: diploma; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.diploma (id, name) FROM stdin;
1	Baccalauréat
2	BTS Agroalimentaire
3	BTS Commerce International
4	DUT Génie Biologique
5	Licence Biologie
6	Licence Commerce
7	Master Agroalimentaire
8	Master Marketing
9	Master Génie des Procédés
10	Master Management
11	Ingénieur Agroalimentaire
12	Ingénieur Chimie
13	Doctorat Biochimie
\.


--
-- Data for Name: evaluation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.evaluation (id, name) FROM stdin;
1	Excellent
2	Très Bien
3	Bien
4	Assez Bien
5	Passable
\.


--
-- Data for Name: offers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offers (id, post_id, company_name, location, contract_type_id, required_profile, experience_level, diploma, available_places, publication_date, created_at, updated_at) FROM stdin;
1	1	Distillerie Artisanale du Périgord	Bergerac	1	Maître distillateur expérimenté pour production de cognac et armagnac. Connaissance des techniques de fermentation et distillation traditionnelles.	4	BTS Agroalimentaire	1	2025-09-13 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
2	2	Brasserie des Alpes	Annecy	1	Brasseur pour production de bières artisanales. Maîtrise du maltage, brassage et fermentation. Créativité pour développer nouvelles recettes.	4	CAP Brasserie	2	2025-09-15 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
3	3	Champagne Dubois & Fils	Épernay	1	Chef de cave pour vinification et assemblage de champagne. Expertise en méthode champenoise et gestion des stocks de vieillissement.	4	BTS Viticulture-Œnologie	1	2025-09-11 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
4	5	Laboratoire Qualité AlcoBev	Lyon	1	Technicien qualité pour analyses physicochimiques et microbiologiques des boissons alcoolisées. Maîtrise des normes ISO et HACCP.	4	DUT Génie Biologique	3	2025-09-16 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
5	7	Rhum Tropical Export	Fort-de-France	1	Responsable export pour commercialisation rhums premium sur marchés internationaux. Anglais et espagnol requis.	4	Master Commerce International	1	2025-09-14 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
6	6	Innovation Spirits Lab	Toulouse	1	Ingénieur R&D pour développement nouvelles boissons alcoolisées. Biotechnologies et fermentation innovante.	4	Ingénieur Agroalimentaire	2	2025-09-12 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
7	4	Vignobles Prestige	Bordeaux	2	Œnologue pour encadrer les vinifications et assemblages. CDD de 8 mois pour période des vendanges et vinification.	4	Master Œnologie	1	2025-09-17 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
8	9	Distillerie Premium Spirits	Cognac	1	Responsable production pour superviser l'ensemble de la chaîne de fabrication des spiritueux haut de gamme.	4	Ingénieur Agroalimentaire	1	2025-09-10 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
9	10	Wine & Spirits Academy	Reims	3	Sommelier conseil pour formation et développement produits. Stage de 6 mois avec possibilité d'embauche.	4	BTS Sommellerie	1	2025-09-15 09:24:34.37703	2025-09-18 09:24:34.37703	2025-09-18 09:24:34.37703
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.person (id, nom, prenom, adresse, naissance, contact) FROM stdin;
1	Dubois	Jean	15 rue des Vignerons, 21000 Dijon	1985-03-15	jean.dubois@email.com
2	Martin	Sophie	28 avenue de la Bière, 67000 Strasbourg	1990-07-22	sophie.martin@email.com
3	Leroy	Pierre	42 chemin des Distilleries, 16100 Cognac	1982-11-08	pierre.leroy@email.com
4	Bernard	Marie	33 rue des Laboratoires, 69000 Lyon	1988-04-12	marie.bernard@email.com
5	Petit	Antoine	19 boulevard des Analyses, 75015 Paris	1992-09-03	antoine.petit@email.com
6	Robert	Camille	8 place du Commerce, 33000 Bordeaux	1987-06-18	camille.robert@email.com
7	Richard	Thomas	52 rue Export, 13000 Marseille	1984-12-25	thomas.richard@email.com
8	Moreau	Julie	14 avenue Marketing, 44000 Nantes	1991-01-30	julie.moreau@email.com
9	Simon	David	7 rue Innovation, 31000 Toulouse	1986-08-14	david.simon@email.com
10	Laurent	Emma	25 chemin Recherche, 38000 Grenoble	1989-05-07	emma.laurent@email.com
11	Michel	Alexandre	11 rue Direction, 59000 Lille	1980-02-28	alexandre.michel@email.com
12	Garcia	Léa	36 avenue Gestion, 35000 Rennes	1983-10-15	lea.garcia@email.com
\.


--
-- Data for Name: post; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.post (id, name, description, missions) FROM stdin;
1	Maître Distillateur	Responsable de la production et du contrôle qualité des spiritueux. Expertise en distillation traditionnelle et moderne.	\N
2	Brasseur	Spécialiste de la fabrication de bières. Maîtrise des techniques de maltage, brassage et fermentation.	\N
3	Chef de Cave	Expert en vinification et assemblage. Responsable du vieillissement et de la qualité des vins et champagnes.	\N
4	Œnologue	Professionnel du vin, de la vinification à la commercialisation. Expertise en analyse sensorielle.	\N
5	Technicien Qualité	Responsable des analyses et du contrôle qualité. Maîtrise des normes alimentaires et certifications.	\N
6	Ingénieur R&D	Développement de nouveaux produits et amélioration des procédés. Innovation en biotechnologies.	\N
7	Responsable Export	Commercialisation internationale des produits. Expertise réglementaire et négociation.	\N
8	Directeur Commercial	Stratégie commerciale et développement des ventes. Management des équipes commerciales.	\N
9	Responsable Production	Gestion de la chaîne de production. Optimisation des rendements et de la qualité.	\N
10	Sommelier Conseil	Expert en dégustation et conseil produits. Formation des équipes commerciales.	\N
\.


--
-- Data for Name: sector; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sector (id, name) FROM stdin;
1	Agroalimentaire
2	Biotechnologie
3	Chimie
4	Commerce
5	Marketing
6	Qualité
7	Production
8	Logistique
9	Recherche et Développement
10	Management
11	Vente
12	Export
\.


--
-- Name: academical_qualification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.academical_qualification_id_seq', 18, true);


--
-- Name: appliance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.appliance_id_seq', 14, true);


--
-- Name: contract_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.contract_types_id_seq', 6, true);


--
-- Name: diploma_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.diploma_id_seq', 13, true);


--
-- Name: evaluation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.evaluation_id_seq', 5, true);


--
-- Name: offers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.offers_id_seq', 9, true);


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.person_id_seq', 12, true);


--
-- Name: post_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.post_id_seq', 10, true);


--
-- Name: sector_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sector_id_seq', 12, true);


--
-- Name: academical_qualification academical_qualification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academical_qualification
    ADD CONSTRAINT academical_qualification_pkey PRIMARY KEY (id);


--
-- Name: appliance appliance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appliance
    ADD CONSTRAINT appliance_pkey PRIMARY KEY (id);


--
-- Name: contract_types contract_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.contract_types
    ADD CONSTRAINT contract_types_pkey PRIMARY KEY (id);


--
-- Name: diploma diploma_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diploma
    ADD CONSTRAINT diploma_pkey PRIMARY KEY (id);


--
-- Name: evaluation evaluation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.evaluation
    ADD CONSTRAINT evaluation_pkey PRIMARY KEY (id);


--
-- Name: offers offers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT offers_pkey PRIMARY KEY (id);


--
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: post post_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.post
    ADD CONSTRAINT post_pkey PRIMARY KEY (id);


--
-- Name: sector sector_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_pkey PRIMARY KEY (id);


--
-- Name: idx_academical_appliance_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_academical_appliance_id ON public.academical_qualification USING btree (appliance_id);


--
-- Name: idx_academical_diploma_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_academical_diploma_id ON public.academical_qualification USING btree (diploma_id);


--
-- Name: idx_academical_sector_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_academical_sector_id ON public.academical_qualification USING btree (sector_id);


--
-- Name: idx_appliance_offer_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_appliance_offer_id ON public.appliance USING btree (offer_id);


--
-- Name: idx_appliance_person_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_appliance_person_id ON public.appliance USING btree (person_id);


--
-- Name: offers fk1x8s5yug2j9j1l38s5o24iguw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT fk1x8s5yug2j9j1l38s5o24iguw FOREIGN KEY (contract_type_id) REFERENCES public.contract_types(id);


--
-- Name: offers fk9fcryvxp4ig92ukalvlj91prh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT fk9fcryvxp4ig92ukalvlj91prh FOREIGN KEY (post_id) REFERENCES public.post(id);


--
-- Name: academical_qualification fk_academical_appliance; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academical_qualification
    ADD CONSTRAINT fk_academical_appliance FOREIGN KEY (appliance_id) REFERENCES public.appliance(id) ON DELETE CASCADE;


--
-- Name: academical_qualification fk_academical_diploma; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academical_qualification
    ADD CONSTRAINT fk_academical_diploma FOREIGN KEY (diploma_id) REFERENCES public.diploma(id) ON DELETE CASCADE;


--
-- Name: academical_qualification fk_academical_sector; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academical_qualification
    ADD CONSTRAINT fk_academical_sector FOREIGN KEY (sector_id) REFERENCES public.sector(id) ON DELETE CASCADE;


--
-- Name: appliance fk_appliance_offer; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appliance
    ADD CONSTRAINT fk_appliance_offer FOREIGN KEY (offer_id) REFERENCES public.offers(id) ON DELETE CASCADE;


--
-- Name: appliance fk_appliance_person; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.appliance
    ADD CONSTRAINT fk_appliance_person FOREIGN KEY (person_id) REFERENCES public.person(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

