-- ================================
-- DONNÉES DE TEST - ENTREPRISE DE FABRICATION DE BOISSONS ALCOOLISÉES
-- ================================

-- Insertion des types de contrats
INSERT INTO contract_types (name) VALUES 
('CDI'),
('CDD'),
('Stage'),
('Alternance'),
('Intérim'),
('Freelance');

-- Insertion des postes
INSERT INTO post (name, description) VALUES 
('Maître Distillateur', 'Responsable de la production et du contrôle qualité des spiritueux. Expertise en distillation traditionnelle et moderne.'),
('Brasseur', 'Spécialiste de la fabrication de bières. Maîtrise des techniques de maltage, brassage et fermentation.'),
('Chef de Cave', 'Expert en vinification et assemblage. Responsable du vieillissement et de la qualité des vins et champagnes.'),
('Œnologue', 'Professionnel du vin, de la vinification à la commercialisation. Expertise en analyse sensorielle.'),
('Technicien Qualité', 'Responsable des analyses et du contrôle qualité. Maîtrise des normes alimentaires et certifications.'),
('Ingénieur R&D', 'Développement de nouveaux produits et amélioration des procédés. Innovation en biotechnologies.'),
('Responsable Export', 'Commercialisation internationale des produits. Expertise réglementaire et négociation.'),
('Directeur Commercial', 'Stratégie commerciale et développement des ventes. Management des équipes commerciales.'),
('Responsable Production', 'Gestion de la chaîne de production. Optimisation des rendements et de la qualité.'),
('Sommelier Conseil', 'Expert en dégustation et conseil produits. Formation des équipes commerciales.');

-- Insertion des diplômes
INSERT INTO diploma (name) VALUES 
('Baccalauréat'),
('BTS Agroalimentaire'),
('BTS Commerce International'),
('DUT Génie Biologique'),
('Licence Biologie'),
('Licence Commerce'),
('Master Agroalimentaire'),
('Master Marketing'),
('Master Génie des Procédés'),
('Master Management'),
('Ingénieur Agroalimentaire'),
('Ingénieur Chimie'),
('Doctorat Biochimie');

-- Insertion des secteurs
INSERT INTO sector (name) VALUES 
('Agroalimentaire'),
('Biotechnologie'),
('Chimie'),
('Commerce'),
('Marketing'),
('Qualité'),
('Production'),
('Logistique'),
('Recherche et Développement'),
('Management'),
('Vente'),
('Export');

-- Insertion des évaluations
INSERT INTO evaluation (name) VALUES 
('Excellent'),
('Très Bien'),
('Bien'),
('Assez Bien'),
('Passable');

-- Insertion des personnes (candidats pour entreprise de boissons alcoolisées)
INSERT INTO person (nom, prenom, adresse, naissance, contact) VALUES 
-- Profils pour production/technique
('Dubois', 'Jean', '15 rue des Vignerons, 21000 Dijon', '1985-03-15', 'jean.dubois@email.com'),
('Martin', 'Sophie', '28 avenue de la Bière, 67000 Strasbourg', '1990-07-22', 'sophie.martin@email.com'),
('Leroy', 'Pierre', '42 chemin des Distilleries, 16100 Cognac', '1982-11-08', 'pierre.leroy@email.com'),

-- Profils pour qualité/contrôle
('Bernard', 'Marie', '33 rue des Laboratoires, 69000 Lyon', '1988-04-12', 'marie.bernard@email.com'),
('Petit', 'Antoine', '19 boulevard des Analyses, 75015 Paris', '1992-09-03', 'antoine.petit@email.com'),

-- Profils commerciaux/marketing
('Robert', 'Camille', '8 place du Commerce, 33000 Bordeaux', '1987-06-18', 'camille.robert@email.com'),
('Richard', 'Thomas', '52 rue Export, 13000 Marseille', '1984-12-25', 'thomas.richard@email.com'),
('Moreau', 'Julie', '14 avenue Marketing, 44000 Nantes', '1991-01-30', 'julie.moreau@email.com'),

-- Profils R&D
('Simon', 'David', '7 rue Innovation, 31000 Toulouse', '1986-08-14', 'david.simon@email.com'),
('Laurent', 'Emma', '25 chemin Recherche, 38000 Grenoble', '1989-05-07', 'emma.laurent@email.com'),

-- Profils management
('Michel', 'Alexandre', '11 rue Direction, 59000 Lille', '1980-02-28', 'alexandre.michel@email.com'),
('Garcia', 'Léa', '36 avenue Gestion, 35000 Rennes', '1983-10-15', 'lea.garcia@email.com');

-- Insertion des offres d'emploi pour entreprise de boissons alcoolisées
INSERT INTO offers (post_id, company_name, location, contract_type_id, required_profile, experience_level, diploma, available_places, publication_date) VALUES 
(1, 'Distillerie Artisanale du Périgord', 'Bergerac', 1, 'Maître distillateur expérimenté pour production de cognac et armagnac. Connaissance des techniques de fermentation et distillation traditionnelles.', '5-10 ans', 'BTS Agroalimentaire', 1, CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 'Brasserie des Alpes', 'Annecy', 1, 'Brasseur pour production de bières artisanales. Maîtrise du maltage, brassage et fermentation. Créativité pour développer nouvelles recettes.', '3-5 ans', 'CAP Brasserie', 2, CURRENT_TIMESTAMP - INTERVAL '3 days'),
(3, 'Champagne Dubois & Fils', 'Épernay', 1, 'Chef de cave pour vinification et assemblage de champagne. Expertise en méthode champenoise et gestion des stocks de vieillissement.', '7-15 ans', 'BTS Viticulture-Œnologie', 1, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(5, 'Laboratoire Qualité AlcoBev', 'Lyon', 1, 'Technicien qualité pour analyses physicochimiques et microbiologiques des boissons alcoolisées. Maîtrise des normes ISO et HACCP.', '2-5 ans', 'DUT Génie Biologique', 3, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(7, 'Rhum Tropical Export', 'Fort-de-France', 1, 'Responsable export pour commercialisation rhums premium sur marchés internationaux. Anglais et espagnol requis.', '5-8 ans', 'Master Commerce International', 1, CURRENT_TIMESTAMP - INTERVAL '4 days'),
(6, 'Innovation Spirits Lab', 'Toulouse', 1, 'Ingénieur R&D pour développement nouvelles boissons alcoolisées. Biotechnologies et fermentation innovante.', '3-7 ans', 'Ingénieur Agroalimentaire', 2, CURRENT_TIMESTAMP - INTERVAL '6 days'),
(4, 'Vignobles Prestige', 'Bordeaux', 2, 'Œnologue pour encadrer les vinifications et assemblages. CDD de 8 mois pour période des vendanges et vinification.', '3-8 ans', 'Master Œnologie', 1, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(9, 'Distillerie Premium Spirits', 'Cognac', 1, 'Responsable production pour superviser l''ensemble de la chaîne de fabrication des spiritueux haut de gamme.', '8-15 ans', 'Ingénieur Agroalimentaire', 1, CURRENT_TIMESTAMP - INTERVAL '8 days'),
(10, 'Wine & Spirits Academy', 'Reims', 3, 'Sommelier conseil pour formation et développement produits. Stage de 6 mois avec possibilité d''embauche.', '2-5 ans', 'BTS Sommellerie', 1, CURRENT_TIMESTAMP - INTERVAL '3 days');

-- Insertion des candidatures pour les offres
-- Candidatures pour Maître distillateur (offer_id = 1)
INSERT INTO appliance (offer_id, cv_link, skills, person_id) VALUES 
(1, 'https://cv-storage.com/jean_dubois_cv.pdf', 'Fermentation alcoolique, Contrôle qualité, Gestion production, Normes HACCP, Conduite équipements industriels', 1),
(1, 'https://cv-storage.com/pierre_leroy_cv.pdf', 'Distillation, Vieillissement spiritueux, Assemblage, Dégustation, Gestion stocks', 3),

-- Candidatures pour Brasseur (offer_id = 2)
(2, 'https://cv-storage.com/sophie_martin_cv.pdf', 'Brassage, Maltage, Analyse microbiologique, ISO 22000, Maintenance préventive', 2),
(2, 'https://cv-storage.com/jean_dubois_cv.pdf', 'Fermentation bière, Houblonnage, Contrôle température, Nettoyage équipements', 1),

-- Candidatures pour Chef de cave Champagne (offer_id = 3)
(3, 'https://cv-storage.com/pierre_leroy_cv.pdf', 'Assemblage vins, Méthode champenoise, Riddage, Dégorgement, Dégustation experte', 3),

-- Candidatures pour Technicien qualité (offer_id = 4)  
(4, 'https://cv-storage.com/marie_bernard_cv.pdf', 'Contrôle qualité, Analyses physicochimiques, Microbiologie alimentaire, Audit qualité, Certification BIO', 4),
(4, 'https://cv-storage.com/antoine_petit_cv.pdf', 'Chromatographie, Spectroscopie, Validation méthodes, Métrologie, Laboratoire accrédité', 5),

-- Candidatures pour Responsable export (offer_id = 5)
(5, 'https://cv-storage.com/camille_robert_cv.pdf', 'Négociation commerciale, Gestion grands comptes, Marketing produit, Export international, Anglais courant', 6),
(5, 'https://cv-storage.com/thomas_richard_cv.pdf', 'Développement export, Réglementation internationale, Logistique, Espagnol natif, Prospection', 7),

-- Candidatures pour Ingénieur R&D (offer_id = 6)
(6, 'https://cv-storage.com/david_simon_cv.pdf', 'Développement produits, Innovation, Biotechnologies, Fermentation, Brevets', 9),
(6, 'https://cv-storage.com/emma_laurent_cv.pdf', 'Recherche appliquée, Biochimie, Enzymologie, Publications scientifiques, Projets collaboratifs', 10),

-- Candidatures supplémentaires variées
(2, 'https://cv-storage.com/julie_moreau_cv.pdf', 'Marketing brasserie, Communication produit, Événements bière, Réseaux sociaux', 8),
(3, 'https://cv-storage.com/alexandre_michel_cv.pdf', 'Management cave, Gestion équipes, Optimisation production, Lean management', 11),
(4, 'https://cv-storage.com/lea_garcia_cv.pdf', 'Direction qualité, Audit certifications, Gestion laboratoire, Formation équipes', 12);

-- Insertion des qualifications académiques (mise à jour des appliance_id)
-- Jean Dubois - BTS Agroalimentaire (appliance_id = 1 et 2)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(2, 1, 1),  -- Candidature distillateur
(2, 7, 2);  -- Candidature brasseur

-- Sophie Martin - Ingénieur Agroalimentaire (appliance_id = 3)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(11, 1, 3);  -- Candidature brasseur

-- Pierre Leroy - Master Génie des Procédés (appliance_id = 4 et 5)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(9, 7, 4),   -- Candidature distillateur  
(9, 7, 5);   -- Candidature chef de cave

-- Marie Bernard - Master Agroalimentaire + Qualité (appliance_id = 6)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(7, 6, 6);   -- Candidature technicien qualité

-- Antoine Petit - Ingénieur Chimie + Qualité (appliance_id = 7)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(12, 3, 7),  -- Chimie
(12, 6, 7);  -- Qualité

-- Camille Robert - Master Marketing + Commerce (appliance_id = 8)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(8, 5, 8),   -- Marketing
(8, 4, 8);   -- Commerce

-- Thomas Richard - BTS Commerce International + Export (appliance_id = 9)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(3, 12, 9);  -- Export

-- David Simon - Ingénieur Agroalimentaire + R&D (appliance_id = 10)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(11, 9, 10); -- R&D

-- Emma Laurent - Doctorat Biochimie + R&D (appliance_id = 11)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(13, 9, 11), -- R&D
(13, 2, 11); -- Biotechnologie

-- Julie Moreau - Master Marketing (appliance_id = 12)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(8, 5, 12);  -- Marketing

-- Alexandre Michel - Master Management (appliance_id = 13)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(10, 10, 13); -- Management

-- Léa Garcia - Master Management + Commerce (appliance_id = 14)
INSERT INTO academical_qualification (diploma_id, sector_id, appliance_id) VALUES 
(10, 10, 14), -- Management
(10, 6, 14);  -- Qualité