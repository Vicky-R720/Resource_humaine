package com.itu.gest_emp.modules.temps_presence.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itu.gest_emp.modules.temps_presence.service.AttendanceService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.temps_presence.dto.attendance.*;
import com.itu.gest_emp.modules.temps_presence.model.AttendanceRh;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/temps-presence/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final PersonnelRhService personnelRhService;
    private final AttendanceService attendanceService;

    @GetMapping
    public String listAttendance(Model model,
            @RequestParam(required = false) Long personnelId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<AttendanceRh> attendances;
        if (personnelId != null) {
            attendances = attendanceService.getAttendanceByPersonnel(personnelId, start, end);
        } else {
            attendances = attendanceService.getAllAttendanceBetweenDates(start, end);
        }

        model.addAttribute("attendances", attendances);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("personnels", personnelRhService.findAll());
        return "modules/temps_presence/attendance-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("attendance", new AttendanceDto());
        model.addAttribute("personnels", personnelRhService.findAll());
        return "modules/temps_presence/attendance-form";
    }

    @PostMapping("/create")
    public String createAttendance(@ModelAttribute AttendanceDto dto, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.createAttendance(dto);
            redirectAttributes.addFlashAttribute("success", "Pointage créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/temps-presence/attendance";
    }

    @PostMapping("/pointage")
    @ResponseBody
    public String pointageMobile(@RequestBody PointageRequest request) {
        try {
            attendanceService.pointage(request);
            return "Pointage enregistré";
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }

    @GetMapping("/retards")
    public String listRetards(Model model,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<AttendanceRh> retards = attendanceService.getRetards(start, end);
        model.addAttribute("retards", retards);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        return "modules/temps_presence/retards-list";
    }

    @PostMapping("/import-csv")
    public String importCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.importBadgeuseCSV(file);
            redirectAttributes.addFlashAttribute("success", "Import CSV réussi");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur import: " + e.getMessage());
        }
        return "redirect:/temps-presence/attendance";
    }
}