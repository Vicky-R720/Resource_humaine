package com.itu.gest_emp.controller;

import com.itu.gest_emp.model.AcademicalQualification;
import com.itu.gest_emp.model.Appliance;
import com.itu.gest_emp.model.Diploma;
import com.itu.gest_emp.model.Filiere;
import com.itu.gest_emp.model.Offer;
import com.itu.gest_emp.model.Sector;
import com.itu.gest_emp.modules.shared.model.Notification;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.NotificationService;
import com.itu.gest_emp.modules.shared.service.PostService;
import com.itu.gest_emp.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.itu.gest_emp.service.AcademicalQualificationServiceImpl;
import com.itu.gest_emp.service.ContractTypeService;
import com.itu.gest_emp.service.SectorService;
import com.itu.gest_emp.service.DiplomaService;
import com.itu.gest_emp.service.FiliereService;
import com.itu.gest_emp.service.ApplianceService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/offers")
public class OfferController {

    @Autowired
    private PostService postService;

    @Autowired
    private AcademicalQualificationServiceImpl academicalQualificationServiceImpl;

    @Autowired
    private SectorService sectorService;

     @Autowired
    private ApplianceService applianceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ContractTypeService contractTypeService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private DiplomaService diplomaService;

    @Autowired
    private FiliereService filiereService;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        model.addAttribute("offer", new Offer());
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        model.addAttribute("diplomas", diplomaService.getAllDiplomas());
        model.addAttribute("filieres", filiereService.getAllFilieres());
        
        // Charger les notifications si utilisateur connecté
        loadNotifications(model, session);
        
        return "offer-form";
    }

    // Traiter la soumission du formulaire
    @PostMapping("/create")
    public String createOffer(@ModelAttribute Offer offer, HttpSession session) {
        offer.setCompanyName("BeerBears");
        offerService.createOffer(offer);
        System.out.println("test");
        return "redirect:/offers";
    }

    @GetMapping
    public String getAllOffers(Model model, HttpSession session) {
        List<Offer> offers1 = offerService.getAllOffers();
        List<Offer> offers=new ArrayList<Offer>();
        LocalDate dateNow = LocalDate.now();

        for (Offer offer : offers1) {
            if(offer.isDispo() && !offer.getDateExpiration().isBefore(dateNow) ){
                offers.add(offer);
            }
        }
        model.addAttribute("offers", offers);
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        model.addAttribute("posts", postService.getAllPosts());
        
        // Charger les notifications si utilisateur connecté
        loadNotifications(model, session);
        
        return "offers-list";
    }

    @GetMapping("/search")
    public String searchOffers(@RequestParam(required = false) String location,
            @RequestParam(required = false) Long contractTypeId,
            Model model, HttpSession session) {
        List<Offer> filteredOffers = offerService.filterOffers(location, contractTypeId);
        model.addAttribute("offers", filteredOffers);
        model.addAttribute("contractTypes", contractTypeService.getAllContractTypes());
        
        // Charger les notifications si utilisateur connecté
        loadNotifications(model, session);
        
        return "offers-list :: offersListFragment";
    }

   @GetMapping("/details/{id}")
public String showOfferDetails(@PathVariable("id") Long id, Model model, HttpSession session) {
    Optional<Offer> offerData = offerService.getOfferById(id);
    
    if (offerData.isPresent()) {
        Offer offer = offerData.get();
        session.setAttribute("currentOffer", offer); // Correction ici
        model.addAttribute("offer", offer);
        
        // Charger les notifications si utilisateur connecté
        loadNotifications(model, session);
        
        return "offer-detail";
    } else {
        return "redirect:/offers";
    }
}

    // Méthode utilitaire pour charger les notifications
    private void loadNotifications(Model model, HttpSession session) {
        Person user = (Person) session.getAttribute("user");
        if (user != null) {
            List<Notification> notifications = notificationService.getNotificationsByPerson(user);
            model.addAttribute("notifications", notifications);
            session.setAttribute("notifications", notifications);
            
            System.out.println("Utilisateur connecté: " + user.getNom() + " " + user.getPrenom());
            System.out.println("Nombre de notifications: " + notifications.size());
        } else {
            System.out.println("Aucun utilisateur connecté");
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
    // Afficher le formulaire de candidature
@GetMapping("/apply/{id}")
public String showApplyForm(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
    Person user = (Person) session.getAttribute("user");
    
    if (user == null) {
        return "redirect:/login";
    }
    
    // Récupérer l'offre directement depuis la base de données
    Optional<Offer> offerData = offerService.getOfferById(id);
    
    if (!offerData.isPresent()) {
        redirectAttributes.addFlashAttribute("error", "Offre non trouvée");
        return "redirect:/offers";
    }
    
    Offer offer = offerData.get();
    
    if (offer.getPost() == null) {
        redirectAttributes.addFlashAttribute("error", "L'offre sélectionnée est invalide");
        return "redirect:/offers";
    }
    
    String postDispo = offer.getPost().getName();

    // Ajouter les données nécessaires au modèle
    model.addAttribute("posteDispo", postDispo);
    model.addAttribute("diplomas", diplomaService.getAllDiplomas());
    model.addAttribute("filieres", filiereService.getAllFilieres());
     model.addAttribute("sectors", sectorService.getAllSectors());
    model.addAttribute("offerId", id); // Ajouter l'ID de l'offre pour le formulaire
    
    // Charger les notifications
    loadNotifications(model, session);
    
    return "offer-apply";
}

// Traiter la soumission du formulaire de candidature
    @PostMapping("/apply")
    public String processApplication(
            @RequestParam("offerId") Long offerId,
            @RequestParam("diplomaId") Long diplomaId,
            @RequestParam("filiereId") Long filiereId,
            @RequestParam("sectorId") Long sectorId,
            @RequestParam("experienceYears") String experienceYears,
            @RequestParam("domainExperience") int domainExperience,
            @RequestParam("skills") String skills,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Person user = (Person) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/login";
        }
        
        try {
            // Vérifier que l'offre existe
            Optional<Offer> offerData = offerService.getOfferById(offerId);
            if (!offerData.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Offre non trouvée");
                return "redirect:/offers";
            }
            
            Offer offer = offerData.get();
            
            
            
            // Ici, vous devriez appeler votre service pour enregistrer la candidature
            // Par exemple: applicationService.createApplication(user, offer, diplomaId, filiereId, etc.)
            
            // Pour l'exemple, nous affichons juste les données reçues
            System.out.println("Candidature reçue:");
            System.out.println("Utilisateur: " + user.getNom() + " " + user.getPrenom());
            System.out.println("Offre ID: " + offerId);
            System.out.println("Diplôme ID: " + diplomaId);
            System.out.println("Filière ID: " + filiereId);
            System.out.println("Secteur ID: " + sectorId);
            System.out.println("Années d'expérience: " + experienceYears);
            System.out.println("Expérience dans le domaine: " + domainExperience);
            System.out.println("Compétences: " + skills);
            Appliance ap=new Appliance(offer,"null",skills,user);
            ap.setTraitement("en_cours");
            applianceService.saveAppliance(ap);
            Diploma dip=diplomaService.getDiplomaById(diplomaId);
            Filiere fil=filiereService.getFiliereById(filiereId);
            Sector se=sectorService.getSectorById(sectorId);
            AcademicalQualification academicalQualification=new AcademicalQualification(dip,se,ap,fil);
            academicalQualificationServiceImpl.saveQualification(academicalQualification);
            
          
            
            
            // Redirection avec message de succès
            redirectAttributes.addFlashAttribute("success", "Votre candidature a été soumise avec succès!");
            return "redirect:/offers/details/"+offerId;
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite lors de la soumission de votre candidature");
            return "redirect:/offers/apply/" + offerId;
        }
    }
}