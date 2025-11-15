package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidente")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titlu;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descriere;

    @Column(nullable = false)
    private LocalDateTime dataIncident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_id", nullable = false)
    private User paznic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Echivalentul { timestamps: true }
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Setăm dataIncident la creare dacă nu e setată explicit
        if (dataIncident == null) {
            dataIncident = LocalDateTime.now();
        }
    }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public Incident() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public LocalDateTime getDataIncident() { return dataIncident; }
    public void setDataIncident(LocalDateTime dataIncident) { this.dataIncident = dataIncident; }
    public User getPaznic() { return paznic; }
    public void setPaznic(User paznic) { this.paznic = paznic; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}