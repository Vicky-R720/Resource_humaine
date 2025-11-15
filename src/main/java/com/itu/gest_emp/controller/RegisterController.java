package com.itu.gest_emp.controller;

import com.itu.gest_emp.modules.shared.model.Notification;
import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.service.NotificationService;
import com.itu.gest_emp.modules.shared.service.PersonService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Controller
public class RegisterController {

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    // Formulaire d'inscription
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("person", new Person());
        return "register";
    }

    // Traitement de l'inscription
    @PostMapping("/register")
    public String registerPerson(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String adresse,
            @RequestParam String naissance,
            @RequestParam String contact,
            @RequestParam String mdp,
            @RequestParam(required = false) MultipartFile pdpFile,
            Model model) {

        try {
            // Vérifier si le contact existe déjà
            if (personService.findByContact(contact) != null) {
                model.addAttribute("error", "Ce contact est déjà utilisé");
                return "register";
            }

            // Convertir la date de naissance
            LocalDate dateNaissance = null;
            if (naissance != null && !naissance.isEmpty()) {
                dateNaissance = LocalDate.parse(naissance);
            }

            // Gérer l'upload de photo de profil (simplifié)
            String pdpPath = null;
            if (pdpFile != null && !pdpFile.isEmpty()) {
                // Ici vous pouvez sauvegarder le fichier et stocker le chemin
                pdpPath = "/uploads/" + pdpFile.getOriginalFilename();
                // Code pour sauvegarder le fichier réel...
            }

            // Créer la nouvelle personne
            Person newPerson = new Person(nom, prenom, adresse, dateNaissance, contact, mdp, pdpPath);
            
            // Sauvegarder la personne
            personService.savePerson(newPerson);

            model.addAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            return "register";
        }
    }
    @GetMapping("/login")
public String showLoginForm(Model model) {
    return "login";
}
  @GetMapping("/test")
public String showTest(Model model) {
    return "candidatecreate";
}
   // Traitement de la connexion
    @PostMapping("/login")
    public String processLogin(@RequestParam String contact, 
                              @RequestParam String mdp, 
                              Model model, 
                              HttpSession session) {
        
        Person person = personService.findByContact(contact);
        
        if (person == null || !person.getMdp().equals(mdp)) {
            model.addAttribute("error", "Contact ou mot de passe incorrect");
            return "login";
        }
        
        // Connexion réussie - stocker l'utilisateur en session
        session.setAttribute("user", person);
            List<Notification> notifications = notificationService.getNotificationsByPerson(person);
            session.setAttribute("notifications", notifications);
            int countNotif=notifications.size();
            System.out.println("liste de notif "+countNotif);
            session.setAttribute("Nombrenotifications", countNotif);

        
        
        return "redirect:/offers";
    }
}