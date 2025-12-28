package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.Profile; // Ai nevoie de Profile

/**
 * Mapează corpul cererii pentru actualizarea unui utilizator (de către Admin).
 */
public class UserUpdateRequest {
    private String nume;
    private String prenume;
    private String telefon;
    private String email;
    private Boolean esteActiv;
    private Role role;
    private Profile profile; // Obiectul Profile poate conține actualizări specifice

    // --- Getters și Setters ---
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getEsteActiv() { return esteActiv; }
    public void setEsteActiv(Boolean esteActiv) { this.esteActiv = esteActiv; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}