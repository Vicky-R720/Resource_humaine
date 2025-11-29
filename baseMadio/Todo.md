# LISTE DES TÂCHES - SYSTÈME RH ORINASA MALAGASY

## RÉSUMÉ DU PROJET
- **Total tables anciennes conservées**: 11 tables (diploma, filiere, contract_types, sector, post, person, offers, appliance, academical_qualification, notification, qcm_questions, qcm_reponses)
- **Total nouvelles tables RH**: 21 tables (toutes avec suffix _rh)
- **Tables principales utilisées par RH**: person, post, notification, personnel_rh

---

## 1. FONCTIONNALITÉS ESSENTIELLES (BASE RH)

### A. GESTION DU PERSONNEL
**Tables concernées**: `personnel_rh`, `contracts_rh`, `career_history_rh`, `documents_rh`, `person`, `post`

- [ ] **Fiche employé complète**
  - [ ] Créer interface de saisie fiche employé (identité, contact, photo)
  - [ ] Intégration avec table `person` (récupération nom, prénom, adresse, naissance, contact)
  - [ ] Ajout informations RH (CIN, situation familiale, urgence, banque, CNAPS, OSTIE)
  - [ ] Liaison avec table `post` pour affectation poste
  - [ ] Upload et affichage photo de profil (pdp)

- [ ] **Suivi contrat de travail**
  - [ ] Formulaire création contrat (CDI, CDD, Stage, Essai, Freelance)
  - [ ] Calcul automatique date fin essai
  - [ ] Workflow validation période essai
  - [ ] Alerte fin de contrat CDD (utiliser table `notification`)
  - [ ] Historique des contrats par employé
  - [ ] Gestion renouvellement contrat

- [ ] **Historique postes, promotions, mobilités**
  - [ ] Interface enregistrement mouvement carrière
  - [ ] Tracking des changements de poste (table `career_history_rh` + `post`)
  - [ ] Historique évolution salariale
  - [ ] Rapport mobilité interne

- [ ] **Gestion documents RH**
  - [ ] Upload documents (CIN, diplômes, attestations, contrats, certificats médicaux)
  - [ ] Stockage sécurisé fichiers (file_path)
  - [ ] Système de vérification documents (verified_by depuis `person`)
  - [ ] Alerte expiration documents (date_expiration)
  - [ ] Archivage et consultation documents

---

### B. GESTION DES ABSENCES ET CONGÉS
**Tables concernées**: `leave_types_rh`, `leave_balance_rh`, `leave_requests_rh`, `personnel_rh`, `person`, `notification`

- [ ] **Suivi soldes de congés**
  - [ ] Dashboard affichage soldes (payés, maladie, exceptionnels)
  - [ ] Calcul automatique solde_restant (solde_initial + solde_acquis - solde_pris)
  - [ ] Génération soldes annuels automatique (1er janvier)
  - [ ] Report soldes non pris année N vers N+1
  - [ ] Historique consommation congés par employé

- [ ] **Workflow demande et validation**
  - [ ] Formulaire demande congé employé (sélection type, dates, motif)
  - [ ] Upload justificatif si nécessaire (requires_justification)
  - [ ] Calcul automatique nombre_jours (jours ouvrés)
  - [ ] Notification automatique manager (table `notification`)
  - [ ] Interface validation manager (validated_by depuis `person`)
  - [ ] Notification retour employé (approuvé/refusé)
  - [ ] Mise à jour automatique soldes après validation

- [ ] **Intégration calendrier entreprise**
  - [ ] Calendrier visuel congés équipe
  - [ ] Affichage congés validés par couleur (color dans `leave_types_rh`)
  - [ ] Détection chevauchements congés même service
  - [ ] Export calendrier (iCal, PDF)

- [ ] **Alertes automatiques**
  - [ ] Alerte congés non validés >48h (table `notification`)
  - [ ] Alerte absences répétées (>3 en 1 mois)
  - [ ] Alerte solde congés négatif
  - [ ] Alerte congés non pris fin année

---

### C. GESTION DU TEMPS ET PRÉSENCES
**Tables concernées**: `attendance_rh`, `overtime_rh`, `personnel_rh`, `person`, `notification`

- [ ] **Feuille de temps / Pointage**
  - [ ] Interface pointage manuel (arrivée, départ, pauses)
  - [ ] Intégration badgeuse (import CSV type_pointage='badgeuse')
  - [ ] Application mobile pointage (type_pointage='mobile')
  - [ ] Calcul automatique durée_travail_minutes
  - [ ] Détection retards automatique
  - [ ] Gestion pointage exceptionnel (oubli badge)

- [ ] **Heures supplémentaires, retards, pauses**
  - [ ] Enregistrement heures supplémentaires
  - [ ] Calcul majoration selon type (jour_normal 1.3x, dimanche 1.5x, férié 2x, nuit 1.5x)
  - [ ] Calcul automatique montant_hs
  - [ ] Workflow validation HS manager (validated_by)
  - [ ] Statistiques retards par employé
  - [ ] Tracking durée pauses

- [ ] **Génération relevé présence**
  - [ ] Rapport mensuel par employé
  - [ ] Synthèse présences/absences/retards
  - [ ] Export Excel/PDF
  - [ ] Statistiques présence par service

- [ ] **Interface intégration paie**
  - [ ] Export données présence vers paie (format compatible `payslips_rh`)
  - [ ] Inclusion heures supplémentaires dans calcul paie
  - [ ] Déduction absences non justifiées
  - [ ] API/Web service pour SIRH externe

---

### D. GESTION DE LA PAIE (BASE)
**Tables concernées**: `payslips_rh`, `salary_parameters_rh`, `salary_components_rh`, `overtime_rh`, `personnel_rh`, `person`

- [ ] **Fiches de paie mensuelles**
  - [ ] Formulaire génération bulletins par mois/année
  - [ ] Calcul salaire_base depuis `contracts_rh`
  - [ ] Calcul total_brut (base + primes + indemnités + HS)
  - [ ] Calcul total_retenues (CNAPS + OSTIE + IRSA + avances)
  - [ ] Calcul net_a_payer (brut - retenues)
  - [ ] Génération PDF bulletin (pdf_path)
  - [ ] Historique bulletins par employé

- [ ] **Paramétrage taux CNAPS, OSTIE, IRSA**
  - [ ] Interface gestion paramètres (table `salary_parameters_rh`)
  - [ ] CNAPS: 1% employé, 13% employeur
  - [ ] OSTIE: 1% employé, 5% employeur
  - [ ] IRSA: calcul progressif par tranches (0%, 5%, 10%, 15%, 20%)
  - [ ] Versionning paramètres (date_debut_validite, date_fin_validite)
  - [ ] Historique modifications taux

- [ ] **Gestion primes, avances, HS, absences**
  - [ ] Gestion primes récurrentes (ancienneté, transport, allocation familiale)
  - [ ] Gestion primes ponctuelles (performance, 13ème mois)
  - [ ] Enregistrement avances sur salaire
  - [ ] Récupération automatique HS depuis `overtime_rh`
  - [ ] Déduction absences non payées
  - [ ] Interface saisie éléments variables

- [ ] **Export format comptable ou PDF**
  - [ ] Export comptable (CSV, Excel)
  - [ ] Génération PDF bulletins individuels
  - [ ] Génération PDF journal de paie global
  - [ ] Envoi automatique bulletins par email

---

## 2. FONCTIONNALITÉS AVANCÉES (RH NUMÉRIQUES / INTELLIGENTES)

### A. TABLEAUX DE BORD ET INDICATEURS RH
**Tables concernées**: `personnel_rh`, `attendance_rh`, `leave_requests_rh`, `contracts_rh`, `performance_evaluations_rh`, `person`, `post`

- [ ] **Statistiques effectifs**
  - [ ] Répartition par genre (sexe)
  - [ ] Pyramide des âges (naissance)
  - [ ] Répartition par service (`post_id`)
  - [ ] Répartition par type contrat (`contracts_rh`)
  - [ ] Graphiques interactifs (Chart.js, Plotly)

- [ ] **Taux turnover, absentéisme, ancienneté**
  - [ ] Calcul turnover: (départs / effectif moyen) * 100
  - [ ] Calcul absentéisme: (jours absence / jours travaillés) * 100
  - [ ] Ancienneté moyenne (date_embauche)
  - [ ] Évolution indicateurs sur 12 mois

- [ ] **Alertes**
  - [ ] Alerte fin contrat <30j (table `notification`)
  - [ ] Alerte congés non pris >20j
  - [ ] Alerte dépassement budget formation
  - [ ] Dashboard alertes centralisé

- [ ] **Visualisation graphique**
  - [ ] KPI cards (effectif, turnover, absentéisme)
  - [ ] Graphiques évolution (lignes, barres)
  - [ ] Cartes de chaleur présences
  - [ ] Export rapports PDF

---

### B. GESTION DES PERFORMANCES
**Tables concernées**: `performance_evaluations_rh`, `personnel_rh`, `person`

- [ ] **Évaluations périodiques automatisées**
  - [ ] Formulaire évaluation (mensuel, trimestriel, semestriel, annuel)
  - [ ] Scoring multi-critères (qualité, productivité, assiduité, équipe, initiative)
  - [ ] Calcul score_global pondéré
  - [ ] Campagnes évaluation automatiques (rappels)

- [ ] **Génération rapports performance**
  - [ ] Rapport individuel employé
  - [ ] Rapport comparatif équipe
  - [ ] Graphiques évolution performance
  - [ ] Identification top performers / low performers
  - [ ] Export PDF rapports

---

### C. GESTION DES COMPÉTENCES
**Tables concernées**: `competences_rh`, `competences_mapping_rh`, `formations_rh`, `personnel_rh`, `post`, `person`

- [ ] **Cartographie compétences entreprise**
  - [ ] Référentiel compétences (technique, soft_skills, management, langue)
  - [ ] Matrice compétences par poste
  - [ ] Cartographie compétences disponibles
  - [ ] Identification gaps de compétences

- [ ] **Matching automatique profil / poste**
  - [ ] Algorithme scoring compatibilité
  - [ ] Comparaison compétences employé vs poste requis
  - [ ] Suggestion candidats internes pour poste
  - [ ] Recommandation mobilité interne

- [ ] **Suggestion formations selon écarts**
  - [ ] Détection écarts compétences (requis vs actuel)
  - [ ] Recommandation formations ciblées
  - [ ] Priorisation formations selon écart
  - [ ] Plan développement individuel automatique

---

### D. SELF-SERVICE EMPLOYÉ (ESPACE EMPLOYÉ)
**Tables concernées**: `employee_requests_rh`, `payslips_rh`, `leave_balance_rh`, `personnel_rh`, `person`, `notification`

- [ ] **Mise à jour personnelle informations**
  - [ ] Interface modification contact, adresse
  - [ ] Upload documents personnels
  - [ ] Mise à jour personne urgence
  - [ ] Validation RH modifications sensibles

- [ ] **Consultation bulletins, soldes**
  - [ ] Affichage bulletins paie (pdf_path)
  - [ ] Download PDF bulletins
  - [ ] Affichage soldes congés temps réel
  - [ ] Historique congés pris

- [ ] **Soumission demandes**
  - [ ] Demande attestation travail
  - [ ] Demande attestation salaire
  - [ ] Demande remboursement frais
  - [ ] Demande avance sur salaire
  - [ ] Tracking statut demandes

- [ ] **Système messagerie RH**
  - [ ] Envoi messages vers RH
  - [ ] Réception notifications (table `notification`)
  - [ ] Historique échanges
  - [ ] Pièces jointes

---

### E. PORTAIL MANAGER
**Tables concernées**: `manager_dashboard_rh`, `leave_requests_rh`, `overtime_rh`, `performance_evaluations_rh`, `attendance_rh`, `person`, `notification`

- [ ] **Validation demandes équipe**
  - [ ] Liste demandes congés en attente (validated_by)
  - [ ] Validation/refus heures supplémentaires
  - [ ] Validation documents
  - [ ] Approbation avances
  - [ ] Historique décisions

- [ ] **Suivi performances et absences**
  - [ ] Dashboard équipe
  - [ ] Alertes membres équipe (retards, absences)
  - [ ] Suivi évaluations à réaliser
  - [ ] Rapport synthèse équipe

- [ ] **Tableaux de bord spécifiques service**
  - [ ] KPI service (présence, performance, congés)
  - [ ] Comparaison inter-services
  - [ ] Planification effectifs
  - [ ] Budget RH service

---

### F. AUTOMATISATION ET INTELLIGENCE ARTIFICIELLE
**Tables concernées**: `chatbot_conversations_rh`, `documents_templates_rh`, `performance_predictions_rh`, `anomalies_detection_rh`, `personnel_rh`, `person`

- [ ] **Chatbot RH questions fréquentes**
  - [ ] Intégration API Anthropic Claude (conges, paie, RH)
  - [ ] Base connaissances FAQ
  - [ ] Historique conversations (table `chatbot_conversations_rh`)
  - [ ] Scoring satisfaction (satisfaction_score)
  - [ ] Escalade vers RH humain

- [ ] **Génération automatique documents**
  - [ ] Templates personnalisables (table `documents_templates_rh`)
  - [ ] Remplacement variables {{nom}}, {{matricule}}, etc.
  - [ ] Génération contrats (CDI, CDD, Stage)
  - [ ] Génération attestations (travail, salaire)
  - [ ] Signature électronique documents

- [ ] **Prédiction turnover**
  - [ ] Modèle ML analyse historique
  - [ ] Facteurs risque (absences, performance, ancienneté)
  - [ ] Score prédiction départ (table `performance_predictions_rh`)
  - [ ] Actions préventives recommandées
  - [ ] Dashboard risque turnover

- [ ] **Détection anomalies heures/paie**
  - [ ] Détection heures excessives (table `anomalies_detection_rh`)
  - [ ] Détection patterns absences suspectes
  - [ ] Détection écarts salaire inhabituels
  - [ ] Alertes fraude potentielle
  - [ ] Workflow investigation anomalies

- [ ] **Recommandation candidats (matching CV ↔ poste)**
  - [ ] Parser CV automatique (compétences, expérience)
  - [ ] Scoring compatibilité CV-poste
  - [ ] Ranking candidats
  - [ ] Suggestions questions entretien
  - [ ] Prédiction succès recrutement

---

### G. CONFORMITÉ ET AUDIT
**Tables concernées**: `audit_log_rh`, `person`

- [ ] **Journalisation actions (traces audit)**
  - [ ] Log toutes actions (CREATE, UPDATE, DELETE, VIEW, EXPORT)
  - [ ] Capture old_values et new_values
  - [ ] Enregistrement IP et user_agent
  - [ ] Traçabilité complète (user_id, timestamp)
  - [ ] Recherche logs par utilisateur/table/date

- [ ] **Gestion autorisations par rôle**
  - [ ] Rôles: employee, manager, rh, admin (role dans `person`)
  - [ ] Matrice permissions par rôle
  - [ ] Contrôle accès données sensibles
  - [ ] Audit permissions accordées
  - [ ] Révision périodique accès

- [ ] **Sauvegarde et archivage légal**
  - [ ] Backup automatique base données
  - [ ] Archivage documents légaux (bulletins paie 5 ans, contrats 10 ans)
  - [ ] Export données pour contrôle inspection travail
  - [ ] Conformité RGPD (droit oubli, portabilité)
  - [ ] Politique rétention données

---

## RÉCAPITULATIF TECHNIQUE

### TABLES ANCIENNES CONSERVÉES (11)
1. `diploma` - Diplômes
2. `filiere` - Filières académiques
3. `contract_types` - Types contrats
4. `sector` - Secteurs activité
5. `post` - **Postes (utilisé par RH)**
6. `person` - **Utilisateurs système (utilisé par RH)**
7. `offers` - Offres d'emploi
8. `appliance` - Candidatures
9. `academical_qualification` - Qualifications
10. `notification` - **Notifications (utilisé par RH)**
11. `qcm_questions` + `qcm_reponses` - QCM recrutement

### NOUVELLES TABLES RH (21)
**Gestion Personnel:**
1. `personnel_rh` - Fiches employés complètes
2. `contracts_rh` - Contrats de travail
3. `career_history_rh` - Historique carrière
4. `documents_rh` - Documents RH

**Congés & Absences:**
5. `leave_types_rh` - Types de congés
6. `leave_balance_rh` - Soldes congés
7. `leave_requests_rh` - Demandes congés

**Temps & Présences:**
8. `attendance_rh` - Pointages
9. `overtime_rh` - Heures supplémentaires

**Paie:**
10. `salary_parameters_rh` - Paramètres paie
11. `salary_components_rh` - Composantes salaire
12. `payslips_rh` - Bulletins paie

**Compétences & Formations:**
13. `competences_rh` - Référentiel compétences
14. `competences_mapping_rh` - Compétences employés
15. `formations_rh` - Formations

**Performance:**
16. `performance_evaluations_rh` - Évaluations

**Self-Service:**
17. `employee_requests_rh` - Demandes employés

**Portail Manager:**
18. `manager_dashboard_rh` - Dashboard managers

**Automatisation & IA:**
19. `documents_templates_rh` - Templates documents
20. `chatbot_conversations_rh` - Historique chatbot
21. `anomalies_detection_rh` - Détection anomalies
22. `performance_predictions_rh` - Prédictions IA

**Conformité:**
23. `audit_log_rh` - Logs audit

---

## PRIORITÉS DÉVELOPPEMENT

### PHASE 1 - MVP (3 mois)
- Gestion personnel (fiches, contrats)
- Gestion congés (demandes, validations, soldes)
- Pointage basique
- Bulletins paie manuels

### PHASE 2 - Automatisation (3 mois)
- Calcul paie automatique
- Tableaux de bord RH
- Self-service employé
- Portail manager

### PHASE 3 - Intelligence (3 mois)
- Chatbot RH
- Génération documents automatique
- Détection anomalies
- Prédictions IA

### PHASE 4 - Conformité (2 mois)
- Audit complet
- Gestion permissions avancée
- Archivage légal
- Conformité RGPD

---

**TOTAL TÂCHES: ~120 tâches**
**DURÉE ESTIMÉE: 11 mois**
**ÉQUIPE RECOMMANDÉE: 3-4 développeurs + 1 chef de projet**