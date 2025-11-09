package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.Role;

public class AuthResponse {
    private Long _id;
    private String email;
    private String nume;
    private Role role;
    private String token;

    public AuthResponse(Long _id, String email, String nume, Role role, String token) {
        this._id = _id;
        this.email = email;
        this.nume = nume;
        this.role = role;
        this.token = token;
    }

    // --- ADaugă ACESTE METODE ---

    public Long get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String getNume() {
        return nume;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    // --- (Poți adăuga și Settere dacă vrei, dar Getters sunt obligatorii pentru JSON) ---

    // public void set_id(Long _id) { this._id = _id; }
    // public void setEmail(String email) { this.email = email; }
    // public void setNume(String nume) { this.nume = nume; }
    // public void setRole(Role role) { this.role = role; }
    // public void setToken(String token) { this.token = token; }
}