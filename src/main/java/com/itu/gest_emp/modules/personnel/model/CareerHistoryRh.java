package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.shared.model.Person;
import com.itu.gest_emp.modules.shared.model.Post;

@Entity
@Table(name = "career_history_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareerHistoryRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
    @Column(nullable = false, length = 50)
    private String typeMouvement;
    
    @Column(length = 255)
    private String ancienPoste;
    
    @Column(length = 255)
    private String nouveauPoste;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal ancienSalaire;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal nouveauSalaire;
    
    @Column(nullable = false)
    private LocalDate dateMouvement;
    
    @Column(columnDefinition = "TEXT")
    private String motif;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Person createdBy;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}