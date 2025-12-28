package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private String prenume;

    private String telefon;

    @Column(columnDefinition = "boolean default true")
    private boolean esteActiv = true;

    @JsonIgnore
    // NOU: Relația inversă pentru Sesizările create de acest User (Beneficiar)
    @OneToMany(mappedBy = "createdByBeneficiary", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Sesizare> sesizariCreate = new ArrayList<>();

    @JsonIgnore
    // NOU: Relația inversă pentru Sesizările atribuite acestui User (Admin)
    @OneToMany(mappedBy = "assignedAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Sesizare> sesizariAtribuite = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creat_de_admin_id")
    private User creatDeAdmin;

    @Embedded
    private Profile profile;

    @JsonIgnore
    // NOU: Relația One-to-Many de la Beneficiar la seturile de alocări
    @OneToMany(mappedBy = "beneficiary", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AssignedPazniciItem> assignedPazniciItems = new ArrayList<>();

    // --- Câmpurile pentru timestamp ---

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Constructor Gol (Cerut de JPA) ---
    public User() {
    }

    // --- Implementare Metode UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.esteActiv;
    }

    // --- GETTERS ȘI SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public boolean isEsteActiv() { return esteActiv; }
    public void setEsteActiv(boolean esteActiv) { this.esteActiv = esteActiv; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getCreatDeAdmin() { return creatDeAdmin; }
    public void setCreatDeAdmin(User creatDeAdmin) { this.creatDeAdmin = creatDeAdmin; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    // Getter/Setter pentru AssignedPazniciItem
    public List<AssignedPazniciItem> getAssignedPazniciItems() {
        return assignedPazniciItems;
    }

    public void setAssignedPazniciItems(List<AssignedPazniciItem> assignedPazniciItems) {
        this.assignedPazniciItems = assignedPazniciItems;
    }

    public List<Sesizare> getSesizariCreate() {
        return sesizariCreate;
    }

    public void setSesizariCreate(List<Sesizare> sesizariCreate) {
        this.sesizariCreate = sesizariCreate;
    }

    public List<Sesizare> getSesizariAtribuite() {
        return sesizariAtribuite;
    }

    public void setSesizariAtribuite(List<Sesizare> sesizariAtribuite) {
        this.sesizariAtribuite = sesizariAtribuite;
    }
}