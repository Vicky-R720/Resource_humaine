package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.itu.gest_emp.modules.absence_conge.dto.CalendarEventDTO;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest.LeaveStatus;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;
import com.itu.gest_emp.modules.shared.model.Service;
import com.itu.gest_emp.modules.shared.repository.ServiceRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/leave-calendar")
public class LeaveCalendarController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private ServiceRepository serviceRepo;

    /**
     * Calendrier visuel des congés par équipe/service
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getServiceCalendar(@PathVariable Long serviceId) {
        try {
            if (serviceId == null || serviceId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de service invalide"));
            }

            Optional<Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Récupération des congés approuvés pour ce service
            List<LeaveRequest> approvedLeaves = leaveRequestRepository
                    .findByStatutAndPersonnel_Post_Equipe_Service_Id(LeaveStatus.APPROUVE, serviceId);

            List<CalendarEventDTO> events = approvedLeaves.stream()
                    .map(this::convertToCalendarEvent)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(events);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Export calendrier (format iCal)
     */
    @GetMapping("/export/ical/{serviceId}")
    public ResponseEntity<?> exportICal(@PathVariable Long serviceId) {
        try {
            if (serviceId == null || serviceId <= 0) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Service> serviceOpt = serviceRepo.findById(serviceId);
            if (serviceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Récupération des congés approuvés pour ce service
            List<LeaveRequest> approvedLeaves = leaveRequestRepository
                    .findByStatutAndPersonnel_Post_Equipe_Service_Id(LeaveStatus.APPROUVE, serviceId);

            String icalContent = generateICalContent(approvedLeaves);

            return ResponseEntity.ok()
                    .header("Content-Type", "text/calendar; charset=utf-8")
                    .header("Content-Disposition",
                            "attachment; filename=\"calendrier-conges-service-" + serviceId + ".ics\"")
                    .body(icalContent);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erreur lors de la génération du fichier iCal"));
        }
    }

    private CalendarEventDTO convertToCalendarEvent(LeaveRequest leaveRequest) {
        CalendarEventDTO event = new CalendarEventDTO();
        event.setId(leaveRequest.getId());
        event.setTitle(leaveRequest.getPersonnel().getPerson().getFullName() + " - " +
                leaveRequest.getLeaveType().getName());
        event.setStart(leaveRequest.getDateDebut());
        event.setEnd(leaveRequest.getDateFin());
        event.setColor(leaveRequest.getLeaveType().getColor());
        event.setDescription(leaveRequest.getMotif());
        return event;
    }

    private String generateICalContent(List<LeaveRequest> leaves) {
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\n");
        ical.append("VERSION:2.0\n");
        ical.append("PRODID:-//Gestion Congés//FR\n");

        for (LeaveRequest leave : leaves) {
            ical.append("BEGIN:VEVENT\n");
            ical.append("SUMMARY:").append(leave.getPersonnel().getPerson().getFullName())
                    .append(" - ").append(leave.getLeaveType().getName()).append("\n");
            ical.append("DTSTART:").append(leave.getDateDebut().toString().replace("-", "")).append("\n");
            ical.append("DTEND:").append(leave.getDateFin().plusDays(1).toString().replace("-", "")).append("\n");
            ical.append("DESCRIPTION:").append(leave.getMotif() != null ? leave.getMotif() : "").append("\n");
            ical.append("END:VEVENT\n");
        }

        ical.append("END:VCALENDAR\n");
        return ical.toString();
    }
}
