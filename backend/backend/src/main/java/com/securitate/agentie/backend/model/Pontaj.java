package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pontaj")
public class Pontaj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_id", nullable = false)
    private User paznic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime oraIntrare;

    private LocalDateTime oraIesire; // Null cât timp tura e activă

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public Pontaj() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPaznic() { return paznic; }
    public void setPaznic(User paznic) { this.paznic = paznic; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public LocalDateTime getOraIntrare() { return oraIntrare; }
    public void setOraIntrare(LocalDateTime oraIntrare) { this.oraIntrare = oraIntrare; }
    public LocalDateTime getOraIesire() { return oraIesire; }
    public void setOraIesire(LocalDateTime oraIesire) { this.oraIesire = oraIesire; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}