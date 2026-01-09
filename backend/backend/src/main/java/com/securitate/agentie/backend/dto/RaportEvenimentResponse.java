package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class RaportEvenimentResponse {
    private Long id;
    private String caleStocarePDF;
    private String numarRaport;
    private String punctDeLucru;
    private LocalDateTime createdAt;

    public RaportEvenimentResponse(Long id, String caleStocarePDF, String numarRaport, String punctDeLucru, LocalDateTime createdAt) {
        this.id = id;
        this.caleStocarePDF = caleStocarePDF;
        this.numarRaport = numarRaport;
        this.punctDeLucru = punctDeLucru;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getCaleStocarePDF() { return caleStocarePDF; }
    public String getNumarRaport() { return numarRaport; }
    public String getPunctDeLucru() { return punctDeLucru; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
