package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.Profile;
import com.securitate.agentie.backend.model.Role;

// Acest DTO mapează corpul cererii (req.body) din user.controller.js
public class CreateUserRequest {
    private String email;
    private String password;
    private Role role;
    private String nume;
    private String prenume;
    private String telefon; // Am adăugat și telefon, chiar dacă nu era în controller
    private Profile profile; // Primește direct obiectul profile

    // --- Getters și Setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}