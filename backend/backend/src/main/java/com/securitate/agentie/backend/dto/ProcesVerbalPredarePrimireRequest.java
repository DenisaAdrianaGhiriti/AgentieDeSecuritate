package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class ProcesVerbalPredarePrimireRequest {
    private Long pontajId;
    private LocalDateTime dataIncheierii;
    private String numeReprezentantPrimire;
    private String obiectePredate;
    private String reprezentantBeneficiar; // Numele societății beneficiarului
    private Long reprezentantVigilentId; // ID-ul paznicului/reprezentantului vigilent

    // --- Getters și Setters ---

    public Long getPontajId() {
        return pontajId;
    }

    public void setPontajId(Long pontajId) {
        this.pontajId = pontajId;
    }

    public LocalDateTime getDataIncheierii() {
        return dataIncheierii;
    }

    public void setDataIncheierii(LocalDateTime dataIncheierii) {
        this.dataIncheierii = dataIncheierii;
    }

    public String getNumeReprezentantPrimire() {
        return numeReprezentantPrimire;
    }

    public void setNumeReprezentantPrimire(String numeReprezentantPrimire) {
        this.numeReprezentantPrimire = numeReprezentantPrimire;
    }

    public String getObiectePredate() {
        return obiectePredate;
    }

    public void setObiectePredate(String obiectePredate) {
        this.obiectePredate = obiectePredate;
    }

    public String getReprezentantBeneficiar() {
        return reprezentantBeneficiar;
    }

    public void setReprezentantBeneficiar(String reprezentantBeneficiar) {
        this.reprezentantBeneficiar = reprezentantBeneficiar;
    }

    public Long getReprezentantVigilentId() {
        return reprezentantVigilentId;
    }

    public void setReprezentantVigilentId(Long reprezentantVigilentId) {
        this.reprezentantVigilentId = reprezentantVigilentId;
    }
}