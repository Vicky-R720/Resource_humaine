package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Cnaps;
import com.itu.gest_emp.modules.paie.repository.CnapsRepository;
import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CnapsService {

    private final CnapsRepository cnapsRepository;

    /**
     * Charger les paramètres CNAPS valides pour un secteur à une date donnée
     */
    public Cnaps load(SecteurActiviteEnum secteur, LocalDate date) {
        return cnapsRepository.findBySecteurAndDateValidite(secteur, date)
                .orElseThrow(() -> new RuntimeException(
                        "Aucun paramètre CNAPS trouvé pour le secteur " + secteur + " à la date " + date));
    }

    /**
     * Charger les paramètres CNAPS actuels pour un secteur
     */
    public Cnaps loadCurrent(SecteurActiviteEnum secteur) {
        return load(secteur, LocalDate.now());
    }

    /**
     * Récupérer tous les paramètres actifs
     */
    public List<Cnaps> getAllParametresActifs() {
        return cnapsRepository.findByActifTrueOrderByDateDebutValiditeDesc();
    }

    /**
     * Créer ou mettre à jour des paramètres CNAPS
     */
    @Transactional
    public Cnaps save(Cnaps cnaps) {
        if (cnaps.getId() == null && cnapsRepository.existsBySecteurAndDate(
                cnaps.getSecteurActivite(),
                cnaps.getDateDebutValidite())) {
            throw new RuntimeException(
                    "Un paramètre CNAPS existe déjà pour le secteur " +
                            cnaps.getSecteurActivite() + " à la date " + cnaps.getDateDebutValidite());
        }
        return cnapsRepository.save(cnaps);
    }

    /**
     * Désactiver un paramètre CNAPS
     */
    @Transactional
    public void disable(Long id) {
        Cnaps cnaps = cnapsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètres CNAPS introuvables"));
        cnaps.setActif(false);
        cnapsRepository.save(cnaps);
    }

    /**
     * Récupérer un paramètre par ID
     */
    public Cnaps getById(Long id) {
        return cnapsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètres CNAPS introuvables"));
    }
}
