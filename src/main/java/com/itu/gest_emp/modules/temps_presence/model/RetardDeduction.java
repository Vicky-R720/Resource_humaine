package com.itu.gest_emp.modules.temps_presence.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;

@Entity
@Table(name = "retard_deduction")
@Data
public class RetardDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "personnel_id", nullable = false)
    private PersonnelRh personnel;

    @Column(name = "date_pointage", nullable = false)
    private LocalDate datePointage;

    @Column(name = "minutes_retard", nullable = false)
    private Integer minutesRetard;

    @Column(name = "jours_couverts", precision = 6, scale = 2)
    private BigDecimal joursCouverts = BigDecimal.ZERO;

    @Column(name = "montant_deduit", precision = 12, scale = 2)
    private BigDecimal montantDeduit = BigDecimal.ZERO;

    @Column(name = "processed_at")
    private LocalDateTime processedAt = LocalDateTime.now();
}
