package com.securitate.agentie.backend.dto;

/**
 * Mapează corpul cererii pentru schimbarea parolei.
 */
public class PasswordUpdateRequest {
    private String newPassword;

    // --- Getters și Setters ---
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}