package com.securitate.agentie.backend.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import java.util.List;
import java.util.ArrayList;

@Embeddable
public class Profile {

    // Câmpuri pentru BENEFICIAR
    private String numeFirma;
    private String cui;

    @ElementCollection
    @CollectionTable(name = "beneficiary_puncte_de_lucru", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "punct_de_lucru")
    private List<String> puncteDeLucru = new ArrayList<>();

    // Câmpuri pentru ADMIN (de agenție)
    private String numeCompanie;

    // Câmpuri pentru PAZNIC
    private String nrLegitimatie;

    // --- Getters și Setters ---

    public String getNumeFirma() { return numeFirma; }
    public void setNumeFirma(String numeFirma) { this.numeFirma = numeFirma; }

    public String getCui() { return cui; }
    public void setCui(String cui) { this.cui = cui; }

    public List<String> getPuncteDeLucru() { return puncteDeLucru; }
    public void setPuncteDeLucru(List<String> puncteDeLucru) { this.puncteDeLucru = puncteDeLucru; }

    public String getNumeCompanie() { return numeCompanie; }
    public void setNumeCompanie(String numeCompanie) { this.numeCompanie = numeCompanie; }

    public String getNrLegitimatie() { return nrLegitimatie; }
    public void setNrLegitimatie(String nrLegitimatie) { this.nrLegitimatie = nrLegitimatie; }
}