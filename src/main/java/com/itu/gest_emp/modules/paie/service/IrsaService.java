package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Irsa;
import com.itu.gest_emp.modules.paie.repository.IrsaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IrsaService {

    private final IrsaRepository irsaRepository;

    /**
     * Récupérer toutes les tranches valides à une date donnée
     */
    public List<Irsa> getTranchesByDate(LocalDate date) {
        List<Irsa> tranches = irsaRepository.findTranchesByDateValidite(date);
        if (tranches.isEmpty()) {
            throw new RuntimeException("Aucun barème IRSA trouvé pour la date " + date);
        }
        return tranches;
    }

    /**
     * Récupérer les tranches actuelles
     */
    public List<Irsa> getTranchesActuelles() {
        return getTranchesByDate(LocalDate.now());
    }

    

    /**
     * Calculer l'IRSA pour un salaire imposable à une date donnée
     */
    public BigDecimal calculerIrsa(BigDecimal salaireImposable,List<Irsa> tranches) {
        if (salaireImposable == null || salaireImposable.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal irsaTotal = BigDecimal.ZERO;

        for (Irsa tranche : tranches) {
            BigDecimal seuilMin = tranche.getSeuilMin();
            BigDecimal seuilMax = tranche.getSeuilMax();
            BigDecimal limite = tranche.getLimite();

            if (salaireImposable.compareTo(seuilMin.subtract(new BigDecimal(1))) > 0) {
                BigDecimal montantDansLaTranche;

                if (seuilMax == null) {
                    // Dernière tranche : toujours salaire - seuilMin
                    montantDansLaTranche = salaireImposable.subtract(seuilMin.subtract(new BigDecimal(1)));
                } else if (salaireImposable.compareTo(seuilMax) > 0) {
                    // Tranche intermédiaire dépassée : on applique la limite
                    montantDansLaTranche = limite;
                } else {
                    // Salaire dans cette tranche
                    montantDansLaTranche = salaireImposable;
                    if (montantDansLaTranche.compareTo(limite) > 0) {
                        montantDansLaTranche = limite;
                    }
                }

                BigDecimal impotTranche = montantDansLaTranche
                        .multiply(tranche.getTaux())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                irsaTotal = irsaTotal.add(impotTranche);
            }
        }
        return irsaTotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculer le salaire net après IRSA
     */
    // public BigDecimal calculerSalaireNetApresIrsa(BigDecimal salaireImposable) {
    //     BigDecimal irsa = calculerIrsa(salaireImposable);
    //     return salaireImposable.subtract(irsa);
    // }

    /**
     * Obtenir la tranche correspondant à un montant
     */
    public Irsa getTrancheByMontant(BigDecimal montant, LocalDate date) {
        return irsaRepository.findTrancheByMontantAndDate(montant, date)
                .orElse(null);
    }

    /**
     * Récupérer le barème le plus récent
     */
    public List<Irsa> getBaremeLePlusRecent() {
        return irsaRepository.findMostRecentBareme();
    }

    /**
     * Récupérer toutes les tranches actives
     */
    public List<Irsa> getAllTranchesActives() {
        return irsaRepository.findByActifTrueOrderByNumeroTrancheAsc();
    }

    /**
     * Créer ou mettre à jour une tranche IRSA
     */
    @Transactional
    public Irsa saveTranche(Irsa irsa) {
        // Vérifier qu'il n'existe pas déjà une tranche avec le même numéro à cette date
        if (irsa.getId() == null && irsaRepository.existsByNumeroTrancheAndDate(
                irsa.getNumeroTranche(),
                irsa.getDateDebutValidite())) {
            throw new RuntimeException(
                    "Une tranche IRSA n°" + irsa.getNumeroTranche() +
                            " existe déjà pour la date " + irsa.getDateDebutValidite());
        }

        // Validation : seuil_max doit être supérieur à seuil_min
        if (irsa.getSeuilMax() != null &&
                irsa.getSeuilMax().compareTo(irsa.getSeuilMin()) <= 0) {
            throw new RuntimeException(
                    "Le seuil maximum doit être supérieur au seuil minimum");
        }

        return irsaRepository.save(irsa);
    }

    /**
     * Créer un barème complet
     */
    @Transactional
    public List<Irsa> saveBaremeComplet(List<Irsa> tranches) {
        LocalDate dateDebut = tranches.get(0).getDateDebutValidite();

        // Vérifier que toutes les tranches ont la même date de début
        boolean sameDateDebut = tranches.stream()
                .allMatch(t -> t.getDateDebutValidite().equals(dateDebut));

        if (!sameDateDebut) {
            throw new RuntimeException(
                    "Toutes les tranches d'un barème doivent avoir la même date de début de validité");
        }

        return irsaRepository.saveAll(tranches);
    }

    /**
     * Désactiver une tranche
     */
    @Transactional
    public void desactiverTranche(Long id) {
        Irsa irsa = irsaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tranche IRSA introuvable"));
        irsa.setActif(false);
        irsaRepository.save(irsa);
    }

    /**
     * Récupérer une tranche par ID
     */
    public Irsa getById(Long id) {
        return irsaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tranche IRSA introuvable"));
    }

    public BigDecimal calculerIrsaPourTranche(BigDecimal salaireImposable, Irsa tranche) {
        BigDecimal seuilMin = tranche.getSeuilMin();
        BigDecimal seuilMax = tranche.getSeuilMax();
        BigDecimal limite = tranche.getLimite();

        if (salaireImposable.compareTo(seuilMin.subtract(BigDecimal.ONE)) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal montantDansLaTranche;
        if (seuilMax == null) {
            montantDansLaTranche = salaireImposable.subtract(seuilMin.subtract(BigDecimal.ONE));
        } else if (salaireImposable.compareTo(seuilMax) > 0) {
            montantDansLaTranche = limite;
        } else {
            montantDansLaTranche = salaireImposable;
            if (montantDansLaTranche.compareTo(limite) > 0) {
                montantDansLaTranche = limite;
            }
        }

        return montantDansLaTranche
                .multiply(tranche.getTaux())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

}