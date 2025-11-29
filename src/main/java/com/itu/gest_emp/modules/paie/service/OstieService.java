package com.itu.gest_emp.modules.paie.service;

import com.itu.gest_emp.modules.paie.model.Ostie;
import com.itu.gest_emp.modules.paie.repository.OstieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OstieService {

    private final OstieRepository ostieRepository;

    // ===============================
    // CALCUL ORIENTÉ OBJET
    // ===============================

    /**
     * Charge les paramètres OSTIE pour une date donnée et prépare le calcul avec un
     * salaire
     */
    public Ostie loadCurrent(BigDecimal salaireBrut) {
        return load(LocalDate.now(), salaireBrut);
    }

    public Ostie load(LocalDate date, BigDecimal salaireBrut) {
        Ostie ostie = ostieRepository.findByDateValidite(date)
                .orElseThrow(() -> new RuntimeException(
                        "Aucun paramètre OSTIE trouvé à la date " + date));
        ostie.setSalaireBrut(salaireBrut);
        return ostie;
    }

    // ===============================
    // FONCTIONS CRUD
    // ===============================

    public List<Ostie> getAllParametresActifs() {
        return ostieRepository.findByActifTrueOrderByDateDebutValiditeDesc();
    }

    @Transactional
    public Ostie saveParametres(Ostie ostie) {
        if (ostie.getId() == null && ostieRepository.existsByDateDebutValidite(
                ostie.getDateDebutValidite())) {
            throw new RuntimeException(
                    "Un paramètre OSTIE existe déjà à la date " + ostie.getDateDebutValidite());
        }
        return ostieRepository.save(ostie);
    }

    @Transactional
    public void desactiverParametres(Long id) {
        Ostie ostie = ostieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètres OSTIE introuvables"));
        ostie.setActif(false);
        ostieRepository.save(ostie);
    }

    public Ostie getById(Long id) {
        return ostieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètres OSTIE introuvables"));
    }

}
