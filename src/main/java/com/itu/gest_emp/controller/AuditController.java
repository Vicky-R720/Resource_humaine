package com.itu.gest_emp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.itu.gest_emp.model.AuditLogRH;
import com.itu.gest_emp.service.AuditService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public String getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<AuditLogRH> auditLogs = auditService.getAuditLogs(PageRequest.of(page, size));
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auditLogs.getTotalPages());

        return "audit/logs";
    }

    @GetMapping("/user/{userId}")
    public String getAuditLogsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Page<AuditLogRH> auditLogs = auditService.getAuditLogsByUser(userId, PageRequest.of(page, size));
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("userId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auditLogs.getTotalPages());

        return "audit/user-logs";
    }

    @GetMapping("/trail")
    public String getAuditTrail(
            @RequestParam String tableName,
            @RequestParam Long recordId,
            Model model) {

        List<AuditLogRH> auditTrail = auditService.getAuditTrail(tableName, recordId);
        model.addAttribute("auditTrail", auditTrail);
        model.addAttribute("tableName", tableName);
        model.addAttribute("recordId", recordId);

        return "audit/trail";
    }

    @GetMapping("/search")
    public String searchAuditLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Model model) {

        List<AuditLogRH> auditLogs = auditService.getAuditLogsByPeriod(start, end);
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "audit/search-results";
    }

    @GetMapping("/statistics")
    public String getAuditStatistics(Model model) {
        List<String> auditedTables = auditService.getAuditedTables();
        LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
        LocalDateTime monthStart = LocalDateTime.now().minusMonths(1);

        Long weeklyCount = auditService.getAuditCountByPeriod(weekStart, LocalDateTime.now());
        Long monthlyCount = auditService.getAuditCountByPeriod(monthStart, LocalDateTime.now());

        model.addAttribute("auditedTables", auditedTables);
        model.addAttribute("weeklyCount", weeklyCount);
        model.addAttribute("monthlyCount", monthlyCount);

        return "audit/statistics";
    }
}
