package com.itu.gest_emp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login-entreprise")
public class LoginEntrepriseController {

    @GetMapping("")
    public String showLoginForm(Model model) {
        return "login-entreprise";
    }

    @PostMapping("/authenticate")
    public String authenticate(
            @RequestParam("fonction") String fonction,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Vérification des identifiants
        if (isValidCredentials(fonction, password)) {
            // Stocker le rôle dans la session
            session.setAttribute("entrepriseRole", fonction);
            session.setAttribute("entrepriseLoggedIn", true);
            
            // Redirection selon le rôle
            return redirectByRole(fonction);
        } else {
            redirectAttributes.addFlashAttribute("error", "Fonction ou mot de passe incorrect");
            return "redirect:/login-entreprise";
        }
    }

    private boolean isValidCredentials(String fonction, String password) {
        // Vérification des credentials
        switch (fonction.toLowerCase()) {
            case "secretaire":
                return "secretaire".equals(password);
            case "rh":
                return "rh".equals(password);
            case "responsable technique":
                return "responsable".equals(password);
            case "Responsable Communication":
                return "rc".equals(password);
            case "direction":
                return "direction".equals(password);
            default:
                return false;
        }
    }

    private String redirectByRole(String fonction) {
        switch (fonction.toLowerCase()) {
            case "secretaire":
                return "redirect:/offers/create";
            case "rh":
                return "redirect:/admin";
            case "responsable technique":
                return "redirect:/persons/appliances";
            case "responsable communication":
                return "redirect:/create/offers";
            case "direction":
                return "redirect:/admin/personnel";
            default:
                return "redirect:/login-entreprise";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("entrepriseRole");
        session.removeAttribute("entrepriseLoggedIn");
        return "redirect:/login-entreprise";
    }
}