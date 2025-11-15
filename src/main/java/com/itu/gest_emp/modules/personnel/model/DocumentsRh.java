package com.itu.gest_emp.modules.personnel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.itu.gest_emp.modules.shared.model.Person;

@Entity
@Table(name = "documents_rh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsRh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;
    
    @Column(nullable = false, length = 100)
    private String typeDocument;
    
    @Column(nullable = false, length = 255)
    private String nomDocument;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 500)
    private String filePath;
    
    private LocalDate dateExpiration;
    
    private Boolean isVerified = false;
    
    @ManyToOne
    @JoinColumn(name = "verified_by")
    private Person verifiedBy;
    
    private LocalDateTime verifiedAt;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}