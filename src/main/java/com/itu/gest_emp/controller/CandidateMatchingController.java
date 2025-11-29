package com.itu.gest_emp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itu.gest_emp.model.CandidateMatching;
import com.itu.gest_emp.service.CandidateMatchingService;

@Controller
@RequestMapping("/manager/recruitment")
public class CandidateMatchingController {

    @Autowired
    private CandidateMatchingService matchingService;

    @PostMapping("/match/{offerId}")
    public String matchCandidates(@PathVariable Long offerId, Model model) {
        List<CandidateMatching> matches = matchingService.matchCandidatesWithOffer(offerId);
        model.addAttribute("matches", matches);
        model.addAttribute("offerId", offerId);
        return "manager/recruitment/matching-results";
    }
}
