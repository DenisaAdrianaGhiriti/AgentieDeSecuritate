package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// --- Importuri noi necesare ---
import jakarta.persistence.Embedded;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

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

    // --- CÂMPURI NOI ADĂUGATE (din logica Node.js) ---

    /**
     * Relație Many-to-One către același tabel (User).
     * Specifică ce cont de admin a creat acest utilizator.
     * Este LAZY pentru a nu încărca adminul decât la cerere explicită.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creat_de_admin_id") // Numele coloanei de foreign key în baza de date
    private User creatDeAdmin;

    /**
     * Încorporează câmpurile din clasa Profile (nume_firma, cui, nr_legitimatie etc.)
     * direct în tabelul 'users'.
     */
    @Embedded
    private Profile profile;

    // --- Câmpurile pentru timestamp (rămân neschimbate) ---

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

    // --- Constructor cu câmpurile de bază (Opțional, dar util) ---
    // (Nu am adăugat noile câmpuri aici pentru a nu-l complica excesiv)
    public User(String email, String password, Role role, String nume, String prenume, String telefon, boolean esteActiv) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.esteActiv = esteActiv;
    }

    // --- Implementare Metode UserDetails (pentru Spring Security) ---

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

    // --- GETTERS ȘI SETTERS MANUALE ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public boolean isEsteActiv() {
        return esteActiv;
    }

    public void setEsteActiv(boolean esteActiv) {
        this.esteActiv = esteActiv;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- GETTERS ȘI SETTERS PENTRU CÂMPURILE NOI ADĂUGATE ---

    public User getCreatDeAdmin() {
        return creatDeAdmin;
    }

    public void setCreatDeAdmin(User creatDeAdmin) {
        this.creatDeAdmin = creatDeAdmin;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}