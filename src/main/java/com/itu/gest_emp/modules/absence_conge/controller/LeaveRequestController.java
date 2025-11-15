package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveTypeRepository;
import com.itu.gest_emp.modules.absence_conge.service.LeaveCalculationService;
import com.itu.gest_emp.modules.absence_conge.service.LeaveWorkflowService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveWorkflowService leaveWorkflowService;

    @Autowired
    private LeaveCalculationService calculationService;

    @Autowired
    private PersonnelRhService personnelRhService;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private final String UPLOAD_DIR = "uploads/justificatifs/";

    /**
     * Soumettre une demande de congé
     */
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> submitLeaveRequest(
            @RequestPart("leaveRequestDTO") LeaveRequestDTO leaveRequestDTO,
            @RequestPart(value = "justificatif", required = false) MultipartFile justificatif) {

        try {
            LeaveRequest leaveRequest = convertToEntity(leaveRequestDTO);

            // Calcul automatique du nombre de jours
            BigDecimal nombreJours = calculationService.calculateWorkingDays(
                    leaveRequest.getDateDebut(),
                    leaveRequest.getDateFin());
            leaveRequest.setNombreJours(nombreJours);

            // Gestion du justificatif
            if (justificatif != null && !justificatif.isEmpty()) {
                String filePath = saveJustificatif(justificatif);
                leaveRequest.setJustificatifPath(filePath);
            }

            LeaveRequest savedRequest = leaveWorkflowService.submitLeaveRequest(leaveRequest);
            return ResponseEntity.ok(savedRequest);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private LeaveRequest convertToEntity(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = new LeaveRequest();
        // Remplir les champs de leaveRequest à partir de leaveRequestDTO
        leaveRequest.setDateDebut(leaveRequestDTO.getDateDebut());
        leaveRequest.setDateFin(leaveRequestDTO.getDateFin());
        leaveRequest.setMotif(leaveRequestDTO.getMotif());
        leaveRequest.setPersonnel(
                personnelRhService.findById(leaveRequestDTO.getPersonnelId())
                        .orElseThrow(() -> new RuntimeException(
                                "Personnel introuvable : " + leaveRequestDTO.getPersonnelId())));

        leaveRequest.setLeaveType(
                leaveTypeRepository.findById(leaveRequestDTO.getLeaveTypeId())
                        .orElseThrow(() -> new RuntimeException(
                                "Type de congé introuvable : " + leaveRequestDTO.getLeaveTypeId())));

        return leaveRequest;
    }

    /**
     * Valider/Refuser une demande
     */
    @PostMapping("/{requestId}/validate")
    public ResponseEntity<?> validateLeaveRequest(
            @PathVariable Long requestId,
            @RequestParam String action, // "approve" ou "reject"
            @RequestParam Long validatorId,
            @RequestParam(required = false) String comment) {
        try {
            LeaveRequest result;
            if ("approve".equals(action)) {
                result = leaveWorkflowService.approveLeaveRequest(requestId, validatorId, comment);
            } else {
                result = leaveWorkflowService.rejectLeaveRequest(requestId, validatorId, comment);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String saveJustificatif(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    // DTO pour la soumission des demandes
    public static class LeaveRequestDTO {
        private Long personnelId;
        private Long leaveTypeId;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String motif;

        public Long getPersonnelId() {
            return personnelId;
        }

        public void setPersonnelId(Long personnelId) {
            this.personnelId = personnelId;
        }

        public Long getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(Long leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public LocalDate getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDate dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDate getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDate dateFin) {
            this.dateFin = dateFin;
        }

        public String getMotif() {
            return motif;
        }

        public void setMotif(String motif) {
            this.motif = motif;
        }

        // Getters et setters...
    }
}