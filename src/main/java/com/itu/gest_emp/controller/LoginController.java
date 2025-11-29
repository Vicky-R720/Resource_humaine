package com.itu.gest_emp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import com.itu.gest_emp.model.*;
import com.itu.gest_emp.repository.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private final PersonnelRHRepository personnelRHRepository;
    private final HttpSession session;

    public LoginController(PersonnelRHRepository personnelRHRepository, HttpSession session) {
        this.personnelRHRepository = personnelRHRepository;
        this.session = session;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            // Rechercher l'employé par matricule ou contact
            Optional<PersonnelRH> personnelOpt = personnelRHRepository.findByIdentifier(loginRequest.getUsername());

            if (personnelOpt.isEmpty()) {
                System.out.println("Aucun employé trouvé avec le matricule ou email fourni" + loginRequest.getUsername());
                redirectAttributes.addFlashAttribute("error", "Matricule ou email incorrect");
                return "redirect:/auth/login";
            }

            PersonnelRH personnel = personnelOpt.get();

            // Vérifier le statut
            // if (!"actif".equals(personnel.getStatut())) {
            //     redirectAttributes.addFlashAttribute("error", "Votre compte est désactivé");
            //     return "redirect:/auth/login";
            // }

            // Vérifier le mot de passe (basique pour l'exemple)
            if (!isValidPassword(loginRequest.getPassword(), personnel)) {
                redirectAttributes.addFlashAttribute("error", "Mot de passe incorrect");
                return "redirect:/auth/login";
            }

            // Stocker l'utilisateur en session
            session.setAttribute("currentPersonnel", personnel);
            session.setAttribute("personnelId", personnel.getId());
            session.setAttribute("personId", personnel.getPerson().getId());

            redirectAttributes.addFlashAttribute("success", "Connexion réussie !");
            if (personnel.getMatricule().equals("EMP002")) {
                return "redirect:/manager/dashboard";
            }
            return "redirect:/employee/self-service/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la connexion");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Déconnexion réussie");
        return "redirect:/auth/login";
    }

    private boolean isValidPassword(String inputPassword, PersonnelRH personnel) {
        // Logique de vérification du mot de passe
        // Pour l'exemple, on utilise un mot de passe par défaut
        String defaultPassword = "employe123";
        return defaultPassword.equals(inputPassword);
    }
}

// DTO pour la requête de login
class LoginRequest {
    private String username;
    private String password;

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}