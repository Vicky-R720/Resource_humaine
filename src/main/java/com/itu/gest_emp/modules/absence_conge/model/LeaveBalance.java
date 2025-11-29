package com.itu.gest_emp.modules.absence_conge.model;

import com.itu.gest_emp.modules.personnel.model.PersonnelRh;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balance_rh", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "personnel_id", "leave_type_id", "annee" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private PersonnelRh personnel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "solde_initial", precision = 5, scale = 2)
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(name = "solde_acquis", precision = 5, scale = 2)
    private BigDecimal soldeAcquis = BigDecimal.ZERO;

    @Column(name = "solde_pris", precision = 5, scale = 2)
    private BigDecimal soldePris = BigDecimal.ZERO;

    @Column(name = "solde_exceptionnel", precision = 5, scale = 2)
    private BigDecimal soldeExceptionnel = BigDecimal.ZERO; // congés justifiés/non validés

    @Column(name = "solde_restant", precision = 5, scale = 2)
    private BigDecimal soldeRestant = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    

    /**
     * Recalcule le solde restant en tenant compte du cumul max (3 ans)
     */
    public void recalculerSoldesCumules() {
        BigDecimal total = soldeInitial.add(soldeAcquis).subtract(soldePris);
        this.soldeRestant = total;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        recalculerSoldesCumules();
    }
}
