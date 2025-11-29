package com.itu.gest_emp.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.itu.gest_emp.model.ChatbotConversationRH;
import com.itu.gest_emp.model.PersonnelRH;
import com.itu.gest_emp.repository.ChatbotConversationRHRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    @Autowired
    private ChatbotConversationRHRepository chatbotRepository;

    @Autowired
    private AuthenticationService authenticationService;

    // ⭐⭐ VOTRE CLÉ API GEMINI ICI ⭐⭐
    private final String geminiApiKey = "AIzaSyAfAcRpGtbCUXdKRcAYD2rBprIauGa6g4w"; // Remplacez par votre vraie clé complète

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Base de connaissances RH - CORRIGÉ avec HashMap
    private final Map<String, String> knowledgeBase;

    public ChatbotService() {
        // Initialisation dans le constructeur
        this.knowledgeBase = createKnowledgeBase();
    }

    private Map<String, String> createKnowledgeBase() {
        Map<String, String> base = new HashMap<>();

        // Salutations
        base.put("bonjour",
                "👋 Bonjour ! Je suis votre assistant RH intelligent. Je peux vous aider avec les congés, la paie, les contrats et bien plus !");
        base.put("salut", "👋 Bonjour ! Comment puis-je vous assister aujourd'hui ?");
        base.put("coucou", "👋 Bonjour ! Assistant RH à votre service !");

        // Congés
        base.put("congé",
                "🏖️ **Gestion des congés** :\n\n📋 **Procédure :**\n1. Allez dans 'Mes congés'\n2. Cliquez 'Nouvelle demande' \n3. Sélectionnez les dates\n4. Soumettez la demande\n\n⏱️ **Délai :** Validation sous 48h\n📊 **Solde :** Consultable en temps réel");
        base.put("vacance",
                "🏖️ **Congés payés** :\n• Droit : 2.5 jours par mois\n• Préavis : 48 heures\n• Solde : Visible dans 'Mes congés'");
        base.put("rtt",
                "📅 **RTT** :\n• Crédité mensuellement\n• Consultez votre solde RTT\n• Utilisation comme les congés classiques");

        // Paie
        base.put("salaire",
                "💰 **Informations salariales** :\n\n📅 **Paiement :** Dernier jour ouvrable du mois\n📄 **Bulletins :** Section 'Mes bulletins'\n📧 **Contact :** service.paie@entreprise.mg\n\nVotre bulletin est disponible dès le 28 du mois.");
        base.put("paie",
                "💰 **Service paie** :\n• Paiement : fin du mois\n• Bulletin : consultable en ligne\n• Questions : paie@entreprise.mg");
        base.put("bulletin",
                "📄 **Bulletin de paie** :\n• Disponible dans 'Mes bulletins'\n• Téléchargement PDF possible\n• Archivage 5 ans");

        // Contrats
        base.put("contrat",
                "📄 **Gestion des contrats** :\n\n📂 **Votre contrat :** Profil → Mes documents\n✏️ **Modifications :** Contactez le RH\n🔄 **Renouvellement :** 1 mois avant échéance\n\nBesoin d'une copie ? Demande via 'Mes demandes'");
        base.put("cdi", "📑 **CDI** :\n• Contrat à durée indéterminée\n• Période d'essai : 3 mois\n• Préavis : 3 mois");
        base.put("cdd",
                "📑 **CDD** :\n• Contrat à durée déterminée\n• Se termine à la date prévue\n• Renouvellement possible");

        // Absences
        base.put("absence",
                "🏥 **Absence maladie** :\n\n🚨 **Procédure :**\n1. Prévenez votre manager\n2. Certificat médical sous 48h\n3. Régularisation via l'application\n\n💰 **Paiement :** À partir du 4ème jour\n📞 **Urgence :** +261 34 00 000 00");
        base.put("maladie",
                "🤒 **Congé maladie** :\n• Certificat médical obligatoire\n• Transmission sous 48h\n• Paiement à partir du 4ème jour");
        base.put("retard",
                "⏰ **Retard** :\n• Prévenir votre manager\n• Justification si répété\n• Ponctualité appréciée");

        // Documents
        base.put("document",
                "📑 **Documents RH** :\n• Attestations : Mes demandes → Nouvelle\n• Délai : 24h maximum\n• Types : travail, salaire, emploi");
        base.put("attestation",
                "📄 **Attestations** :\n• Génération automatique\n• Délai : 24 heures\n• Formats : PDF uniquement");

        // Formations
        base.put("formation",
                "🎓 **Formations** :\n• Catalogue disponible\n• Inscription avec accord manager\n• Financement : 100% entreprise");

        // Contact
        base.put("rh",
                "📞 **Service RH** :\n• Lundi-Vendredi : 8h-17h\n• Téléphone : +261 34 00 000 00\n• Email : rh@entreprise.mg");
        base.put("contact",
                "📞 **Nous contacter** :\n• RH : rh@entreprise.mg\n• Paie : paie@entreprise.mg\n• Urgence : +261 34 00 000 00");

        // Remerciements
        base.put("merci", "😊 Je vous en prie ! N'hésitez pas si vous avez d'autres questions.");
        base.put("help",
                "ℹ️ **Domaines d'aide** :\n• 📅 Congés et absences\n• 💰 Paie et bulletins\n• 📄 Contrats et documents\n• 🎓 Formations\n• 🏥 Santé et mutuelle\n\nPosez-moi une question précise !");

        return base;
    }

    @PostConstruct
    public void init() {
        logger.info("🤖 ChatbotService initialisé");
        if (geminiApiKey != null && geminiApiKey.length() > 10) {
            logger.info("🔑 Clé Gemini configurée: {}...{}",
                    geminiApiKey.substring(0, Math.min(10, geminiApiKey.length())),
                    geminiApiKey.substring(geminiApiKey.length() - 4));
        } else {
            logger.info("🔑 Clé Gemini: {}", geminiApiKey);
        }

        testGeminiConnection();
    }

    private void testGeminiConnection() {
        logger.info("🧪 Test de connexion Gemini...");

        try {
            // Test simple avec un modèle connu
            String testResponse = getResponseFromGemini("Bonjour");
            if (testResponse != null && !testResponse.contains("Mode simulation")) {
                logger.info("✅ Gemini fonctionne !");
            } else {
                logger.info("🔄 Utilisation du mode simulation local");
            }
        } catch (Exception e) {
            logger.info("🔄 Mode simulation activé (Erreur: {})", e.getMessage());
        }
    }

    public ChatbotResponse processQuestion(String question) {
        try {
            PersonnelRH personnel = authenticationService.getCurrentPersonnel();
            String response = getResponseFromGemini(question);

            // Sauvegarder la conversation
            ChatbotConversationRH conversation = new ChatbotConversationRH();
            conversation.setPerson(personnel.getPerson());
            conversation.setSessionId("session-" + personnel.getId() + "-" + System.currentTimeMillis());
            conversation.setQuestion(question);
            conversation.setReponse(response);
            conversation.setCategorie(detectCategory(question));
            chatbotRepository.save(conversation);

            return new ChatbotResponse(response, true);

        } catch (Exception e) {
            logger.error("Erreur processQuestion: {}", e.getMessage());
            return new ChatbotResponse("❌ Erreur temporaire. Veuillez réessayer.", false);
        }
    }

    private String getResponseFromGemini(String question) {
        // D'abord essayer Gemini
        String geminiResponse = tryAllGeminiModels(question);
        if (geminiResponse != null) {
            return geminiResponse;
        }

        // Sinon utiliser la base de connaissances locale
        return getKnowledgeBaseResponse(question);
    }

    private String tryAllGeminiModels(String question) {
        String[] modelsToTry = {
                "gemini-1.5-flash",
                "gemini-1.5-flash-001",
                "gemini-1.0-pro",
                "gemini-1.0-pro-001",
                "gemini-pro"
        };

        for (String model : modelsToTry) {
            try {
                String response = callGeminiModel(model, question);
                if (response != null) {
                    logger.info("✅ Réponse Gemini avec modèle: {}", model);
                    return response;
                }
            } catch (Exception e) {
                logger.debug("❌ Modèle {} échoué", model);
            }
        }

        return null;
    }

    private String callGeminiModel(String model, String question) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1/models/" + model + ":generateContent?key="
                    + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Tu es un assistant RH professionnel. Réponds en français de manière concise et utile à cette question : "
                    + question;
            String requestBody = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", escapeJson(prompt));

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText()
                        .trim();
            }

        } catch (Exception e) {
            // Ignorer les erreurs silencieusement
        }
        return null;
    }

    private String getKnowledgeBaseResponse(String question) {
        String lowerQuestion = question.toLowerCase();

        // Recherche exacte d'abord
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerQuestion.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Recherche par mots-clés étendus
        if (lowerQuestion.contains("horaire") || lowerQuestion.contains("heure")) {
            return "⏰ **Horaires de travail** :\n• Lundi-Vendredi : 8h00-17h00\n• Pause déjeuner : 12h00-13h00\n• Flexibilité possible avec accord manager";
        } else if (lowerQuestion.contains("mutuelle") || lowerQuestion.contains("santé")) {
            return "🏥 **Mutuelle santé** :\n• Prise en charge : 80%\n• Famille : incluse\n• Dentaire/optique : forfaits\n• Contact : mutuelle@entreprise.mg";
        } else if (lowerQuestion.contains("retraite") || lowerQuestion.contains("cnaps")) {
            return "👵 **Retraite CNAPS** :\n• Cotisation : 1% salarié + 1% employeur\n• Relevé disponible\n• Contact : cnaps.mg";
        } else if (lowerQuestion.contains("ostie")) {
            return "🏥 **OSTIE** :\n• Prévoyance sociale\n• Soins médicaux\n• Indemnités journalières\n• Contact : ostie.mg";
        }

        // Réponse par défaut
        return """
                🤖 **Assistant RH Intelligent**

                Je peux vous aider avec :
                • 📅 **Congés** : demande, solde, procédure
                • 💰 **Paie** : bulletin, dates, contact
                • 📄 **Contrats** : consultation, copies
                • 🏥 **Absences** : maladie, procédure
                • 🎓 **Formations** : catalogue, inscription
                • 📞 **Contacts** : RH, paie, urgences

                Posez-moi une question précise ! 😊
                """;
    }

    private String escapeJson(String text) {
        if (text == null)
            return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String detectCategory(String question) {
        String lowerQuestion = question.toLowerCase();
        if (lowerQuestion.contains("congé") || lowerQuestion.contains("vacance"))
            return "conges";
        if (lowerQuestion.contains("salaire") || lowerQuestion.contains("paie"))
            return "paie";
        if (lowerQuestion.contains("contrat"))
            return "contrat";
        if (lowerQuestion.contains("absence") || lowerQuestion.contains("maladie"))
            return "absences";
        if (lowerQuestion.contains("document") || lowerQuestion.contains("attestation"))
            return "documents";
        if (lowerQuestion.contains("formation"))
            return "formations";
        return "general";
    }

    public List<ChatbotConversationRH> getConversationHistory() {
        try {
            PersonnelRH personnel = authenticationService.getCurrentPersonnel();
            return chatbotRepository.findByPersonIdOrderByCreatedAtDesc(personnel.getPerson().getId());
        } catch (Exception e) {
            return List.of();
        }
    }

    public static record ChatbotResponse(String response, boolean success) {
    }
}