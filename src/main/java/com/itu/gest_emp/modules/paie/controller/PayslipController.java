package com.itu.gest_emp.modules.paie.controller;

import com.itu.gest_emp.modules.paie.model.PayslipsRh;
import com.itu.gest_emp.modules.paie.service.PayslipService;
import com.itu.gest_emp.modules.paie.service.SalarySnapshotService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/modules/paie")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @Autowired
    private PersonnelRhService personnelRhService;

    @Autowired
    private SalarySnapshotService salarySnapshotService;

    /*
     * --------------------------------------------------
     * PAGE PRINCIPALE – LISTE DES BULLETINS
     * --------------------------------------------------
     */
    @GetMapping("/bulletins")
    public String listBulletins(Model model) {
        model.addAttribute("bulletins", payslipService.findAll());
        model.addAttribute("pageTitle", "Gestion des bulletins de paie");
        return "modules/paie/bulletins";
    }

    /*
     * --------------------------------------------------
     * PAGE DE GENERATION (FORMULAIRE)
     * --------------------------------------------------
     */
    @GetMapping("/generation")
    public String showGenerationForm(Model model) {
        model.addAttribute("personnels", personnelRhService.findAll());
        model.addAttribute("pageTitle", "Génération des bulletins");
        return "modules/paie/generation";
    }

    /*
     * --------------------------------------------------
     * ACTION : GENERER UN BULLETIN
     * --------------------------------------------------
     */
    @PostMapping("/generer-bulletins")
    public String genererBulletins(
            @RequestParam Integer mois,
            @RequestParam Integer annee,
            @RequestParam Long personnelId,
            RedirectAttributes redirectAttributes) {

        try {
            payslipService.generatePayslip(personnelId, mois, annee);
            redirectAttributes.addFlashAttribute("success",
                    "Bulletin généré pour " + mois + "/" + annee + " !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de la génération : " + e.getMessage());
        }

        return "redirect:/modules/paie/generation";
    }

    /*
     * --------------------------------------------------
     * HISTORIQUE DES BULLETINS D'UN EMPLOYE
     * --------------------------------------------------
     */
    @GetMapping("/employe/{id}/bulletins")
    public String bulletinsEmploye(@PathVariable Long id, Model model) {
        List<PayslipsRh> bulletins = payslipService.getPayslipsByPersonnel(id);
        model.addAttribute("bulletins", bulletins);
        model.addAttribute("personnel", personnelRhService.findById(id).orElse(null));
        model.addAttribute("pageTitle", "Historique des bulletins");
        return "modules/paie/historique-employe";
    }

    /*
     * --------------------------------------------------
     * DETAIL D’UN BULLETIN
     * --------------------------------------------------
     */
    @GetMapping("/bulletins/{id}")
    public String viewBulletin(@PathVariable Long id, Model model) {
        PayslipsRh bulletin = payslipService.findById(id);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("snapshot", salarySnapshotService.getSnapshot(
                bulletin.getPersonnel().getId(),
                bulletin.getMois(),
                bulletin.getAnnee()));
        model.addAttribute("anciennete", personnelRhService.getAnciennete(bulletin.getPersonnel()));
        model.addAttribute("lines", bulletin.getLignes()); // détail lignes
        model.addAttribute("pageTitle", "Détail du bulletin");
        return "modules/paie/bulletin-detail";
    }

    /*
     * --------------------------------------------------
     * TELECHARGEMENT PDF
     * --------------------------------------------------
     */
    @GetMapping("/bulletin/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdfBytes = payslipService.generatePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulletin-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /*
     * --------------------------------------------------
     * EXPORT EXCEL
     * --------------------------------------------------
     */
    @GetMapping("/bulletin/{id}/excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable Long id) {
        byte[] excelBytes = payslipService.generateExcel(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulletin-" + id + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    /*
     * --------------------------------------------------
     * SUPPRESSION D’UN BULLETIN
     * --------------------------------------------------
     */
    @PostMapping("/bulletin/{id}/delete")
    public String deleteBulletin(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            payslipService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Bulletin supprimé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Impossible de supprimer : " + e.getMessage());
        }

        return "redirect:/modules/paie/bulletins";
    }
}
