package com.itu.gest_emp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
    
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "id_appliance", nullable = false)
    private Appliance appliance;
    
  
    
 
    public Notification(Person person, String message, Appliance id_appliance) {
        this.person = person;
        this.message = message;
        this.appliance = id_appliance;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance id_appliance) {
        this.appliance = id_appliance;
    }




    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    
    // Constructeurs
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Notification(Person person, String message) {
        this();
        this.person = person;
        this.message = message;
  
    }
    
   
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    

    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    

    
  
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
   
}