package com.itu.gest_emp.modules.personnel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.model.DemandeRuptureContrat;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import com.itu.gest_emp.modules.personnel.repository.DemandeRuptureRepository;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.PersonService;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;

import lombok.RequiredArgsConstructor;

// Service amélioré
@Service
@RequiredArgsConstructor
public class RuptureContratService {

    private final DemandeRuptureRepository demandeRepository;
    private final ContractsRhService contractsRhService;
    private final PreavisService preavisService;
    private final NotificationRhService notificationService;
    private final PersonnelRhService personnelRhService;
    private final UtilisateurService utilisateurService;
    private final PersonService personService;
    

    /**
     * Étape 1 : Créer une demande de rupture
     */
    @Transactional
    public DemandeRuptureContrat creerDemande(
            Long contratId,
            Long demandeurId,
            String motif,
            String justification,
            LocalDate dateEffetSouhaitee,
            boolean preavisEffectue) {

        ContractsRh contrat = contractsRhService.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        PersonnelRh demandeur = personnelRhService.findById(demandeurId)
                .orElseThrow(() -> new RuntimeException("Demandeur non trouvé"));

        // Vérifications
        if (!contrat.getStatut().equals("actif")) {
            throw new IllegalStateException("Le contrat n'est pas actif");
        }

        DemandeRuptureContrat demande = new DemandeRuptureContrat();
        demande.setContrat(contrat);
        demande.setDemandeur(demandeur);
        demande.setMotif(motif);
        demande.setJustification(justification);
        demande.setDateDemande(LocalDate.now());
        demande.setDateEffetSouhaitee(dateEffetSouhaitee);
        demande.setPreavisEffectue(preavisEffectue);
        demande.setStatut(StatutDemande.EN_ATTENTE);

        demande = demandeRepository.save(demande);

        // Notification aux RH
        // notificationService.notifierDemandeRupture(demande);

        return demande;
    }

    /**
     * Étape 2 : Valider la demande de rupture
     */
    @Transactional
    public void validerDemande(
            Long demandeId,
            Long validateurId,
            boolean accepte,
            String commentaire) {

        DemandeRuptureContrat demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        Utilisateur validateur = utilisateurService.findById(validateurId);

        demande.setValidateur(validateur);
        demande.setDateValidation(LocalDate.now());
        demande.setCommentaireValidation(commentaire);
        demande.setStatut(accepte ? StatutDemande.VALIDEE : StatutDemande.REFUSEE);

        demandeRepository.save(demande);

        if (accepte) {
            // Exécuter la rupture
            executerRupture(demande);
        }

        // Notifier le demandeur
        // notificationService.notifierReponseRupture(demande);
    }

    /**
     * Étape 3 : Exécuter la rupture validée
     */
    @Transactional
    private void executerRupture(DemandeRuptureContrat demande) {
        ContractsRh contrat = demande.getContrat();

        // Calculer l'indemnité si préavis non effectué
        if (!demande.isPreavisEffectue()) {
            BigDecimal indemnite = preavisService.genererIndemnitePreavis(
                    contrat,
                    demande.getMotif(),
                    contrat.getSalaireBase());

            // Notifier l'indemnité calculée
            // notificationService.notifierIndemnitePreavis(
            // contrat.getPersonnel(),
            // indemnite,
            // demande.getMotif()
            // );
        }

        // Terminer le contrat
        personnelRhService.terminerContrat(
                contrat.getPersonnel().getId(),
                demande.getMotif());
        
                if (contrat.getContractType().getCode().equals("CDD")) {
                    contrat.setDateFin(demande.getDateEffetSouhaitee());
                }
        contrat.setMotifFin(demande.getMotif());
        contractsRhService.update(contrat.getId(), contrat);

        // Notifications finales
        // notificationService.notifierRuptureEffective(contrat);
    }

    /**
     * Rupture immédiate (cas d'urgence - faute grave)
     */
 @Transactional
public void ruptureImmediatesFauteGrave(
        Long contratId,
        Long demandeurId,
        String justification) {
    
    try {
        // Validation des paramètres d'entrée
        if (contratId == null) {
            throw new IllegalArgumentException("L'ID du contrat ne peut pas être null");
        }
        if (demandeurId == null) {
            throw new IllegalArgumentException("L'ID du demandeur ne peut pas être null");
        }
        if (justification == null || justification.trim().isEmpty()) {
            throw new IllegalArgumentException("La justification ne peut pas être vide");
        }

        // Créer et valider automatiquement la demande
        DemandeRuptureContrat demande = creerDemande(
                contratId,
                demandeurId,
                "licenciement_faute_grave",
                justification,
                LocalDate.now(),
                true // Pas de préavis en cas de faute grave
        );

        // Validation automatique par le validateur par défaut (ID 13)
        Optional<Person> validateurOpt = personService.getPersonById(13L);
        
        if (validateurOpt.isPresent()) {
            demande.setValidateur(utilisateurService.findByPersonId(validateurOpt.get().getId()));
        } else {
            // Log et gestion du cas où le validateur n'existe pas

        }

        demande.setStatut(StatutDemande.VALIDEE);
        demande.setDateValidation(LocalDate.now());
        
        // Sauvegarde de la demande
        DemandeRuptureContrat demandeSauvegardee = demandeRepository.save(demande);
        
        // Exécution de la rupture
        executerRupture(demandeSauvegardee);
        
        
    } catch (Exception e) {

        throw new RuntimeException("Échec de la rupture pour faute grave", e);
    }
}
}