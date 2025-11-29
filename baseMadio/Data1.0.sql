-- ============================================================
-- ORDRE D'INSERTION CORRECT (respectant les dependances)
-- ============================================================

-- 1. Tables sans dependances
INSERT INTO public.diploma (id, name, niveau) VALUES
(1, 'Baccalaureat', 1),
(2, 'Licence Agroalimentaire', 4),
(3, 'Master Brasserie', 7),
(4, 'Licence Œnologie', 4),
(5, 'Master Distillation', 7),
(6, 'BTS Biotechnologie', 3),
(7, 'Licence Chimie', 4),
(8, 'Master Management', 7),
(9, 'BTS Commerce', 3),
(10, 'Licence Marketing', 4),
(11, 'Master Qualite', 7),
(12, 'Licence Biochimie', 4),
(13, 'Master Recherche', 7),
(14, 'BTS Production', 3),
(15, 'Licence Logistique', 4),
(16, 'Master Export', 7),
(17, 'Licence Sommellerie', 4),
(18, 'BTS Viticulture', 3);

INSERT INTO public.filiere (id, name, niveau) VALUES
(1, 'BACC', 1),
(2, 'BACC+1', 2),
(3, 'BACC+2', 3),
(4, 'Licence', 4),
(5, 'BACC+4', 5),
(6, 'BACC+5', 6),
(7, 'Master', 7),
(8, 'Doctorat', 8);

INSERT INTO public.contract_types 
(code, libelle, requires_end_date, duree_max_mois, has_trial_period, duree_essai_max_mois, is_active, description) 
VALUES
('CDI', 'Contrat à Duree Indeterminee', FALSE, NULL, TRUE, 6, TRUE, 
 'Contrat sans limitation de duree. La periode d''essai maximale est de 6 mois pour les cadres.'),

('CDD', 'Contrat à Duree Determinee', TRUE, 18, TRUE, 3, TRUE, 
 'Contrat avec date de fin obligatoire. Duree maximale de 18 mois renouvellements inclus. Periode d''essai maximale de 3 mois.'),

('STAGE', 'Convention de Stage', TRUE, 6, FALSE, NULL, TRUE, 
 'Convention de stage pour etudiant. Duree maximale de 6 mois. Pas de periode d''essai.'),

('INTERIM', 'Contrat d''Interim', TRUE, 18, FALSE, NULL, TRUE, 
 'Mission d''interim. Duree maximale de 18 mois pour un même poste. Pas de periode d''essai.'),

('APPRENTISSAGE', 'Contrat d''Apprentissage', TRUE, 36, TRUE, 2, TRUE, 
 'Contrat en alternance associe à une formation qualifiante. Duree de 6 mois à 3 ans selon le diplôme.'),

('PROFESSIONNALISATION', 'Contrat de Professionnalisation', TRUE, 24, TRUE, 2, TRUE, 
 'Contrat en alternance visant l''acquisition d''une qualification professionnelle. Duree de 6 à 24 mois.'),

('SAISONNIER', 'Contrat Saisonnier', TRUE, 8, FALSE, NULL, TRUE, 
 'Contrat pour emploi à caractère saisonnier. Duree maximale de 8 mois par an.'),

('CDD_REMPLACEMENT', 'CDD de Remplacement', TRUE, NULL, TRUE, 1, TRUE, 
 'CDD conclu pour remplacer un salarie absent. Duree variable selon l''absence à couvrir.');

 
INSERT INTO public.sector (id, name) VALUES
(1, 'Agroalimentaire (Sakafo sy zava-pisotro)'),
(2, 'Biotechnologie'),
(3, 'Chimie'),
(4, 'Commerce (Varotra)'),
(5, 'Marketing'),
(6, 'Qualite (Kalitao)'),
(7, 'Production (Famokarana)'),
(8, 'Logistique'),
(9, 'Recherche et Developpement (Fikarohana sy fampandrosoana)'),
(10, 'Management (Fitantanana)'),
(11, 'Vente (Fivarotana)'),
(12, 'Export (Fanondranana)');

-- 2. Categorie personnel (sans dependances)
INSERT INTO public.categorie_personnel (id, nom, niveau) VALUES
(1, 'Ouvrier', 1),
(2, 'Employe', 2),
(3, 'TAM (Technicien Agent de Maîtrise)', 3),
(4, 'Cadre', 4),
(5, 'Dirigeant', 5);

-- 3. Post (depend de categorie_personnel)

-- 4. Person (sans dependances)
INSERT INTO public.person (id, nom, prenom, adresse, naissance, contact) VALUES
(1, 'Rakoto', 'Jean', '15 lalana ny mpamboly, Antananarivo', '1985-03-15', 'jean.rakoto@mail.mg'),
(2, 'Rasoa', 'Sophie', '28 arabe ny labiera, Fianarantsoa', '1990-07-22', 'sophie.rasoa@mail.mg'),
(3, 'Randria', 'Pierre', '42 lalana ny distillerie, Toamasina', '1982-11-08', 'pierre.randria@mail.mg'),
(4, 'Razafy', 'Marie', '33 lalana ny laboratoara, Antsirabe', '1988-04-12', 'marie.razafy@mail.mg'),
(5, 'Andriamalala', 'Antoine', '19 boulevard des Analyses, Toliara', '1992-09-03', 'antoine.andriamalala@mail.mg'),
(6, 'Rajaona', 'Camille', '8 toerana varotra, Mahajanga', '1987-06-18', 'camille.rajaona@mail.mg'),
(7, 'Randrianarisoa', 'Thomas', '52 lalana export, Nosy Be', '1984-12-25', 'thomas.randrianarisoa@mail.mg'),
(8, 'Ramanantsoa', 'Julie', '14 arabe marketing, Antsiranana', '1991-01-30', 'julie.ramanantsoa@mail.mg'),
(9, 'Ravelonarivo', 'David', '7 lalana innovation, Moramanga', '1986-08-14', 'david.ravelonarivo@mail.mg'),
(10, 'Rasolofoniaina', 'Emma', '25 lalana fikarohana, Ambatolampy', '1989-05-07', 'emma.rasolofoniaina@mail.mg'),
(11, 'Randriamanantena', 'Alexandre', '11 lalana fitantanana, Ambositra', '1980-02-28', 'alexandre.randriamanantena@mail.mg'),
(12, 'Razakanirina', 'Lea', '36 arabe fitantanana, Manakara', '1983-10-15', 'lea.razakanirina@mail.mg'),
(13, 'Vonjiniaina', 'Ralevazaha', 'LOT 0910C61, Antananarivo', '1995-09-05', 'ralevazaha@mail.mg'),
(14, 'Rakotondrabe', 'Hery', '23 lalana Andohalo, Antananarivo', '1993-06-12', 'hery.rakotondrabe@mail.mg'),
(15, 'Ramiaramanana', 'Fara', '67 lalana Analakely, Antananarivo', '1994-11-20', 'fara.ramiaramanana@mail.mg');

-- 5. Utilisateur (depend de person)
INSERT INTO public.utilisateur (id, person_id, email, mot_de_passe, role,actif) VALUES
(1, 13, 'admin@alcomalagasy.mg', '$2a$10$hashedpassword1', 'ADMIN',TRUE),
(2, 1, 'jean.rakoto@mail.mg', '$2a$10$hashedpassword2', 'MANAGER',TRUE),
(3, 2, 'sophie.rasoa@mail.mg', '$2a$10$hashedpassword3', 'EMPLOYE',TRUE),
(4, 3, 'pierre.randria@mail.mg', '$2a$10$hashedpassword4', 'MANAGER',TRUE),
(5, 4, 'marie.razafy@mail.mg', '$2a$10$hashedpassword5', 'EMPLOYE',TRUE),
(6, 11, 'alexandre.randriamanantena@mail.mg', '$2a$10$hashedpassword6', 'RH',TRUE);

-- 6. Service (depend de utilisateur)
INSERT INTO public.service (id, nom, manager_id) VALUES
(1, 'Production', 2),
(2, 'Qualite', 4),
(3, 'Commercial', 6),
(4, 'Recherche & Developpement', NULL),
(5, 'Logistique', NULL),
(6, 'Ressources Humaines', 6);

INSERT INTO equipe (id, nom, service_id, manager_id) VALUES
-- Service Production
(1, 'Equipe Production Toaka', 1, 2),
(2, 'Equipe Production Labiera', 1, 2),
(3, 'Equipe Production Generale', 1, 2),

-- Service Qualite
(4, 'Equipe Controle Qualite', 2, 4),

-- Service Commercial
(5, 'Equipe Export', 3, 6),
(6, 'Equipe Vente Nationale', 3, 6),
(7, 'Equipe Sommellerie', 3, 6),

-- Service Recherche & Developpement
(8, 'Equipe R&D', 4, NULL);


INSERT INTO public.post (id, name, description, missions, categorie_id) VALUES
(1, 'Mpamokatra toaka', 'Tompon''andraikitra amin''ny famokarana sy fanaraha-maso ny kalitaon''ny toaka. Mahay ny fomba fanaovana toaka tranainy sy maoderina.', 'Famokarana toaka, fanaraha-maso fermentation, distillation, fanendasana', 3),
(2, 'Mpamokatra labiera', 'Manam-pahaizana manokana amin''ny famokarana labiera artisanale. Mahay ny fomba fanaovana maltage, brassage ary fermentation.', 'Famoronana recettes vaovao, fanaraha-maso ny fizotran''ny famokarana', 3),
(3, 'Mpitantana cave', 'Manam-pahaizana momba ny fanaovana divay sy champagne. Tompon''andraikitra amin''ny fitehirizana sy ny kalitao.', 'Fanaraha-maso ny fahanasan''ny divay, fananganana champagne', 4),
(4, 'Œnologue (Mpanandro divay)', 'Matihanina momba ny divay, hatramin''ny fanaovana divay ka hatramin''ny fivarotana azy. Mahay ny fitsapana sensorielle.', 'Fanasafiana divay, torohevitra momba ny fanaovana divay', 4),
(5, 'Teknisiana kalitao', 'Tompon''andraikitra amin''ny fanaraha-maso sy fitsapana ara-pitsaboana ny zava-pisotro misy alikaola. Mahay ny normes ISO sy HACCP.', 'Fanatontosiana kalitao, fanaraha-maso laboratoara', 3),
(6, 'Injeniera R&D', 'Fampandrosoana vokatra vaovao sy fanatsarana ny fomba famokarana. Innovation amin''ny biotechnologies.', 'Fikarohana, fampandrosoana vokatra vaovao', 4),
(7, 'Mpitantana export', 'Fivarotana iraisam-pirenena ny vokatra. Fahaizana momba ny lalàna iraisam-pirenena sy fifampiraharahana.', 'Fikarohana tsena iraisam-pirenena, fifampiraharahana', 4),
(8, 'Mpitantana varotra', 'Paikady varotra sy fampandrosoana ny fivarotana. Fitantanana ny ekipa varotra.', 'Famolavola paikady varotra, fitantanana mpivarotra', 4),
(9, 'Mpitantana famokarana', 'Fitantanana ny rojo famokarana. Fanatsarana ny fahombiazana sy ny kalitao.', 'Fanaraha-maso ny famokarana, optimisation', 4),
(10, 'Sommelier (Mpanolo-tsakafo)', 'Manam-pahaizana amin''ny fitsapana divay sy torohevitra momba ny vokatra. Fampiofanana ny ekipa varotra.', 'Fanasafiana divay, torohevitra amin''ny mpanjifa', 3);


UPDATE public.post SET equipe_id = 1 WHERE id = 1;
UPDATE public.post SET equipe_id = 2 WHERE id = 2;
UPDATE public.post SET equipe_id = 3 WHERE id = 9;
UPDATE public.post SET equipe_id = 4 WHERE id IN (4, 5);
UPDATE public.post SET equipe_id = 5 WHERE id = 7;
UPDATE public.post SET equipe_id = 6 WHERE id = 8;
UPDATE public.post SET equipe_id = 7 WHERE id = 10;
UPDATE public.post SET equipe_id = 8 WHERE id = 6;


-- 7. Offers (depend de post et contract_types)
INSERT INTO public.offers (id, post_id, company_name, location, contract_type_id, required_profile, experience_level, diploma, available_places, publication_date, filiere) VALUES
(1, 1, 'Distillerie Artisanale Malagasy', 'Antananarivo', 1, 'Mpamokatra toaka efa za-draharaha. Fahalalana momba ny fomba fanaovana toaka nentim-paharazana.', '4', 'Agroalimentaire', 1, '2025-09-13 09:24:34.37703', 'Licence'),
(2, 2, 'Brasserie des Hauts-Plateaux', 'Antsirabe', 1, 'Mpamokatra labiera ho an''ny famokarana labiera artisanale. Mahay ny fomba fanaovana maltage, brassage ary fermentation.', '4', 'Brasserie', 2, '2025-09-15 09:24:34.37703', 'Licence'),
(3, 3, 'Champagne Malagasy', 'Fianarantsoa', 1, 'Mpitantana cave ho an''ny fanaovana divay sy fananganana champagne. Mahay ny fomba fanaovana champagne.', '4', 'Viticulture-Œnologie', 1, '2025-09-11 09:24:34.37703', 'Licence'),
(4, 5, 'Laboratoire Qualite AlcoMalagasy', 'Toamasina', 1, 'Teknisiana kalitao ho an''ny fanaraha-maso sy fitsapana ny zava-pisotro misy alikaola. Mahay ny normes ISO sy HACCP.', '4', 'Genie Biologique', 3, '2025-09-16 09:24:34.37703', 'Licence'),
(5, 7, 'Rhum Tropical Export', 'Nosy Be', 1, 'Mpitantana export ho an''ny fivarotana rhums premium any ivelany. Ilaina ny teny anglisy sy espaniola.', '4', 'Commerce International', 1, '2025-09-14 09:24:34.37703', 'Licence'),
(6, 6, 'Innovation Spirits Lab', 'Antananarivo', 1, 'Injeniera R&D ho an''ny fampandrosoana zava-pisotro misy alikaola vaovao. Biotechnologies sy fermentation innovante.', '4', 'Agroalimentaire', 2, '2025-09-12 09:24:34.37703', 'Licence'),
(7, 4, 'Vignobles Prestige Malagasy', 'Fianarantsoa', 2, 'Œnologue ho an''ny fitarihana ny fanaovana divay. CDD 8 volana ho an''ny vanim-potoana vendanges.', '4', 'Œnologie', 1, '2025-09-17 09:24:34.37703', 'Licence'),
(8, 9, 'Distillerie Premium Spirits', 'Toamasina', 1, 'Mpitantana famokarana ho an''ny fanaraha-maso ny rojo famokarana ny toaka haut de gamme.', '4', 'Agroalimentaire', 1, '2025-09-10 09:24:34.37703', 'Licence'),
(9, 10, 'Wine & Spirits Academy Malagasy', 'Antananarivo', 3, 'Sommelier torohevitra ho an''ny fampiofanana sy fampandrosoana vokatra. Stage 6 volana misy mety ho asa.', '4', 'Sommellerie', 1, '2025-09-15 09:24:34.37703', 'Licence'),
(10, 6, 'Orinasa Malagasy Fanamboarana', 'Antananarivo', 3, 'Azo atao ho an''ny rehetra', '0', 'Agroalimentaire', 3, '2025-09-19 23:37:58.105465', 'BACC+2');

-- 8. Appliance (depend de offers et person)
INSERT INTO public.appliance (id, created_at, offer_id, cv_link, skills, person_id, experiencelevel, traitement) VALUES
(1, '2025-09-20 10:30:00', 1, 'https://cv.storage/rakoto_jean.pdf', 'Distillation, fermentation, contrôle qualite', 1, '4', 'en_cours'),
(2, '2025-09-21 14:15:00', 2, 'https://cv.storage/rasoa_sophie.pdf', 'Brassage, maltage, creation de recettes', 2, '4', 'accepte'),
(3, '2025-09-22 09:00:00', 3, 'https://cv.storage/randria_pierre.pdf', 'Œnologie, gestion de cave, champagne', 3, '4', 'en_cours'),
(4, '2025-09-23 11:45:00', 4, 'https://cv.storage/razafy_marie.pdf', 'ISO 9001, HACCP, analyse laboratoire', 4, '4', 'accepte'),
(5, '2025-09-24 16:20:00', 5, 'https://cv.storage/andriamalala_antoine.pdf', 'Export, commerce international, anglais, espagnol', 5, '4', 'refuse'),
(6, '2025-09-25 08:30:00', 6, 'https://cv.storage/rajaona_camille.pdf', 'R&D, biotechnologie, innovation produits', 6, '4', 'en_cours'),
(7, '2025-09-26 13:00:00', 7, 'https://cv.storage/randrianarisoa_thomas.pdf', 'Œnologie, analyse sensorielle, viticulture', 7, '4', 'en_cours'),
(8, '2025-09-27 10:15:00', 1, 'https://cv.storage/rakotondrabe_hery.pdf', 'Production alcools, distillation traditionnelle', 14, '2', 'en_cours'),
(9, '2025-09-28 15:45:00', 2, 'https://cv.storage/ramiaramanana_fara.pdf', 'Brasserie artisanale, fermentation', 15, '3', 'refuse'),
(10, '2025-09-29 09:30:00', 10, 'https://cv.storage/ravelonarivo_david.pdf', 'Chimie, biotechnologie, recherche', 9, '0', 'accepte');

-- 9. Academic Qualification (depend de diploma, sector, appliance, filiere)
INSERT INTO public.academical_qualification (id, created_at, diploma_id, sector_id, appliance_id, filiere_id) VALUES
(1, '2025-09-20 10:30:00', 2, 1, 1, 4),
(2, '2025-09-21 14:15:00', 3, 1, 2, 7),
(3, '2025-09-22 09:00:00', 4, 1, 3, 4),
(4, '2025-09-23 11:45:00', 11, 6, 4, 7),
(5, '2025-09-24 16:20:00', 16, 12, 5, 7),
(6, '2025-09-25 08:30:00', 13, 9, 6, 7),
(7, '2025-09-26 13:00:00', 4, 1, 7, 4),
(8, '2025-09-27 10:15:00', 2, 1, 8, 4),
(9, '2025-09-28 15:45:00', 6, 2, 9, 3),
(10, '2025-09-29 09:30:00', 7, 3, 10, 4);

-- 10. Notification (depend de person)
INSERT INTO public.notification (id, person_id, message, created_at) VALUES
(1, 1, 'Votre candidature pour le poste de Mpamokatra toaka a ete reçue', '2025-09-20 10:35:00'),
(2, 2, 'Felicitations! Votre candidature pour Mpamokatra labiera a ete acceptee', '2025-09-22 09:00:00'),
(3, 3, 'Votre candidature est en cours d''examen', '2025-09-22 09:05:00'),
(4, 4, 'Felicitations! Vous êtes selectionne(e) pour le poste de Teknisiana kalitao', '2025-09-24 10:00:00'),
(5, 5, 'Nous regrettons de vous informer que votre candidature n''a pas ete retenue', '2025-09-26 14:00:00'),
(6, 6, 'Votre dossier est actuellement en phase de selection', '2025-09-25 08:35:00'),
(7, 14, 'Nouvelle offre correspondant à votre profil disponible', '2025-09-27 08:00:00'),
(8, 15, 'Votre candidature a ete examinee. Resultat à venir prochainement', '2025-09-28 16:00:00'),
(9, 9, 'Bienvenue! Votre candidature pour le stage a ete acceptee', '2025-09-30 09:00:00'),
(10, 1, 'Rappel: Entretien prevu le 5 octobre 2025 à 14h00', '2025-10-01 10:00:00');

-- 11. QCM Questions
INSERT INTO public.qcm_questions (id, texte, categorie, created_at) VALUES
(1, 'Quelle est la temperature ideale de fermentation pour une bière ale?', 'Brasserie', '2025-09-01 10:00:00'),
(2, 'Quel est le taux d''alcool minimum pour un spiritueux?', 'Distillation', '2025-09-01 10:05:00'),
(3, 'Quelle norme internationale s''applique au management de la qualite?', 'Qualite', '2025-09-01 10:10:00'),
(4, 'Qu''est-ce que le processus de maltage?', 'Brasserie', '2025-09-01 10:15:00'),
(5, 'Combien de temps dure generalement la fermentation alcoolique du vin?', 'Œnologie', '2025-09-01 10:20:00'),
(6, 'Quel est le principe de la distillation fractionnee?', 'Distillation', '2025-09-01 10:25:00'),
(7, 'Que signifie HACCP?', 'Qualite', '2025-09-01 10:30:00'),
(8, 'Quelle levure est utilisee pour la production de champagne?', 'Œnologie', '2025-09-01 10:35:00'),
(9, 'Quel est le pH optimal pour la fermentation du rhum?', 'Distillation', '2025-09-01 10:40:00'),
(10, 'Combien de temps doit vieillir un cognac VSOP minimum?', 'Distillation', '2025-09-01 10:45:00');

-- 12. QCM Reponses (depend de qcm_questions)
INSERT INTO public.qcm_reponses (id, texte, est_correcte, question_id, created_at) VALUES
-- Reponses pour Question 1
(1, '15-20°C', true, 1, '2025-09-01 10:00:00'),
(2, '5-10°C', false, 1, '2025-09-01 10:00:00'),
(3, '25-30°C', false, 1, '2025-09-01 10:00:00'),
(4, '0-5°C', false, 1, '2025-09-01 10:00:00'),

-- Reponses pour Question 2
(5, '15%', false, 2, '2025-09-01 10:05:00'),
(6, '37.5%', true, 2, '2025-09-01 10:05:00'),
(7, '20%', false, 2, '2025-09-01 10:05:00'),
(8, '50%', false, 2, '2025-09-01 10:05:00'),

-- Reponses pour Question 3
(9, 'ISO 9001', true, 3, '2025-09-01 10:10:00'),
(10, 'ISO 14001', false, 3, '2025-09-01 10:10:00'),
(11, 'ISO 27001', false, 3, '2025-09-01 10:10:00'),
(12, 'ISO 45001', false, 3, '2025-09-01 10:10:00'),

-- Reponses pour Question 4
(13, 'Germination contrôlee de l''orge', true, 4, '2025-09-01 10:15:00'),
(14, 'Fermentation du moût', false, 4, '2025-09-01 10:15:00'),
(15, 'Distillation de l''alcool', false, 4, '2025-09-01 10:15:00'),
(16, 'Filtration de la bière', false, 4, '2025-09-01 10:15:00'),

-- Reponses pour Question 5
(17, '1-2 semaines', true, 5, '2025-09-01 10:20:00'),
(18, '3-6 mois', false, 5, '2025-09-01 10:20:00'),
(19, '1-2 jours', false, 5, '2025-09-01 10:20:00'),
(20, '1 an', false, 5, '2025-09-01 10:20:00'),

-- Reponses pour Question 6
(21, 'Separation basee sur les differents points d''ebullition', true, 6, '2025-09-01 10:25:00'),
(22, 'Filtration à travers plusieurs membranes', false, 6, '2025-09-01 10:25:00'),
(23, 'Congelation selective', false, 6, '2025-09-01 10:25:00'),
(24, 'evaporation sous vide', false, 6, '2025-09-01 10:25:00'),

-- Reponses pour Question 7
(25, 'Hazard Analysis Critical Control Point', true, 7, '2025-09-01 10:30:00'),
(26, 'Health and Care Control Program', false, 7, '2025-09-01 10:30:00'),
(27, 'Hygiene Assessment and Critical Control', false, 7, '2025-09-01 10:30:00'),
(28, 'High Analysis Chemical Control Point', false, 7, '2025-09-01 10:30:00'),

-- Reponses pour Question 8
(29, 'Saccharomyces cerevisiae', true, 8, '2025-09-01 10:35:00'),
(30, 'Candida albicans', false, 8, '2025-09-01 10:35:00'),
(31, 'Aspergillus niger', false, 8, '2025-09-01 10:35:00'),
(32, 'Penicillium roqueforti', false, 8, '2025-09-01 10:35:00'),

-- Reponses pour Question 9
(33, '4.5-5.5', true, 9, '2025-09-01 10:40:00'),
(34, '2.0-3.0', false, 9, '2025-09-01 10:40:00'),
(35, '7.0-8.0', false, 9, '2025-09-01 10:40:00'),
(36, '9.0-10.0', false, 9, '2025-09-01 10:40:00'),

-- Reponses pour Question 10
(37, '4 ans', true, 10, '2025-09-01 10:45:00'),
(38, '2 ans', false, 10, '2025-09-01 10:45:00'),
(39, '10 ans', false, 10, '2025-09-01 10:45:00'),
(40, '6 mois', false, 10, '2025-09-01 10:45:00');

-- ============================================================
-- RESET DES SEQUENCES
-- ============================================================

SELECT pg_catalog.setval('public.diploma_id_seq', 18, true);
SELECT pg_catalog.setval('public.filiere_id_seq', 8, true);
SELECT pg_catalog.setval('public.contract_types_id_seq', 6, true);
SELECT pg_catalog.setval('public.sector_id_seq', 12, true);
SELECT pg_catalog.setval('public.categorie_personnel_id_seq', 5, true);
SELECT pg_catalog.setval('public.post_id_seq', 10, true);
SELECT pg_catalog.setval('public.person_id_seq', 15, true);
SELECT pg_catalog.setval('public.utilisateur_id_seq', 6, true);
SELECT pg_catalog.setval('public.service_id_seq', 6, true);
SELECT pg_catalog.setval('public.offers_id_seq', 10, true);
SELECT pg_catalog.setval('public.appliance_id_seq', 10, true);
SELECT pg_catalog.setval('public.academical_qualification_id_seq', 10, true);
SELECT pg_catalog.setval('public.notification_id_seq', 10, true);
SELECT pg_catalog.setval('public.qcm_questions_id_seq', 10, true);
SELECT pg_catalog.setval('public.qcm_reponses_id_seq', 40, true);