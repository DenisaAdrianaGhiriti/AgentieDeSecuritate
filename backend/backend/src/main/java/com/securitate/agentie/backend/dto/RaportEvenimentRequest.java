package com.securitate.agentie.backend.dto;

import java.time.LocalDate;

public class RaportEvenimentRequest {
    private Long beneficiaryId;
    private String punctDeLucru;
    private String numarRaport;

    // ✅ doar dată (yyyy-MM-dd)
    private LocalDate dataRaport;

    private String functiePaznic;

    // ✅ doar dată (yyyy-MM-dd)
    private LocalDate dataConstatare;

    // păstrezi separat ora
    private String oraConstatare;

    private String numeFaptuitor;
    private String descriereFapta;
    private String cazSesizatLa;

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public String getPunctDeLucru() { return punctDeLucru; }
    public void setPunctDeLucru(String punctDeLucru) { this.punctDeLucru = punctDeLucru; }

    public String getNumarRaport() { return numarRaport; }
    public void setNumarRaport(String numarRaport) { this.numarRaport = numarRaport; }

    public LocalDate getDataRaport() { return dataRaport; }
    public void setDataRaport(LocalDate dataRaport) { this.dataRaport = dataRaport; }

    public String getFunctiePaznic() { return functiePaznic; }
    public void setFunctiePaznic(String functiePaznic) { this.functiePaznic = functiePaznic; }

    public LocalDate getDataConstatare() { return dataConstatare; }
    public void setDataConstatare(LocalDate dataConstatare) { this.dataConstatare = dataConstatare; }

    public String getOraConstatare() { return oraConstatare; }
    public void setOraConstatare(String oraConstatare) { this.oraConstatare = oraConstatare; }

    public String getNumeFaptuitor() { return numeFaptuitor; }
    public void setNumeFaptuitor(String numeFaptuitor) { this.numeFaptuitor = numeFaptuitor; }

    public String getDescriereFapta() { return descriereFapta; }
    public void setDescriereFapta(String descriereFapta) { this.descriereFapta = descriereFapta; }

    public String getCazSesizatLa() { return cazSesizatLa; }
    public void setCazSesizatLa(String cazSesizatLa) { this.cazSesizatLa = cazSesizatLa; }
}
