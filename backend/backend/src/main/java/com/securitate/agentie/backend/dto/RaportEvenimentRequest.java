package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class RaportEvenimentRequest {
    private Long beneficiaryId;
    private String punctDeLucru; // Corespunde cu "numarPost" din Node.js
    private String numarRaport;
    private LocalDateTime dataRaport;
    private String functiePaznic;
    private LocalDateTime dataConstatare;
    private String oraConstatare;
    private String numeFaptuitor;
    private String descriereFapta;
    private String cazSesizatLa;

    // --- Getters și Setters ---

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getPunctDeLucru() {
        return punctDeLucru;
    }

    public void setPunctDeLucru(String punctDeLucru) {
        this.punctDeLucru = punctDeLucru;
    }

    public String getNumarRaport() {
        return numarRaport;
    }

    public void setNumarRaport(String numarRaport) {
        this.numarRaport = numarRaport;
    }

    public LocalDateTime getDataRaport() {
        return dataRaport;
    }

    public void setDataRaport(LocalDateTime dataRaport) {
        this.dataRaport = dataRaport;
    }

    public String getFunctiePaznic() {
        return functiePaznic;
    }

    public void setFunctiePaznic(String functiePaznic) {
        this.functiePaznic = functiePaznic;
    }

    public LocalDateTime getDataConstatare() {
        return dataConstatare;
    }

    public void setDataConstatare(LocalDateTime dataConstatare) {
        this.dataConstatare = dataConstatare;
    }

    public String getOraConstatare() {
        return oraConstatare;
    }

    public void setOraConstatare(String oraConstatare) {
        this.oraConstatare = oraConstatare;
    }

    public String getNumeFaptuitor() {
        return numeFaptuitor;
    }

    public void setNumeFaptuitor(String numeFaptuitor) {
        this.numeFaptuitor = numeFaptuitor;
    }

    public String getDescriereFapta() {
        return descriereFapta;
    }

    public void setDescriereFapta(String descriereFapta) {
        this.descriereFapta = descriereFapta;
    }

    public String getCazSesizatLa() {
        return cazSesizatLa;
    }

    public void setCazSesizatLa(String cazSesizatLa) {
        this.cazSesizatLa = cazSesizatLa;
    }
}