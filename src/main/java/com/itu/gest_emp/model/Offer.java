package com.itu.gest_emp.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers")
public class Offer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @Column(name = "company_name", nullable = true, length = 255)
    private String companyName;
    
    @Column(name = "location", nullable = false, length = 255)
    private String location;
    
    @ManyToOne
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;
    
    @Column(name = "required_profile", columnDefinition = "TEXT")
    private String requiredProfile;
    
    @Column(name = "experience_level", length = 100)
    private String experienceLevel;
    
    @Column(name = "diploma", length = 100)
    private String diploma;

    @Column(name = "filiere", length = 100)
    private String filiere;
    
    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    @Column(name = "available_places")
    private Integer availablePlaces = 1;
    
    @Column(name = "publication_date")
    private LocalDateTime publicationDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Offer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.publicationDate = LocalDateTime.now();
    }
    
    public Offer(Post post, String companyName, String location, ContractType contractType, 
                String requiredProfile, String experienceLevel, String diploma, Integer availablePlaces,String filiere) {
        this();
        this.post = post;
        this.companyName = companyName;
        this.location = location;
        this.contractType = contractType;
        this.requiredProfile = requiredProfile;
        this.experienceLevel = experienceLevel;
        this.diploma = diploma;
        this.availablePlaces = availablePlaces;
        this.filiere=filiere;
    }
    public Boolean isDispo(){
        return availablePlaces!=0;
    }
    public static List<Offer> offreDispo(List<Offer> offer){
        List<Offer> result=new ArrayList<Offer>();
        for (Offer offer2 : result) {
            if(offer2.isDispo()){
                result.add(offer2);
            }
        }
        return result;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    
    public String getRequiredProfile() { return requiredProfile; }
    public void setRequiredProfile(String requiredProfile) { this.requiredProfile = requiredProfile; }
    
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    
    public String getDiploma() { return diploma; }
    public void setDiploma(String diploma) { this.diploma = diploma; }
    
    public Integer getAvailablePlaces() { return availablePlaces; }
    public void setAvailablePlaces(Integer availablePlaces) { this.availablePlaces = availablePlaces; }
    
    public LocalDateTime getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDateTime publicationDate) { this.publicationDate = publicationDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", post=" + post +
                ", companyName='" + companyName + '\'' +
                ", location='" + location + '\'' +
                ", contractType=" + contractType +
                ", availablePlaces=" + availablePlaces +
                ", publicationDate=" + publicationDate +
                '}';
    }
}