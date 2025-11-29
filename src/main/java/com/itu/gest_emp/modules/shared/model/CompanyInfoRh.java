package com.itu.gest_emp.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_info_rh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfoRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_entreprise", nullable = false)
    private String nomEntreprise;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "secteur_activite", nullable = false)
    private SecteurActiviteEnum secteurActivite = SecteurActiviteEnum.non_agricole;

    @Column(name = "numero_cnaps")
    private String numeroCnaps;

    @Column(name = "numero_ostie")
    private String numeroOstie;

    @Column(name = "numero_nif")
    private String numeroNif;

    @Column(name = "numero_stat")
    private String numeroStat;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
