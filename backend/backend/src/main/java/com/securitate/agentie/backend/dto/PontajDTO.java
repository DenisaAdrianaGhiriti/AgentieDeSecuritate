package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class PontajDTO {

    private Long id;

    private LocalDateTime oraIntrare;
    private LocalDateTime oraIesire;

    private Long paznicId;
    private String paznicNume;
    private String paznicPrenume;
    private String paznicEmail;
    private String paznicTelefon;

    private Long beneficiaryId;
    private String numeFirma;

    // constructor gol (obligatoriu pentru Jackson)
    public PontajDTO() {
    }

    // constructor complet (opțional)
    public PontajDTO(
            Long id,
            LocalDateTime oraIntrare,
            LocalDateTime oraIesire,
            Long paznicId,
            String paznicNume,
            String paznicPrenume,
            String paznicEmail,
            String paznicTelefon,
            Long beneficiaryId,
            String numeFirma
    ) {
        this.id = id;
        this.oraIntrare = oraIntrare;
        this.oraIesire = oraIesire;
        this.paznicId = paznicId;
        this.paznicNume = paznicNume;
        this.paznicPrenume = paznicPrenume;
        this.paznicEmail = paznicEmail;
        this.paznicTelefon = paznicTelefon;
        this.beneficiaryId = beneficiaryId;
        this.numeFirma = numeFirma;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOraIntrare() {
        return oraIntrare;
    }

    public void setOraIntrare(LocalDateTime oraIntrare) {
        this.oraIntrare = oraIntrare;
    }

    public LocalDateTime getOraIesire() {
        return oraIesire;
    }

    public void setOraIesire(LocalDateTime oraIesire) {
        this.oraIesire = oraIesire;
    }

    public Long getPaznicId() {
        return paznicId;
    }

    public void setPaznicId(Long paznicId) {
        this.paznicId = paznicId;
    }

    public String getPaznicNume() {
        return paznicNume;
    }

    public void setPaznicNume(String paznicNume) {
        this.paznicNume = paznicNume;
    }

    public String getPaznicPrenume() {
        return paznicPrenume;
    }

    public void setPaznicPrenume(String paznicPrenume) {
        this.paznicPrenume = paznicPrenume;
    }

    public String getPaznicEmail() {
        return paznicEmail;
    }

    public void setPaznicEmail(String paznicEmail) {
        this.paznicEmail = paznicEmail;
    }

    public String getPaznicTelefon() {
        return paznicTelefon;
    }

    public void setPaznicTelefon(String paznicTelefon) {
        this.paznicTelefon = paznicTelefon;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getNumeFirma() {
        return numeFirma;
    }

    public void setNumeFirma(String numeFirma) {
        this.numeFirma = numeFirma;
    }
}
