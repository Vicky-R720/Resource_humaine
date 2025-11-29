package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Avance;
import com.itu.gest_emp.modules.paie.model.AvanceAction;
import com.itu.gest_emp.modules.paie.model.AvanceStatus;
import com.itu.gest_emp.modules.paie.repository.AvanceActionRepository;
import com.itu.gest_emp.modules.paie.repository.AvanceRepository;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;
import com.itu.gest_emp.modules.shared.model.Utilisateur;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvanceService {

    private final AvanceRepository avanceRepository;
    private final AvanceActionRepository avanceActionRepository;
    private final PersonnelRhService personnelRhService;
    private final UtilisateurService utilisateurService;
    private final NotificationRhService notificationRhService;

    @Transactional
    public Avance createDemande(Long personnelId, BigDecimal montant, String motif) {
        Avance a = new Avance();
        a.setPersonnel(personnelRhService.findById(personnelId)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable : " + personnelId)));
        a.setMontant(montant);
        a.setMotif(motif);
        a.setDateDemande(LocalDateTime.now());
        a.setStatut(AvanceStatus.DEMANDEE);

        Avance saved = avanceRepository.save(a);

        Utilisateur requester = utilisateurService.findById(saved.getPersonnel().getPerson().getId());
        if (requester == null) {
            requester = utilisateurService.findByPersonId(saved.getPersonnel().getPerson().getId());
        }

        AvanceAction action = new AvanceAction(saved, null, AvanceStatus.DEMANDEE, requester, "Demande créée");
        avanceActionRepository.save(action);

        // notification au demandeur
        if (requester != null) {
            notificationRhService.createNotification(requester, requester, "Demande d'avance créée", "Votre demande d'avance de " + montant + " a été enregistrée.", "AVANCE", "Avance", saved.getId());
        }

        // notification aux RH (tous les utilisateurs avec role 'RH')
        final Utilisateur finalRequester = requester;
        utilisateurService.getAllUtilisateurs().stream()
                .filter(u -> "RH".equalsIgnoreCase(u.getRole()))
                .forEach(u -> notificationRhService.createNotification(u, finalRequester, "Nouvelle demande d'avance", "Nouvelle demande d'avance de " + montant + " par " + saved.getPersonnel().getPerson().getFullName(), "AVANCE", "Avance", saved.getId()));

        return saved;
    }

    @Transactional
    public Avance approve(Long avanceId, Long approbateurUtilisateurId, String commentaire) {
        Avance a = avanceRepository.findById(avanceId).orElseThrow(() -> new IllegalArgumentException("Avance introuvable : " + avanceId));
        a.setStatut(AvanceStatus.APPROUVEE);
        a.setDateDecision(LocalDateTime.now());
        Utilisateur approbateur = utilisateurService.findById(approbateurUtilisateurId);
        if (approbateur == null) {
            throw new IllegalArgumentException("Approbateur introuvable : " + approbateurUtilisateurId);
        }
        a.setApprobateur(approbateur);
        Avance saved = avanceRepository.save(a);
        Utilisateur actor = approbateur;
        Utilisateur requester = utilisateurService.findByPersonId(saved.getPersonnel().getPerson().getId());

        AvanceAction action = new AvanceAction(saved, AvanceStatus.DEMANDEE, AvanceStatus.APPROUVEE, actor, commentaire);
        avanceActionRepository.save(action);

        if (requester != null) {
            notificationRhService.createNotification(requester, actor, "Avance approuvée", "Votre avance de " + saved.getMontant() + " a été approuvée.", "AVANCE", "Avance", saved.getId());
        }

        return saved;
    }

    @Transactional
    public Avance reject(Long avanceId, Long approbateurUtilisateurId, String commentaire) {
        Avance a = avanceRepository.findById(avanceId).orElseThrow(() -> new IllegalArgumentException("Avance introuvable : " + avanceId));
        a.setStatut(AvanceStatus.REFUSEE);
        a.setDateDecision(LocalDateTime.now());
        Utilisateur approbateurR = utilisateurService.findById(approbateurUtilisateurId);
        if (approbateurR == null) {
            throw new IllegalArgumentException("Approbateur introuvable : " + approbateurUtilisateurId);
        }
        a.setApprobateur(approbateurR);
        Avance saved = avanceRepository.save(a);
        Utilisateur actor = approbateurR;
        Utilisateur requester = utilisateurService.findByPersonId(saved.getPersonnel().getPerson().getId());

        AvanceAction action = new AvanceAction(saved, AvanceStatus.DEMANDEE, AvanceStatus.REFUSEE, actor, commentaire);
        avanceActionRepository.save(action);

        if (requester != null) {
            notificationRhService.createNotification(requester, actor, "Avance refusée", "Votre avance de " + saved.getMontant() + " a été refusée. Raison: " + commentaire, "AVANCE", "Avance", saved.getId());
        }

        return saved;
    }

    public java.math.BigDecimal getTotalApprovedAdvancesForPeriod(Long personnelId, Integer mois, Integer annee) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23,59,59);
        List<Avance> list = avanceRepository.findByPersonnel_IdAndStatutAndDateDecisionBetween(personnelId, AvanceStatus.APPROUVEE, start, end);
        return list.stream().map(a -> a.getMontant() != null ? a.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Avance> getApprovedAdvancesForPeriod(Long personnelId, Integer mois, Integer annee) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23,59,59);
        return avanceRepository.findByPersonnel_IdAndStatutAndDateDecisionBetween(personnelId, AvanceStatus.APPROUVEE, start, end);
    }

    public List<Avance> findPendingRequests() {
        return avanceRepository.findByStatut(AvanceStatus.DEMANDEE);
    }
}
