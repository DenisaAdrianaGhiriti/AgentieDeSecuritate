package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jurnal_status_sesizari")
public class JurnalStatusSesizari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesizare_id", nullable = false)
    private Sesizare sesizare;

    @Enumerated(EnumType.STRING)
    private StatusSesizare statusVechi; // Poate fi null la prima intrare

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSesizare statusNou;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modificat_de_id", nullable = false)
    private User modificatDe;

    // Echivalentul { timestamps: { createdAt: 'data_schimbare', updatedAt: false } }
    @Column(name = "data_schimbare", updatable = false)
    private LocalDateTime dataSchimbare;

    @PrePersist
    protected void onCreate() { dataSchimbare = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public JurnalStatusSesizari() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sesizare getSesizare() { return sesizare; }
    public void setSesizare(Sesizare sesizare) { this.sesizare = sesizare; }
    public StatusSesizare getStatusVechi() { return statusVechi; }
    public void setStatusVechi(StatusSesizare statusVechi) { this.statusVechi = statusVechi; }
    public StatusSesizare getStatusNou() { return statusNou; }
    public void setStatusNou(StatusSesizare statusNou) { this.statusNou = statusNou; }
    public User getModificatDe() { return modificatDe; }
    public void setModificatDe(User modificatDe) { this.modificatDe = modificatDe; }
    public LocalDateTime getDataSchimbare() { return dataSchimbare; }
    public void setDataSchimbare(LocalDateTime dataSchimbare) { this.dataSchimbare = dataSchimbare; }
}