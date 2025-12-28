package com.securitate.agentie.backend.dto;
import java.util.List;

// DTO simplificat pentru a returna datele paznicilor/beneficiarilor
public class SimpleUserDTO {
    private Long id;
    private String nume;
    private String prenume;
    private String email;
    private String telefon;
    private String nrLegitimatie; // Pentru Paznic
    private String numeCompanie; // Pentru Beneficiar
    private List<String> puncteDeLucru;

    public SimpleUserDTO(Long id, String nume, String prenume, String email, String telefon) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.telefon = telefon;
    }

    // Getters și Setters (doar pentru câmpurile necesare)
    public List<String> getPuncteDeLucru() { return puncteDeLucru; }
    public void setPuncteDeLucru(List<String> puncteDeLucru) { this.puncteDeLucru = puncteDeLucru; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public String getNrLegitimatie() { return nrLegitimatie; }
    public void setNrLegitimatie(String nrLegitimatie) { this.nrLegitimatie = nrLegitimatie; }
    public String getNumeCompanie() { return numeCompanie; }
    public void setNumeCompanie(String numeCompanie) { this.numeCompanie = numeCompanie; }
}
