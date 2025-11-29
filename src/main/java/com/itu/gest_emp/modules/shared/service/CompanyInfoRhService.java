package com.itu.gest_emp.modules.shared.service;

import com.itu.gest_emp.modules.shared.model.CompanyInfoRh;
import com.itu.gest_emp.modules.shared.model.SecteurActiviteEnum;
import com.itu.gest_emp.modules.shared.repository.CompanyInfoRhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyInfoRhService {

    private final CompanyInfoRhRepository repository;

    public CompanyInfoRh getCompanyInfo() {
        CompanyInfoRh info = repository.findTopByOrderByIdDesc();
        if (info == null) {
            // Valeurs par défaut
            return CompanyInfoRh.builder()
                    .nomEntreprise("Entreprise Non Définie")
                    .secteurActivite(SecteurActiviteEnum.non_agricole)
                    .build();
        }
        return info;
    }

    public SecteurActiviteEnum getSecteurActivite() {
        return getCompanyInfo().getSecteurActivite();
    }
}
