package com.itu.gest_emp.modules.personnel.service;

import com.itu.gest_emp.modules.personnel.model.*;
import com.itu.gest_emp.modules.personnel.repository.PersonnelRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonnelRhService {

    private final PersonnelRhRepository personnelRhRepository;

    public List<PersonnelRh> findAll() {
        return personnelRhRepository.findAll();
    }

    public Optional<PersonnelRh> findById(Long id) {
        return personnelRhRepository.findById(id);
    }

    public Optional<PersonnelRh> findByMatricule(String matricule) {
        return personnelRhRepository.findByMatricule(matricule);
    }

    public List<PersonnelRh> findByStatut(String statut) {
        return personnelRhRepository.findByStatut(statut);
    }

    public Optional<PersonnelRh> findByPersonId(Long personId) {
        return personnelRhRepository.findByPersonId(personId);
    }

    public PersonnelRh save(PersonnelRh personnelRh) {
        return personnelRhRepository.save(personnelRh);
    }

    public PersonnelRh create(PersonnelRh personnelRh) {
        if (personnelRh.getDateEmbauche() == null) {
            personnelRh.setDateEmbauche(LocalDate.now());
        }
        if (personnelRh.getStatut() == null) {
            personnelRh.setStatut("actif");
        }
        return personnelRhRepository.save(personnelRh);
    }

    public PersonnelRh update(Long id, PersonnelRh personnelRh) {
        PersonnelRh existing = personnelRhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'ID: " + id));

        // Mise à jour des champs
        if (personnelRh.getPerson() != null)
            existing.setPerson(personnelRh.getPerson());
        if (personnelRh.getPost() != null)
            existing.setPost(personnelRh.getPost());
        if (personnelRh.getCin() != null)
            existing.setCin(personnelRh.getCin());
        if (personnelRh.getCinDateDelivery() != null)
            existing.setCinDateDelivery(personnelRh.getCinDateDelivery());
        if (personnelRh.getCinPlaceDelivery() != null)
            existing.setCinPlaceDelivery(personnelRh.getCinPlaceDelivery());
        if (personnelRh.getSexe() != null)
            existing.setSexe(personnelRh.getSexe());
        if (personnelRh.getSituationFamiliale() != null)
            existing.setSituationFamiliale(personnelRh.getSituationFamiliale());
        if (personnelRh.getNombreEnfants() != null)
            existing.setNombreEnfants(personnelRh.getNombreEnfants());
        if (personnelRh.getNationalite() != null)
            existing.setNationalite(personnelRh.getNationalite());
        if (personnelRh.getLieuNaissance() != null)
            existing.setLieuNaissance(personnelRh.getLieuNaissance());
        if (personnelRh.getPersonneUrgenceNom() != null)
            existing.setPersonneUrgenceNom(personnelRh.getPersonneUrgenceNom());
        if (personnelRh.getPersonneUrgenceContact() != null)
            existing.setPersonneUrgenceContact(personnelRh.getPersonneUrgenceContact());
        if (personnelRh.getPersonneUrgenceLien() != null)
            existing.setPersonneUrgenceLien(personnelRh.getPersonneUrgenceLien());
        if (personnelRh.getRib() != null)
            existing.setRib(personnelRh.getRib());
        if (personnelRh.getBanque() != null)
            existing.setBanque(personnelRh.getBanque());
        if (personnelRh.getNumeroCnaps() != null)
            existing.setNumeroCnaps(personnelRh.getNumeroCnaps());
        if (personnelRh.getNumeroOstie() != null)
            existing.setNumeroOstie(personnelRh.getNumeroOstie());
        if (personnelRh.getStatut() != null)
            existing.setStatut(personnelRh.getStatut());

        return personnelRhRepository.save(existing);
    }

    public void deleteById(Long id) {
        personnelRhRepository.deleteById(id);
    }

    public void terminerContrat(Long id, String motif) {
        PersonnelRh personnel = personnelRhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'ID: " + id));

        personnel.setDateSortie(LocalDate.now());
        personnel.setMotifSortie(motif);
        personnel.setStatut("inactif");
        personnelRhRepository.save(personnel);
    }
}