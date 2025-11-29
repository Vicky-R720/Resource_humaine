package com.itu.gest_emp.modules.personnel.controller.view;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.personnel.model.DocumentsRh;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.DocumentsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.personnel.util.FileUploadHelper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/personnel/{personnelId}/documents")
@RequiredArgsConstructor
public class DocumentsRhViewController {

    private final DocumentsRhService documentsRhService;
    private final PersonnelRhService personnelService;

    @GetMapping
    public String listDocuments(@PathVariable Long personnelId, Model model) {
        PersonnelRh personnel = personnelService.findById(personnelId).orElseThrow();
        model.addAttribute("personnel", personnel);
        model.addAttribute("documents", documentsRhService.findByPersonnelId(personnelId));
        return "modules/personnel/documents/list";
    }

    @GetMapping("/upload")
    public String uploadForm(@PathVariable Long personnelId, Model model) {
        model.addAttribute("personnelId", personnelId);
        return "modules/personnel/documents/form";
    }

    @PostMapping("/save")
    public String saveDocument(
            @PathVariable Long personnelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("nomDocument") String nomDocument,
            @RequestParam("typeDocument") String typeDocument,
            @RequestParam(value = "dateExpiration", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration)
            throws IOException {

        PersonnelRh personnel = personnelService.findById(personnelId).orElseThrow();

        // Sauvegarder le fichier avec le helper
        String filePath = FileUploadHelper.save("uploads/documents/", file);

        DocumentsRh document = new DocumentsRh();
        document.setPersonnel(personnel);
        document.setNomDocument(nomDocument);
        document.setTypeDocument(typeDocument);
        document.setDateExpiration(dateExpiration);
        document.setFilePath(filePath); // ✅ chemin généré
        document.setIsVerified(false);

        documentsRhService.save(document);

        return "redirect:/personnel/" + personnelId + "/documents";
    }

    @GetMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long personnelId, @PathVariable Long id) {
        documentsRhService.deleteById(id);
        return "redirect:/personnel/" + personnelId + "/documents";
    }

    @GetMapping("/{id}/verify")
    public String verifyDocument(@PathVariable Long personnelId, @PathVariable Long id) {
        // ex: VERIFIED BY = admin id = 1
        documentsRhService.verifierDocument(id, 1L);
        return "redirect:/personnel/" + personnelId + "/documents";
    }
}
