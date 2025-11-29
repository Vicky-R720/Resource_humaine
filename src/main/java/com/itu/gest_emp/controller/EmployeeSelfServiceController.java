package com.itu.gest_emp.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itu.gest_emp.service.*;
import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/self-service")
public class EmployeeSelfServiceController {
    
    private final EmployeeSelfService employeeSelfService;
    private final AuthenticationService authenticationService;
    
    public EmployeeSelfServiceController(EmployeeSelfService employeeSelfService,
                                       AuthenticationService authenticationService) {
        this.employeeSelfService = employeeSelfService;
        this.authenticationService = authenticationService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            
            // CORRECTION : Utiliser java.util.Map explicitement
            java.util.Map<String, Object> leaveSummary = employeeSelfService.getLeaveSummary(personnel.getId());
            List<PayslipsRH> recentPayslips = employeeSelfService.getEmployeePayslips(personnel.getId())
                    .stream()
                    .limit(3)
                    .collect(Collectors.toList());
            
            model.addAttribute("personnel", personnel);
            model.addAttribute("leaveSummary", leaveSummary);
            model.addAttribute("recentPayslips", recentPayslips);
            
            return "employee/dashboard";
            
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/personal-info")
    public String personalInfo(Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            model.addAttribute("personnel", personnel);
            return "employee/personal-info";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/personal-info")
    public String updatePersonalInfo(@ModelAttribute PersonnelRh personnelInfo, 
                                   RedirectAttributes redirectAttributes) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            employeeSelfService.updatePersonalInfo(personnel.getId(), personnelInfo);
            redirectAttributes.addFlashAttribute("success", "Informations mises à jour avec succès");
            return "redirect:/employee/self-service/personal-info";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/leaves")
    public String leaves(Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            
            List<LeaveRequestsRH> leaveRequests = employeeSelfService.getEmployeeLeaveRequests(personnel.getId());
            List<LeaveBalanceRH> leaveBalances = employeeSelfService.getLeaveBalances(personnel.getId());
            
            model.addAttribute("personnel", personnel);
            model.addAttribute("leaveRequests", leaveRequests);
            model.addAttribute("leaveBalances", leaveBalances);
            model.addAttribute("newLeaveRequest", new LeaveRequestsRH());
            
            return "employee/leaves";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/leaves/submit")
    public String submitLeaveRequest(@ModelAttribute LeaveRequestsRH leaveRequest,
                                   RedirectAttributes redirectAttributes) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            leaveRequest.setPersonnel(personnel);
            employeeSelfService.submitLeaveRequest(leaveRequest);
            redirectAttributes.addFlashAttribute("success", "Demande de congé soumise avec succès");
            return "redirect:/employee/self-service/leaves";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/leaves/cancel/{id}")
    public String cancelLeaveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeSelfService.cancelLeaveRequest(id);
            redirectAttributes.addFlashAttribute("success", "Demande de congé annulée");
            return "redirect:/employee/self-service/leaves";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/payslips")
    public String payslips(Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            List<PayslipsRH> payslips = employeeSelfService.getEmployeePayslips(personnel.getId());
            
            model.addAttribute("personnel", personnel);
            model.addAttribute("payslips", payslips);
            
            return "employee/payslips";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/payslips/{mois}/{annee}")
    public String viewPayslip(@PathVariable Integer mois, @PathVariable Integer annee, Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            PayslipsRH payslip = employeeSelfService.getPayslip(personnel.getId(), mois, annee)
                    .orElseThrow(() -> new RuntimeException("Bulletin de paie non trouvé"));
            
            model.addAttribute("personnel", personnel);
            model.addAttribute("payslip", payslip);
            
            return "employee/payslip-details";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/requests")
    public String requests(Model model) {
        try {
            PersonnelRh personnel = authenticationService.getCurrentPersonnel();
            List<EmployeeRequestsRH> employeeRequests = employeeSelfService.getEmployeeRequests(personnel.getId());
            
            model.addAttribute("personnel", personnel);
            model.addAttribute("requests", employeeRequests);
            model.addAttribute("newRequest", new EmployeeRequestsRH());
            
            return "employee/requests";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    @PostMapping("/requests/submit")
    public String submitEmployeeRequest(@ModelAttribute EmployeeRequestsRH employeeRequest,
                                      RedirectAttributes redirectAttributes) {
        try {
            PersonnelRH personnel = authenticationService.getCurrentPersonnel();
            employeeRequest.setPersonnel(personnel);
            employeeSelfService.submitEmployeeRequest(employeeRequest);
            redirectAttributes.addFlashAttribute("success", "Demande soumise avec succès");
            return "redirect:/employee/self-service/requests";
        } catch (RuntimeException e) {
            return "redirect:/auth/login";
        }
    }
    
    // Ajouter le lien de déconnexion dans le header
    @ModelAttribute("currentUser")
    public PersonnelRh getCurrentUser() {
        try {
            return authenticationService.getCurrentPersonnel();
        } catch (Exception e) {
            return null;
        }
    }
}