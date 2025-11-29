package com.itu.gest_emp.model;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;


@Entity
@Table(name = "personnel_rh")
public class PersonnelRH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(unique = true, nullable = false)
    private String matricule;

    private String cin;
    
    @Column(name = "cin_date_delivery")
    private LocalDate cinDateDelivery;
    
    @Column(name = "cin_place_delivery")
    private String cinPlaceDelivery;
    
    private String sexe;
    
    @Column(name = "situation_familiale")
    private String situationFamiliale;
    
    @Column(name = "nombre_enfants")
    private Integer nombreEnfants = 0;
    
    private String nationalite = "Malagasy";
    
    @Column(name = "lieu_naissance")
    private String lieuNaissance;
    
    @Column(name = "personne_urgence_nom")
    private String personneUrgenceNom;
    
    @Column(name = "personne_urgence_contact")
    private String personneUrgenceContact;
    
    @Column(name = "personne_urgence_lien")
    private String personneUrgenceLien;
    
    private String rib;
    private String banque;
    
    @Column(name = "numero_cnaps")
    private String numeroCnaps;
    
    @Column(name = "numero_ostie")
    private String numeroOstie;
    
    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;
    
    @Column(name = "date_sortie")
    private LocalDate dateSortie;
    
    @Column(name = "motif_sortie")
    private String motifSortie;
    
    private String statut = "actif";

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public LocalDate getCinDateDelivery() { return cinDateDelivery; }
    public void setCinDateDelivery(LocalDate cinDateDelivery) { this.cinDateDelivery = cinDateDelivery; }
    public String getCinPlaceDelivery() { return cinPlaceDelivery; }
    public void setCinPlaceDelivery(String cinPlaceDelivery) { this.cinPlaceDelivery = cinPlaceDelivery; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(String situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public Integer getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(Integer nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
    public String getPersonneUrgenceNom() { return personneUrgenceNom; }
    public void setPersonneUrgenceNom(String personneUrgenceNom) { this.personneUrgenceNom = personneUrgenceNom; }
    public String getPersonneUrgenceContact() { return personneUrgenceContact; }
    public void setPersonneUrgenceContact(String personneUrgenceContact) { this.personneUrgenceContact = personneUrgenceContact; }
    public String getPersonneUrgenceLien() { return personneUrgenceLien; }
    public void setPersonneUrgenceLien(String personneUrgenceLien) { this.personneUrgenceLien = personneUrgenceLien; }
    public String getRib() { return rib; }
    public void setRib(String rib) { this.rib = rib; }
    public String getBanque() { return banque; }
    public void setBanque(String banque) { this.banque = banque; }
    public String getNumeroCnaps() { return numeroCnaps; }
    public void setNumeroCnaps(String numeroCnaps) { this.numeroCnaps = numeroCnaps; }
    public String getNumeroOstie() { return numeroOstie; }
    public void setNumeroOstie(String numeroOstie) { this.numeroOstie = numeroOstie; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }
    public String getMotifSortie() { return motifSortie; }
    public void setMotifSortie(String motifSortie) { this.motifSortie = motifSortie; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}