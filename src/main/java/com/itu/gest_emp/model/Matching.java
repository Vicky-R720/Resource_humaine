package com.itu.gest_emp.model;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Matching {
    
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/google/flan-t5-large";
    private static final String HF_TOKEN = "votre_token_ici";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    
    public static double calculerPourcentageMatching(String competencesRecruteur, String competencesCandidat) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String prompt = createPrompt(competencesRecruteur, competencesCandidat);
                String response = callHuggingFaceAPI(prompt);
                return extractPercentageFromResponse(response);
                
            } catch (IOException e) {
                if (e.getMessage().contains("429") && attempt < MAX_RETRIES) {
                    handleRateLimit(attempt, e);
                    continue;
                }
                System.err.println("Erreur lors du matching (tentative " + attempt + "): " + e.getMessage());
                return -1.0;
            } catch (Exception e) {
                System.err.println("Erreur inattendue (tentative " + attempt + "): " + e.getMessage());
                return -1.0;
            }
        }
        return -1.0;
    }
    
    private static String createPrompt(String competencesRecruteur, String competencesCandidat) {
        return "Calculate matching percentage 0-100 between job requirements and candidate profile:\n" +
               "JOB: " + competencesRecruteur + "\n" +
               "CANDIDATE: " + competencesCandidat + "\n" +
               "RULES: wrong degree=-50, no skills=0-10, partial skills=30-70, perfect match=80-100\n" +
               "Answer ONLY with the number:";
    }
    
    private static String callHuggingFaceAPI(String prompt) throws IOException {
        HttpURLConnection connection = createConnection();
        sendRequest(connection, prompt);
        validateResponse(connection);
        return readResponse(connection);
    }
    
    private static HttpURLConnection createConnection() throws IOException {
        URL url = new URL(HF_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + HF_TOKEN);
        connection.setConnectTimeout(60000); // 60 seconds timeout
        connection.setReadTimeout(60000);
        connection.setDoOutput(true);
        
        return connection;
    }
    
    private static void sendRequest(HttpURLConnection connection, String prompt) throws IOException {
        String jsonBody = buildHuggingFaceJsonRequest(prompt);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }
    
    private static void validateResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            String errorMessage = readErrorMessage(connection);
            
            // Gérer le cas où le modèle est en cours de chargement
            if (errorMessage.contains("loading")) {
                System.out.println("Modèle en cours de chargement, attente de 30 secondes...");
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                throw new IOException("Modèle loading - retry needed");
            }
            
            throw new IOException("Erreur API Hugging Face: " + responseCode + " - " + errorMessage);
        }
    }
    
    private static String readErrorMessage(HttpURLConnection connection) throws IOException {
        try (InputStream errorStream = connection.getErrorStream();
             BufferedReader errorReader = new BufferedReader(
                 new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            return errorResponse.toString();
        } catch (Exception e) {
            return "Unable to read error message: " + e.getMessage();
        }
    }
    
    private static String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }
        return extractContentFromHuggingFaceResponse(response.toString());
    }
    
    private static String buildHuggingFaceJsonRequest(String prompt) {
        String escapedPrompt = escapeJsonString(prompt);
        
        return String.format("""
        {
            "inputs": "%s",
            "parameters": {
                "max_new_tokens": 10,
                "temperature": 0.1,
                "do_sample": false
            }
        }
        """, escapedPrompt);
    }
    
    private static String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private static String extractContentFromHuggingFaceResponse(String jsonResponse) {
        try {
            // Format: [{"generated_text": "65"}]
            if (jsonResponse.contains("generated_text")) {
                int start = jsonResponse.indexOf("generated_text") + 16;
                int end = jsonResponse.indexOf("\"", start);
                return jsonResponse.substring(start, end).trim();
            }
            return "0";
        } catch (Exception e) {
            System.err.println("Erreur parsing JSON: " + e.getMessage());
            return "0";
        }
    }
    
    private static double extractPercentageFromResponse(String response) {
        try {
            System.out.println("Réponse brute: " + response);
            
            // Extraire uniquement les chiffres et points
            String cleanResponse = response.replaceAll("[^0-9.]", "").trim();
            if (cleanResponse.isEmpty()) {
                return 0.0;
            }
            
            double result = Double.parseDouble(cleanResponse);
            return Math.max(0, Math.min(100, result));
            
        } catch (NumberFormatException e) {
            System.err.println("Impossible de parser: " + response);
            return 0.0;
        }
    }
    
    private static void handleRateLimit(int attempt, IOException e) {
        System.err.println("Attente avant nouvelle tentative (" + attempt + ")...");
        try {
            TimeUnit.SECONDS.sleep(10 * attempt);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static boolean testAPIConnection() {
        try {
            String response = callHuggingFaceAPI("Answer with number 50:");
            System.out.println("Test réponse: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("Test échoué: " + e.getMessage());
            return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Test de matching ===");
        
        // Test simple d'abord
        if (testAPIConnection()) {
            System.out.println("✅ API accessible");
            
            String job = "Poste: Développeur Java, Diplôme: Informatique, Expérience: 3 ans";
            String candidate = "Compétences: Java, Spring, SQL; Diplôme: Informatique; Expérience: 2 ans";
            
            double matching = calculerPourcentageMatching(job, candidate);
            System.out.println("Résultat matching: " + matching + "%");
        }
    }
}