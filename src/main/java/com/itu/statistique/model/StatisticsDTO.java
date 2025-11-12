package com.itu.statistique.model;

// StatisticsDTO.java
public class StatisticsDTO {
    private String label;
    private Long value;
    private String category;
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Constructeurs, getters, setters
    public StatisticsDTO(String label, Long value) {
        this.label = label;
        this.value = value;
    }
    
    public StatisticsDTO(String label, Long value, String category) {
        this.label = label;
        this.value = value;
        this.category = category;
    }
}
