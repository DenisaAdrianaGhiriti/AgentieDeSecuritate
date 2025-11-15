package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "atasamente_incident")
public class AtasamentIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(nullable = false)
    private String numeFisier;

    @Column(nullable = false)
    private String caleStocare; // Probabil calea către S3, minIO sau un folder de pe server

    private String tipFisier; // ex: "image/jpeg", "application/pdf"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incarcat_de_id", nullable = false)
    private User incarcatDe;

    // Echivalentul { timestamps: { createdAt: 'data_incarcare', updatedAt: false } }
    @Column(name = "data_incarcare", updatable = false)
    private LocalDateTime dataIncarcare;

    @PrePersist
    protected void onCreate() { dataIncarcare = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public AtasamentIncident() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public String getNumeFisier() { return numeFisier; }
    public void setNumeFisier(String numeFisier) { this.numeFisier = numeFisier; }
    public String getCaleStocare() { return caleStocare; }
    public void setCaleStocare(String caleStocare) { this.caleStocare = caleStocare; }
    public String getTipFisier() { return tipFisier; }
    public void setTipFisier(String tipFisier) { this.tipFisier = tipFisier; }
    public User getIncarcatDe() { return incarcatDe; }
    public void setIncarcatDe(User incarcatDe) { this.incarcatDe = incarcatDe; }
    public LocalDateTime getDataIncarcare() { return dataIncarcare; }
    public void setDataIncarcare(LocalDateTime dataIncarcare) { this.dataIncarcare = dataIncarcare; }
}