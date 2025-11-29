package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.ContractsRh;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PreavisService {

    private final ContractsRhService contractsRhService;

    /**
     * Calcule la durée du préavis en jours ou mois selon le contrat
     */
    public int calculerDureePreavis(ContractsRh contrat) {
        return contrat.getDureePreavis(); // par exemple en jours ou mois
    }

    /**
     * Détermine l'initiateur de la rupture
     * @param motif "licenciement" ou "demission"
     * @return "employeur" ou "employe"
     */
    public String getInitiateur(String motif) {
        if ("licenciement".equalsIgnoreCase(motif)) {
            return "employeur";
        } else if ("demission".equalsIgnoreCase(motif)) {
            return "employe";
        } else {
            throw new IllegalArgumentException("Motif inconnu : " + motif);
        }
    }

    /**
     * Génère l'indemnité de préavis si celui-ci n'est pas effectué
     * @param contrat Contrat concerné
     * @param motif Motif de rupture
     * @param salaireMensuel Salaire de base du salarié
     * @return montant de l'indemnité
     */
    public BigDecimal genererIndemnitePreavis(ContractsRh contrat, String motif, BigDecimal salaireMensuel) {
        String initiateur = getInitiateur(motif);
        int dureePreavis = calculerDureePreavis(contrat);

        BigDecimal indemnité = salaireMensuel
                .multiply(BigDecimal.valueOf(dureePreavis))
                .divide(BigDecimal.valueOf(30)); // si préavis en jours, 30j = 1 mois

        if ("employeur".equals(initiateur)) {
            // L'employeur doit payer le salarié
            contrat.setIndemnitePreavis(indemnité);
        } else {
            // L'employé doit payer → retenir sur salaire final
            contrat.setRetenuePreavis(indemnité);
        }
        contrat.setStatut("inactif");
        // Sauvegarde du contrat avec l'indemnité
        contractsRhService.update(contrat.getId(), contrat);

        return indemnité;
    }
}
