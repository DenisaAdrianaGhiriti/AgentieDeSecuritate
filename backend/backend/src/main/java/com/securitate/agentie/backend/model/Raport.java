package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapoarte")
public class Raport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipRaport tipRaport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generat_de_id", nullable = false)
    private User generatDe;

    /**
     * Echivalentul 'Mixed' din Mongoose.
     * Vom stoca parametrii ca un String JSON.
     * Ex: {"startDate": "2023-01-01", "userId": 5}
     */
    @Column(columnDefinition = "TEXT")
    private String parametrii;

    @Column(nullable = false)
    private String caleStocare; // Calea către fișierul PDF/CSV generat

    @Column(nullable = false)
    private LocalDateTime dataExpirare;

    // NOTĂ: Indexul TTL (expireAfterSeconds) din Mongoose nu are un echivalent direct în JPA/MySQL.
    // Ștergerea rapoartelor expirate ar trebui făcută de un @Scheduled task în Spring.

    // Echivalentul { timestamps: { createdAt: 'data_generare', updatedAt: false } }
    @Column(name = "data_generare", updatable = false)
    private LocalDateTime dataGenerare;

    @PrePersist
    protected void onCreate() { dataGenerare = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public Raport() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TipRaport getTipRaport() { return tipRaport; }
    public void setTipRaport(TipRaport tipRaport) { this.tipRaport = tipRaport; }
    public User getGeneratDe() { return generatDe; }
    public void setGeneratDe(User generatDe) { this.generatDe = generatDe; }
    public String getParametrii() { return parametrii; }
    public void setParametrii(String parametrii) { this.parametrii = parametrii; }
    public String getCaleStocare() { return caleStocare; }
    public void setCaleStocare(String caleStocare) { this.caleStocare = caleStocare; }
    public LocalDateTime getDataExpirare() { return dataExpirare; }
    public void setDataExpirare(LocalDateTime dataExpirare) { this.dataExpirare = dataExpirare; }
    public LocalDateTime getDataGenerare() { return dataGenerare; }
    public void setDataGenerare(LocalDateTime dataGenerare) { this.dataGenerare = dataGenerare; }
}