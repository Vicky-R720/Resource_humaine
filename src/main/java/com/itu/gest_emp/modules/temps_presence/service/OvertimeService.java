package com.itu.gest_emp.modules.temps_presence.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itu.gest_emp.modules.temps_presence.model.OvertimeRh;
import com.itu.gest_emp.modules.temps_presence.model.TypeHs;
import com.itu.gest_emp.modules.temps_presence.dto.overtime.OvertimeCreateDto;
import com.itu.gest_emp.modules.temps_presence.dto.overtime.OvertimeValidationDto;
import com.itu.gest_emp.modules.temps_presence.repository.OvertimeRepository;
import com.itu.gest_emp.modules.temps_presence.repository.AttendanceRepository;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.repository.PersonnelRhRepository;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.shared.model.Utilisateur;
import com.itu.gest_emp.modules.shared.service.NotificationRhService;
import com.itu.gest_emp.modules.shared.service.UtilisateurService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final OvertimeRepository overtimeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PersonnelRhRepository personnelRepository;
    private final NotificationRhService notificationRhService;
    private final UtilisateurService utilisateurService;
    private final TypeHsService typeHsService;
    private final ContractsRhService contractsRhService;
    private final com.itu.gest_emp.modules.paie.service.SalarySnapshotService salarySnapshotService;

    

    // === CRUD OVERTIME ===

    public ContractsRhService getContractsRhService() {
        return contractsRhService;
    }

    public TypeHsService getTypeHsService() {
        return typeHsService;
    }

    public List<OvertimeRh> getOvertimeByPersonnel(Long personnelId) {
        return overtimeRepository.findByPersonnel_Id(personnelId);
    }

    public List<OvertimeRh> getOvertimeEnAttente() {
        return overtimeRepository.findByStatut("en_attente");
    }

    public LocalDate getStartOfWeek(LocalDate date) {
        return date.with(java.time.DayOfWeek.MONDAY);
    }

    public LocalDate getEndOfWeek(LocalDate date) {
        return date.with(java.time.DayOfWeek.SUNDAY);
    }

    public BigDecimal calculMontantHs(PersonnelRh personnel, LocalDate dateHs, BigDecimal heuresAjoutees, TypeHs typeHs,
            BigDecimal TH) {

        // Cas spéciaux → simple multiplication
        if (typeHs.getTauxMajoration() != null) {
            return TH.multiply(typeHs.getTauxMajoration()).multiply(heuresAjoutees);
        }

        // Cas NORMAL → règles des 8h puis 12h

        LocalDate startWeek = dateHs.with(java.time.DayOfWeek.MONDAY);
        LocalDate endWeek = dateHs.with(java.time.DayOfWeek.SUNDAY);

        // Récupérer HS déjà validées cette semaine
        List<OvertimeRh> validated = overtimeRepository
                .findByPersonnel_IdAndDateHsBetweenAndStatut(
                        personnel.getId(), startWeek, endWeek, "approuve");

        BigDecimal totalBefore = validated.stream()
                .map(OvertimeRh::getNombreHeures)
                .filter(h -> h != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = heuresAjoutees;
        BigDecimal montant = BigDecimal.ZERO;

        BigDecimal max8 = BigDecimal.valueOf(8);
        BigDecimal seuil8Restant = BigDecimal.ZERO;

        // Déterminer combien des 8 premières heures sont déjà faites
        if (totalBefore.compareTo(max8) < 0) {

            seuil8Restant = max8.subtract(totalBefore);

            BigDecimal heuresAPrix30 = remaining.min(seuil8Restant);

            montant = montant.add(
                    heuresAPrix30.multiply(TH).multiply(BigDecimal.valueOf(1.3)));

            remaining = remaining.subtract(heuresAPrix30);
        }

        // Heures restantes → taux 1.5
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            montant = montant.add(
                    remaining.multiply(TH).multiply(BigDecimal.valueOf(1.5)));
        }

        return montant;
    }

    @Transactional
    public OvertimeRh createOvertime(OvertimeCreateDto dto) {
        PersonnelRh personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        com.itu.gest_emp.modules.paie.model.PersonnelSalarySnapshot snapshot = salarySnapshotService.getSnapshot(personnel.getId(), dto.getDateHs().getMonthValue(), dto.getDateHs().getYear());
        OvertimeRh overtime = new OvertimeRh();
        overtime.setPersonnel(personnel);
        dto.setSalaireHoraireBase(snapshot.getTauxHoraire());

        if (dto.getAttendanceId() != null) {
            attendanceRepository.findById(dto.getAttendanceId()).ifPresent(overtime::setAttendance);
        }

        TypeHs typeHs = typeHsService.findById(dto.getTypeHsId());
        overtime.setDateHs(dto.getDateHs());
        overtime.setNombreHeures(dto.getNombreHeures());
        overtime.setTypeHs(typeHs);
        overtime.setSalaireHoraireBase(dto.getSalaireHoraireBase());

            BigDecimal montant = calculMontantHs(
                personnel,
                dto.getDateHs(),
                dto.getNombreHeures(),
                typeHs,
                dto.getSalaireHoraireBase());

        overtime.setMontantHs(montant);

        overtime.setStatut("en_attente");

        OvertimeRh savedOvertime = overtimeRepository.save(overtime);

        // Notification au manager
        Utilisateur manager = findManagerOfPersonnel(personnel);
        if (manager != null) {
            notificationRhService.createNotification(
                    manager,
                    utilisateurService.findByPersonId(personnel.getId()),
                    "Demande d'heures supplémentaires en attente",
                    String.format("%s %s a soumis une demande d'heures supplémentaires pour le %s (%s heures)",
                            personnel.getPerson().getPrenom(),
                            personnel.getPerson().getNom(),
                            overtime.getDateHs(),
                            overtime.getNombreHeures()),
                    "overtime_pending",
                    "OvertimeRh",
                    overtime.getId());
        }

        return savedOvertime;
    }

    @Transactional
    public OvertimeRh validerOvertime(Long overtimeId, OvertimeValidationDto validationDto, Long validatorId)
            throws Exception {
        OvertimeRh overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("Heures supplémentaires non trouvées"));

        Utilisateur validator = utilisateurService.findById(validatorId);
        overtime.setValidatedBy(validator);
        overtime.setValidationDate(LocalDateTime.now());

        Utilisateur employee = utilisateurService.findByPersonId(overtime.getPersonnel().getPerson().getId());

        if (validationDto.getApprouve()) {
            LocalDate date = overtime.getDateHs();
            LocalDate start = getStartOfWeek(date);
            LocalDate end = getEndOfWeek(date);

            List<OvertimeRh> validatedHours = overtimeRepository
                    .findByPersonnel_IdAndDateHsBetweenAndStatut(
                            overtime.getPersonnel().getId(),
                            start,
                            end,
                            "approuve");

            BigDecimal total = BigDecimal.ZERO;
            for (OvertimeRh hs : validatedHours) {
                if (hs.getNombreHeures() != null) {
                    total = total.add(hs.getNombreHeures());
                }
            }

            total = total.add(overtime.getNombreHeures());

            if (total.compareTo(new BigDecimal(20)) > 0) {
                throw new RuntimeException("Limite de 20 heures supplémentaires par semaine dépassée");
            }
            BigDecimal montant = calculMontantHs(
                    overtime.getPersonnel(),
                    overtime.getDateHs(),
                    overtime.getNombreHeures(),
                    overtime.getTypeHs(),
                    overtime.getSalaireHoraireBase());
            overtime.setMontantHs(montant);

            // Approuvé
            overtime.setStatut("approuve");
            notificationRhService.createNotification(
                    employee,
                    validator,
                    "Heures supplémentaires approuvées",
                    String.format("Vos heures supplémentaires du %s (%s heures) ont été approuvées. Montant: %s €",
                            overtime.getDateHs(),
                            overtime.getNombreHeures(),
                            overtime.getMontantHs() != null ? overtime.getMontantHs() : 0),
                    "overtime_approval",
                    "OvertimeRh",
                    overtime.getId());
        } else {
            overtime.setStatut("refuse");
            String message = String.format("Vos heures supplémentaires du %s (%s heures) ont été refusées.",
                    overtime.getDateHs(), overtime.getNombreHeures());
            if (validationDto.getCommentaire() != null && !validationDto.getCommentaire().trim().isEmpty()) {
                message += " Motif: " + validationDto.getCommentaire();
            }
            notificationRhService.createNotification(
                    employee,
                    validator,
                    "Heures supplémentaires refusées",
                    message,
                    "overtime_rejection",
                    "OvertimeRh",
                    overtime.getId());
        }

        return overtimeRepository.save(overtime);
    }

    public List<OvertimeRh> getOvertimeForPaiement(LocalDate startDate, LocalDate endDate) {
        return overtimeRepository.findByDateHsBetweenAndStatut(startDate, endDate, "approuve");
    }

    public List<OvertimeRh> getAllOvertime() {
        return overtimeRepository.findAll();
    }

    // === UTILITAIRES ===

    private Utilisateur findManagerOfPersonnel(PersonnelRh personnel) {
        try {
            return personnel.getPost().getEquipe().getService().getManager();
        } catch (Exception e) {
            log.warn("Impossible de trouver un manager pour le personnel {}", personnel.getId());
            return null;
        }
    }
}
