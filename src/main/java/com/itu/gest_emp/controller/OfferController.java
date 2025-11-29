package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.Offer;
import com.itu.gest_emp.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itu.gest_emp.service.PostService;
import com.itu.gest_emp.service.ContractTypeService;
import com.itu.gest_emp.service.DiplomaService;
import com.itu.gest_emp.service.FiliereService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/offers")
public class OfferController {

    // Ajoutez ces méthodes au OfferController
    @Autowired
    private PostService postService;

    @Autowired
    private ContractTypeService contractTypeService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private DiplomaService diplomaService;

    @Autowired
    private FiliereService filiereService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("offer", new Offer());
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        model.addAttribute("diplomas", diplomaService.getAllDiplomas());
        model.addAttribute("filieres", filiereService.getAllFilieres());
        return "offer-form";
    }

    // Traiter la soumission du formulaire
    @PostMapping("/create")
    public String createOffer(@ModelAttribute Offer offer) {
        offerService.createOffer(offer);
        System.out.println("test");
        return "redirect:/offers";
    }

    // Afficher toutes les offres
    @GetMapping
    public String getAllOffers(Model model) {
        List<Offer> offers = offerService.getAllOffers();
        model.addAttribute("offers", offers);
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        model.addAttribute("posts", postService.getAllPosts());
        return "offers-list";
    }

    @GetMapping("/search")
    public String searchOffers(@RequestParam(required = false) String location,
            @RequestParam(required = false) Long contractTypeId,
            Model model) {
        List<Offer> filteredOffers = offerService.filterOffers(location, contractTypeId);
        model.addAttribute("offers", filteredOffers);
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        return "offers-list :: offersListFragment";
    }

    @GetMapping("/details/{id}")
    public String showOfferDetails(@PathVariable("id") Long id, Model model) {
        Optional<Offer> offerData = offerService.getOfferById(id);
        if (offerData.isPresent()) {
            model.addAttribute("offer", offerData.get());
            return "offer-detail";
        } else {
            return "redirect:/offers";
        }
    }

    // API REST - Récupérer toutes les offres
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Offer>> getAllOffersApi() {
        try {
            List<Offer> offers = offerService.getAllOffers();
            if (offers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(offers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Créer une offre
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Offer> createOfferApi(@RequestBody Offer offer) {
        try {
            Offer newOffer = offerService.createOffer(offer);
            return new ResponseEntity<>(newOffer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API REST - Récupérer une offre par ID
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Offer> getOfferByIdApi(@PathVariable("id") Long id) {
        Optional<Offer> offerData = offerService.getOfferById(id);
        return offerData.map(offer -> new ResponseEntity<>(offer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // API REST - Supprimer une offre
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<HttpStatus> deleteOfferApi(@PathVariable("id") Long id) {
        try {
            offerService.deleteOffer(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    



}