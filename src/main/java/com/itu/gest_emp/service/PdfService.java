package com.itu.gest_emp.service;

import com.itu.gest_emp.model.Appliance;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateContratEssai(Appliance appliance) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // En-tête
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("CONTRAT D'ESSAI PROFESSIONNEL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Informations du candidat
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            
            document.add(new Paragraph("INFORMATIONS DU CANDIDAT", headerFont));
            document.add(new Paragraph("Nom: " + appliance.getPerson().getNom() + " " + appliance.getPerson().getPrenom(), normalFont));
            document.add(new Paragraph("Contact: " + appliance.getPerson().getContact(), normalFont));
            document.add(new Paragraph(" "));

            // Informations du poste
            document.add(new Paragraph("INFORMATIONS DU POSTE", headerFont));
            if (appliance.getOffer() != null && appliance.getOffer().getPost() != null) {
                document.add(new Paragraph("Poste: " + appliance.getOffer().getPost().getName(), normalFont));
                document.add(new Paragraph("Entreprise: BeerBears", normalFont));
                document.add(new Paragraph("Lieu: " + (appliance.getOffer().getLocation() != null ? appliance.getOffer().getLocation() : "Non spécifié"), normalFont));
            }
            document.add(new Paragraph(" "));

            // Conditions du contrat
            document.add(new Paragraph("CONDITIONS DU CONTRAT", headerFont));
            document.add(new Paragraph("Durée: 3 mois (période d'essai)", normalFont));
            document.add(new Paragraph("Date de début: " + LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph("Date de fin: " + LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph(" "));

            // Clause de confidentialité
            document.add(new Paragraph("CLAUSE DE CONFIDENTIALITÉ", headerFont));
            document.add(new Paragraph("Le candidat s'engage à respecter la confidentialité des informations de l'entreprise durant et après la période d'essai.", normalFont));
            document.add(new Paragraph(" "));

            // Signature
            document.add(new Paragraph("Fait à Antananarivo le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Signature du candidat", normalFont));
            document.add(new Paragraph("_________________________", normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Signature de l'entreprise", normalFont));
            document.add(new Paragraph("_________________________", normalFont));

            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}