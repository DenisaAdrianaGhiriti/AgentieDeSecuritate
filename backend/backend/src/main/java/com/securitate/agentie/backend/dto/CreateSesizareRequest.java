package com.securitate.agentie.backend.dto;

public class CreateSesizareRequest {
    private String titlu;
    private String descriere;

    // --- Getters și Setters ---

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}