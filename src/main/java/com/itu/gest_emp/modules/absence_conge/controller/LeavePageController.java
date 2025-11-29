package com.itu.gest_emp.modules.absence_conge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/leave")
public class LeavePageController {
    private final String VIEW_BASE_DIR = "modules/absence_conge/";

    public String getViewPath(String s) {
        return VIEW_BASE_DIR + s;
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return getViewPath("calendar"); // Thymeleaf rend /templates/leave/calendar.html
    }

    @GetMapping("/request")
    public String requestFormPage() {
        return getViewPath("request-form");
    }

    @GetMapping("/validate")
    public String validateFormPage() {
        return getViewPath("validate-form");
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return getViewPath("dashboard");
    }

    @GetMapping("/history")
    public String historyPage() {
        return getViewPath("history");
    }
}
