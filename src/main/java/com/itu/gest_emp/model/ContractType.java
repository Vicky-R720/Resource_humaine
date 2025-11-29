package com.itu.gest_emp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contract_types")
public class ContractType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;
    
    // Constructeurs
    public ContractType() {}
    
    public ContractType(String name) {
        this.name = name;
    }
    
    public ContractType(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "ContractType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}