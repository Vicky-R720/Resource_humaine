package com.itu.gest_emp.modules.personnel.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itu.gest_emp.modules.personnel.model.CareerHistoryRh;
import com.itu.gest_emp.modules.personnel.service.CareerHistoryService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/personnel/{personnelId}/career-history")
@RequiredArgsConstructor
public class CareerHistoryViewController {

    private final CareerHistoryService careerHistoryService;
    private final PersonnelRhService personnelService;

    @GetMapping
    public String listCareerHistory(
            @PathVariable Long personnelId,
            Model model) {
        model.addAttribute("personnel", personnelService.findById(personnelId).orElseThrow());
        model.addAttribute("careerHistory", careerHistoryService.findByPersonnelId(personnelId));
        return "modules/personnel/career-history/list";
    }

    @GetMapping("/new")
    public String newCareerHistoryForm(
            @PathVariable Long personnelId,
            Model model) {
        model.addAttribute("personnelId", personnelId);
        model.addAttribute("careerHistory", new CareerHistoryRh());
        return "modules/personnel/career-history/form";
    }

    @PostMapping("/save")
    public String saveCareerHistory(
            @PathVariable Long personnelId,
            @ModelAttribute CareerHistoryRh careerHistory) {
        careerHistory.setPersonnel(personnelService.findById(personnelId).orElseThrow());
        careerHistoryService.save(careerHistory);
        return "redirect:/personnel/" + personnelId + "/career-history";
    }

    @GetMapping("/{id}/delete")
    public String deleteCareerHistory(
            @PathVariable Long personnelId,
            @PathVariable Long id) {
        careerHistoryService.deleteById(id);
        return "redirect:/personnel/" + personnelId + "/career-history";
    }
}
