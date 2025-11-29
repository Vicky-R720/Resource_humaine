package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itu.gest_emp.modules.absence_conge.dto.LeaveRequestDTO;
import com.itu.gest_emp.modules.absence_conge.dto.RetourCongeRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveTypeRepository;
// import com.itu.gest_emp.modules.absence_conge.service.AbsencePVService;
// import com.itu.gest_emp.modules.absence_conge.service.IndemniteRuptureService;
import com.itu.gest_emp.modules.absence_conge.service.LeaveCalculationService;
import com.itu.gest_emp.modules.absence_conge.service.LeaveWorkflowService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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



    // @Autowired
    // private AbsencePVService absencePVService;

    // @Autowired
    // private IndemniteRuptureService indemniteRuptureService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

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

    // @PostMapping("/declarer-absence")
    // public ResponseEntity<?> declarerAbsenceNonAutorisee(@RequestBody
    // DeclarerAbsenceRequest request) {
    // try {
    // absencePVService.traiterAbsenceNonAutorisee(
    // request.getPersonnelId(),
    // request.getDateAbsence(),
    // request.getMotif());
    // return ResponseEntity.ok().build();
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    // /**
    // * Calculer l'indemnité de congés pour rupture
    // */
    // @GetMapping("/indemnite-rupture/{personnelId}")
    // public ResponseEntity<BigDecimal> calculerIndemniteRupture(
    // @PathVariable Long personnelId,
    // @RequestParam String typeRupture) {
    // try {
    // BigDecimal indemnite =
    // indemniteRuptureService.calculerIndemniteCongeRupture(personnelId,
    // typeRupture);
    // return ResponseEntity.ok(indemnite);
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().build();
    // }
    // }

    @PostMapping("/{requestId}/retour")
    public ResponseEntity<?> enregistrerRetour(
            @PathVariable Long requestId,
            @RequestBody RetourCongeRequest request) {
        try {
            LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

            // Enregistrer le retour effectif
            leaveWorkflowService.enregistrerRetour(leaveRequest,request.getDateRetour(), request.getJustification());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}