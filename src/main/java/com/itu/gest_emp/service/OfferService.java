package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Offer;
import com.itu.gest_emp.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    // Récupérer toutes les offres
    public List<Offer> getAllOffers() {
        return offerRepository.findAllOrderByPublicationDateDesc();
    }

    // Récupérer une offre par ID
    public Optional<Offer> getOfferById(Long id) {
        return offerRepository.findById(id);
    }

    // Créer une nouvelle offre
    public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    // Mettre à jour une offre
    public Offer updateOffer(Long id, Offer offerDetails) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            offer.setPost(offerDetails.getPost());
            offer.setCompanyName(offerDetails.getCompanyName());
            offer.setLocation(offerDetails.getLocation());
            offer.setContractType(offerDetails.getContractType());
            offer.setRequiredProfile(offerDetails.getRequiredProfile());
            offer.setExperienceLevel(offerDetails.getExperienceLevel());
            offer.setDiploma(offerDetails.getDiploma());
            offer.setAvailablePlaces(offerDetails.getAvailablePlaces());
            return offerRepository.save(offer);
        } else {
            throw new RuntimeException("Offer not found with id: " + id);
        }
    }

    // Supprimer une offre
    public void deleteOffer(Long id) {
        if (offerRepository.existsById(id)) {
            offerRepository.deleteById(id);
        } else {
            throw new RuntimeException("Offer not found with id: " + id);
        }
    }

    // Rechercher par entreprise
    public List<Offer> searchByCompany(String companyName) {
        return offerRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }

    // Rechercher par localisation
    public List<Offer> searchByLocation(String location) {
        return offerRepository.findByLocationContainingIgnoreCase(location);
    }

    // Rechercher par type de contrat
    public List<Offer> searchByContractType(Long contractTypeId) {
        return offerRepository.findByContractTypeId(contractTypeId);
    }

    // Rechercher par poste
    public List<Offer> searchByPost(Long postId) {
        return offerRepository.findByPostId(postId);
    }

    public List<Offer> filterOffers(String location, Long contractTypeId) {
        List<Offer> allOffers = getAllOffers(); // toutes les offres

        return allOffers.stream()
                .filter(o -> location == null || location.isEmpty() || o.getLocation().equals(location))
                .filter(o -> contractTypeId == null || o.getContractType().getId().equals(contractTypeId))
                .collect(Collectors.toList());
    }

  

}