package com.itu.gest_emp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu.gest_emp.modules.personnel.model.DocumentsRH;

import com.itu.gest_emp.service.DocumentArchivingService;
import com.itu.gest_emp.service.PersonnelRHService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentArchivingController {

    @Autowired
    private DocumentArchivingService documentArchivingService;

    @Autowired
    private PersonnelRHService personnelRHService;

    private final String UPLOAD_DIR = "./uploads/documents/";

    @GetMapping
    public String listDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<DocumentsRH> documents = documentArchivingService.getAllDocuments(PageRequest.of(page, size));
        model.addAttribute("documents", documents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());

        return "documents/list";
    }

    @GetMapping("/personnel/{personnelId}")
    public String getPersonnelDocuments(
            @PathVariable Long personnelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<DocumentsRH> documents = documentArchivingService.getDocumentsByPersonnel(personnelId,
                PageRequest.of(page, size));
        model.addAttribute("documents", documents);
        model.addAttribute("personnelId", personnelId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());

        return "documents/personnel-documents";
    }

    // CORRECTION : Méthode GET pour afficher le formulaire
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        DocumentsRH document = new DocumentsRH();
        model.addAttribute("document", document);

        // Ajouter la liste du personnel pour le select
        List<PersonnelRH> personnelList = personnelRHService.getAllPersonnel();
        model.addAttribute("personnelList", personnelList);

        return "documents/upload-form";
    }

    // CORRECTION : Méthode POST pour traiter l'upload
    @PostMapping("/upload")
    public String handleUpload(
            @RequestParam("personnelId") Long personnelId,
            @RequestParam("typeDocument") String typeDocument,
            @RequestParam("nomDocument") String nomDocument,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dateExpiration", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            // Validation basique
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner un fichier");
                return "redirect:/documents/upload";
            }

            // Vérifier que le personnel existe
            PersonnelRH personnel = personnelRHService.getPersonnelById(personnelId);

            // Créer le répertoire s'il n'existe pas
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Générer un nom de fichier unique
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            // Sauvegarder le fichier
            Files.write(path, file.getBytes());

            // Créer et sauvegarder le document
            DocumentsRH document = new DocumentsRH();
            document.setPersonnel(personnel);
            document.setTypeDocument(typeDocument);
            document.setNomDocument(nomDocument);
            document.setDescription(description);
            document.setDateExpiration(dateExpiration);
            document.setFilePath(fileName); // Stocker juste le nom du fichier
            document.setIsVerified(false);

            documentArchivingService.saveDocument(document);

            redirectAttributes.addFlashAttribute("success", "Document uploadé avec succès: " + fileName);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'upload: " + e.getMessage());
            return "redirect:/documents/upload";
        }

        return "redirect:/documents";
    }

    @GetMapping("/expiring")
    public String getExpiringDocuments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate threshold,
            Model model) {

        if (threshold == null) {
            threshold = LocalDate.now().plusDays(30);
        }

        List<DocumentsRH> documents = documentArchivingService.getExpiringDocuments(threshold);
        model.addAttribute("documents", documents);
        model.addAttribute("threshold", threshold);

        return "documents/expiring";
    }

    @GetMapping("/unverified")
    public String getUnverifiedDocuments(Model model) {
        List<DocumentsRH> documents = documentArchivingService.getUnverifiedDocuments();
        Long unverifiedCount = documentArchivingService.getUnverifiedDocumentsCount();

        model.addAttribute("documents", documents);
        model.addAttribute("unverifiedCount", unverifiedCount);

        return "documents/unverified";
    }

    // CORRECTION : Changer en GET pour simplifier
    @GetMapping("/verify/{documentId}")
    public String verifyDocument(@PathVariable Long documentId, RedirectAttributes redirectAttributes) {
        try {
            documentArchivingService.verifyDocument(documentId);
            redirectAttributes.addFlashAttribute("success", "Document vérifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la vérification: " + e.getMessage());
        }
        return "redirect:/documents/unverified";
    }

    @GetMapping("/statistics")
    public String getDocumentStatistics(Model model) {
        Long unverifiedCount = documentArchivingService.getUnverifiedDocumentsCount();
        List<Object[]> typeStatistics = documentArchivingService.getDocumentsStatistics();

        model.addAttribute("unverifiedCount", unverifiedCount);
        model.addAttribute("typeStatistics", typeStatistics);

        return "documents/statistics";
    }

    @GetMapping("/download/{documentId}")
    public String downloadDocument(@PathVariable Long documentId) {
        // TODO: Implémenter le téléchargement
        return "redirect:/documents";
    }
}