package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payslip_lines_rh")
public class PayslipLinesRh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payslip_id")
    private PayslipsRh payslip;

    private String code; // ex: SB, HS25, INDT, COT_CNAPS
    private String label; // description
    private String type; // GAIN, RETENUE, EMPLOYER

    @Column(precision = 15, scale = 2)
    private BigDecimal quantite = BigDecimal.ONE;

    @Column(precision = 15, scale = 2)
    private BigDecimal taux = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal montant;

    private Integer ordre = 0;
}
