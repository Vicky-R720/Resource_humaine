package com.itu.gest_emp.modules.personnel.controller.view;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import com.itu.gest_emp.modules.personnel.service.CareerHistoryService;
import com.itu.gest_emp.modules.personnel.service.ContractsRhService;
import com.itu.gest_emp.modules.personnel.service.DocumentsRhService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import com.itu.gest_emp.modules.shared.service.PostService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/personnel")
@RequiredArgsConstructor
public class PersonnelRhViewController {

    private final PersonnelRhService personnelRhService;
    private final ContractsRhService contratService;
    private final CareerHistoryService historiqueService;
    private final DocumentsRhService documentService;
    private final PostService postService;

    // Page liste
    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("personnels", personnelRhService.findAll());
        model.addAttribute("posts", postService.getAllPosts());
        return "modules/personnel/personnel-list";
    }

    // Page creation
    @GetMapping("/new")
    public String createPage(Model model) {
        return "modules/personnel/personnel-create";
    }

    // Page détail
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        PersonnelRh personnel = personnelRhService.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        model.addAttribute("personnel", personnel);
        model.addAttribute("anciennete", personnelRhService.getAnciennete(personnel));
        model.addAttribute("contrats", contratService.findByPersonnelId(id));
        model.addAttribute("historiques", historiqueService.findByPersonnelId(id));
        model.addAttribute("documents", documentService.findByPersonnelId(id));

        return "modules/personnel/personnel-detail";
    }
}
