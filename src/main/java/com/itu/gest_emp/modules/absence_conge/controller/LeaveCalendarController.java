package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.itu.gest_emp.modules.absence_conge.model.LeaveRequest;
import com.itu.gest_emp.modules.absence_conge.repository.LeaveRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/leave-calendar")
public class LeaveCalendarController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    /**
     * Calendrier visuel des congés par équipe/service
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<?> getServiceCalendar(@PathVariable Long serviceId) {
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByValidatedBy_IdAndStatut(serviceId, "approuve");
        List<CalendarEventDTO> events = approvedLeaves.stream()
                .map(this::convertToCalendarEvent)
                .collect(Collectors.toList());

        return ResponseEntity.ok(events);
    }

    /**
     * Export calendrier (format iCal)
     */
    @GetMapping("/export/ical/{serviceId}")
    public ResponseEntity<?> exportICal(@PathVariable Long serviceId) {
        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findByValidatedBy_IdAndStatut(serviceId, "approuve");
        String icalContent = generateICalContent(approvedLeaves);

        return ResponseEntity.ok()
                .header("Content-Type", "text/calendar")
                .header("Content-Disposition", "attachment; filename=calendrier-conges.ics")
                .body(icalContent);
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

    public static class CalendarEventDTO {
        private Long id;
        private String title;
        private LocalDate start;
        private LocalDate end;
        private String color;
        private String description;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public LocalDate getStart() {
            return start;
        }

        public void setStart(LocalDate start) {
            this.start = start;
        }

        public LocalDate getEnd() {
            return end;
        }

        public void setEnd(LocalDate end) {
            this.end = end;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        // Getters et setters...
    }
}