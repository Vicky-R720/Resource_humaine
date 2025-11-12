package com.itu.statistique.model;

import java.math.BigDecimal;

/**
 * Modèle pour les indicateurs KPI
 */
public class KPIData {
    private String label;
    private BigDecimal value;
    private String unit;
    private String trend; // "up", "down", "stable"
    private String icon;
    
    public KPIData() {
    }
    
    public KPIData(String label, BigDecimal value, String unit) {
        this.label = label;
        this.value = value;
        this.unit = unit;
    }
    
    public KPIData(String label, BigDecimal value, String unit, String trend, String icon) {
        this.label = label;
        this.value = value;
        this.unit = unit;
        this.trend = trend;
        this.icon = icon;
    }

    // Getters et Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}