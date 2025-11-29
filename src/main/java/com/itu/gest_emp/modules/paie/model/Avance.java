package com.itu.gest_emp.modules.paie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avance")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Avance {
    @Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "personnel_id")
    private com.itu.gest_emp.modules.personnel.model.PersonnelRh personnel;

    private java.math.BigDecimal montant;

    @jakarta.persistence.Column(columnDefinition = "TEXT")
    private String motif;

    private java.time.LocalDateTime dateDemande = java.time.LocalDateTime.now();

    private java.time.LocalDateTime dateDecision;

    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private AvanceStatus statut = AvanceStatus.DEMANDEE;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "approbateur_id")
    private com.itu.gest_emp.modules.shared.model.Utilisateur approbateur;

    private java.math.BigDecimal montantRembourse = java.math.BigDecimal.ZERO;

    private java.time.LocalDate echeance;

        @JsonManagedReference
        @jakarta.persistence.OneToMany(mappedBy = "avance", cascade = jakarta.persistence.CascadeType.ALL)
        private java.util.List<AvanceAction> actions = new java.util.ArrayList<>();

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

    @jakarta.persistence.PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
