package com.itu.gest_emp.modules.paie.controller.api;

import java.math.BigDecimal;

public class AvanceRequestDto {
    private Long personnelId;
    private BigDecimal montant;
    private String motif;

    public Long getPersonnelId() { return personnelId; }
    public BigDecimal getMontant() { return montant; }
    public String getMotif() { return motif; }

    public void setPersonnelId(Long personnelId) { this.personnelId = personnelId; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public void setMotif(String motif) { this.motif = motif; }
}
