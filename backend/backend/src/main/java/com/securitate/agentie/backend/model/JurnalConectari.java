package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jurnal_conectari")
public class JurnalConectari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String adresaIp;

    @Column(length = 512) // User-Agent poate fi lung
    private String agentUtilizator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLogare status;

    // Echivalentul { timestamps: { createdAt: 'data_logare', updatedAt: false } }
    @Column(name = "data_logare", updatable = false)
    private LocalDateTime dataLogare;

    @PrePersist
    protected void onCreate() { dataLogare = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public JurnalConectari() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getAdresaIp() { return adresaIp; }
    public void setAdresaIp(String adresaIp) { this.adresaIp = adresaIp; }
    public String getAgentUtilizator() { return agentUtilizator; }
    public void setAgentUtilizator(String agentUtilizator) { this.agentUtilizator = agentUtilizator; }
    public StatusLogare getStatus() { return status; }
    public void setStatus(StatusLogare status) { this.status = status; }
    public LocalDateTime getDataLogare() { return dataLogare; }
    public void setDataLogare(LocalDateTime dataLogare) { this.dataLogare = dataLogare; }
}