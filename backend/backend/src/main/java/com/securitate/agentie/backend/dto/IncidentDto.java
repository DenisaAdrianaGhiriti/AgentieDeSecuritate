package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class IncidentDto {
    private Long id;
    private String titlu;
    private String descriere;
    private String punctDeLucru;
    private LocalDateTime dataIncident;

    private boolean restabilit;
    private boolean istoric;

    private Long companieId;
    private Long paznicId;
    private Long postId;

    public IncidentDto() {}

    public IncidentDto(Long id, String titlu, String descriere, String punctDeLucru,
                       LocalDateTime dataIncident, boolean restabilit, boolean istoric,
                       Long companieId, Long paznicId, Long postId) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.punctDeLucru = punctDeLucru;
        this.dataIncident = dataIncident;
        this.restabilit = restabilit;
        this.istoric = istoric;
        this.companieId = companieId;
        this.paznicId = paznicId;
        this.postId = postId;
    }

    public Long getId() { return id; }
    public String getTitlu() { return titlu; }
    public String getDescriere() { return descriere; }
    public String getPunctDeLucru() { return punctDeLucru; }
    public LocalDateTime getDataIncident() { return dataIncident; }

    public boolean isRestabilit() { return restabilit; }
    public boolean isIstoric() { return istoric; }

    public Long getCompanieId() { return companieId; }
    public Long getPaznicId() { return paznicId; }
    public Long getPostId() { return postId; }

    public void setId(Long id) { this.id = id; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public void setPunctDeLucru(String punctDeLucru) { this.punctDeLucru = punctDeLucru; }
    public void setDataIncident(LocalDateTime dataIncident) { this.dataIncident = dataIncident; }

    public void setRestabilit(boolean restabilit) { this.restabilit = restabilit; }
    public void setIstoric(boolean istoric) { this.istoric = istoric; }

    public void setCompanieId(Long companieId) { this.companieId = companieId; }
    public void setPaznicId(Long paznicId) { this.paznicId = paznicId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
