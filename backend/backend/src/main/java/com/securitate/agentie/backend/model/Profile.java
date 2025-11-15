package com.securitate.agentie.backend.model;

import jakarta.persistence.Embeddable;

@Embeddable // Ne spune că această clasă va fi "încorporată" în altă entitate
public class Profile {

    // Câmpuri pentru BENEFICIAR
    private String numeFirma;
    private String cui;
    private String punctDeLucru;

    // Câmpuri pentru ADMIN
    private String numeCompanie;

    // Câmpuri pentru PAZNIC
    private String nrLegitimatie;

    // --- Getters și Setters ---

    public String getNumeFirma() { return numeFirma; }
    public void setNumeFirma(String numeFirma) { this.numeFirma = numeFirma; }
    public String getCui() { return cui; }
    public void setCui(String cui) { this.cui = cui; }
    public String getPunctDeLucru() { return punctDeLucru; }
    public void setPunctDeLucru(String punctDeLucru) { this.punctDeLucru = punctDeLucru; }
    public String getNumeCompanie() { return numeCompanie; }
    public void setNumeCompanie(String numeCompanie) { this.numeCompanie = numeCompanie; }
    public String getNrLegitimatie() { return nrLegitimatie; }
    public void setNrLegitimatie(String nrLegitimatie) { this.nrLegitimatie = nrLegitimatie; }
}