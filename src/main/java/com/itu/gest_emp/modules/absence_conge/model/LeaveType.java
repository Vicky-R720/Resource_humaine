package com.itu.gest_emp.modules.absence_conge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_types_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "requires_justification")
    private Boolean requiresJustification = false;

    @Column(name = "is_paid")
    private Boolean isPaid = true;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters et setters...
}
