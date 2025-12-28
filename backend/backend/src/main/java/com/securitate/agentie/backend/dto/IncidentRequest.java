package com.securitate.agentie.backend.dto;

public class IncidentRequest {
    private String titlu;
    private String descriere;
    private String punctDeLucru;
    private Long companieId; // ID-ul beneficiarului asociat (din Node.js: companieId)

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

    public String getPunctDeLucru() {
        return punctDeLucru;
    }

    public void setPunctDeLucru(String punctDeLucru) {
        this.punctDeLucru = punctDeLucru;
    }

    public Long getCompanieId() {
        return companieId;
    }

    public void setCompanieId(Long companieId) {
        this.companieId = companieId;
    }
}