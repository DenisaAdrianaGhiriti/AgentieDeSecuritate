package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitate separată care leagă un punct de lucru la o listă de Paznici.
 * Aceasta este necesară pentru a rezolva eroarea de imbricare a ElementCollection (JPA/Hibernate).
 */
@Entity
@Table(name = "assigned_paznici_items")
public class AssignedPazniciItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String punct;

    // Relație Many-to-One către User (Beneficiar) - campul "beneficiary" este folosit în User.java -> mappedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id")
    private User beneficiary;

    // Relație Many-to-Many cu User (Paznic)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "punct_paznici", // Tabelul intermediar
            joinColumns = @JoinColumn(name = "assigned_item_id"),
            inverseJoinColumns = @JoinColumn(name = "paznic_id")
    )
    private List<User> paznici = new ArrayList<>();

    // --- Constructor gol (JPA) ---
    public AssignedPazniciItem() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPunct() { return punct; }
    public void setPunct(String punct) { this.punct = punct; }

    public User getBeneficiary() { return beneficiary; }
    public void setBeneficiary(User beneficiary) { this.beneficiary = beneficiary; }

    public List<User> getPaznici() { return paznici; }
    public void setPaznici(List<User> paznici) { this.paznici = paznici; }
}