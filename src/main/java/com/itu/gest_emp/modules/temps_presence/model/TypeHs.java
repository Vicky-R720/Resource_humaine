package com.itu.gest_emp.modules.temps_presence.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "type_hs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeHs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exemple: "weekend", "nuit", "ferie", etc.
    @Column(nullable = false, unique = true)
    private String code;

    // Description optionnelle
    private String description;

    // Taux de majoration (ex: 1.0 = 100%, 0.3 = 30%, 2.0 = 200%)
    private BigDecimal tauxMajoration;
}
