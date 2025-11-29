package com.itu.gest_emp.modules.paie.controller.view;

import com.itu.gest_emp.modules.paie.model.Avance;
import com.itu.gest_emp.modules.paie.service.AvanceService;
import com.itu.gest_emp.modules.paie.service.SalarySnapshotService;
import com.itu.gest_emp.modules.personnel.service.PersonnelRhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/modules/paie/avances")
@RequiredArgsConstructor
public class AvanceViewController {

    private final AvanceService avanceService;
    private final PersonnelRhService personnelRhService;
    private final SalarySnapshotService salarySnapshotService;

    @GetMapping
    public String list(Model model,
            @RequestParam(required = false) Long personnelId) {
        List<Avance> list;
        if (personnelId != null) {
            list = avanceService.findPendingRequests(); // keep simple: use service filters if needed
        } else {
            list = avanceService.findPendingRequests();
        }

        model.addAttribute("avances", list);
        model.addAttribute("personnels", personnelRhService.findAll());
        return "modules/paie/avances-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("personnels", personnelRhService.findAll());
        return "modules/paie/avance-form";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable Long id,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        Avance a = avanceService.findPendingRequests().stream().filter(x -> x.getId().equals(id)).findFirst()
                .orElse(null);
        if (a == null) {
            // try repository via service methods
            // fallback: return list page
            return "redirect:/modules/paie/avances";
        }

        model.addAttribute("avance", a);

        // optionally add salary snapshot for display if mois/annee provided
        if (mois != null && annee != null) {
            try {
                model.addAttribute("snapshot",
                        salarySnapshotService.getSnapshot(a.getPersonnel().getId(), mois, annee));
            } catch (Exception e) {
                // ignore
            }
        }

        return "modules/paie/avance-detail";
    }
}
