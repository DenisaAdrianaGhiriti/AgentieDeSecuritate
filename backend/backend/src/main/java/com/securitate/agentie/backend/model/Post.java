package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts") // "post" poate fi un cuvânt cheie SQL, folosim "posts"
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numePost;

    private String adresaPost;

    @Column(unique = true, nullable = false)
    private String qrCodeIdentifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private User beneficiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private User createdByAdmin;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public Post() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumePost() { return numePost; }
    public void setNumePost(String numePost) { this.numePost = numePost; }
    public String getAdresaPost() { return adresaPost; }
    public void setAdresaPost(String adresaPost) { this.adresaPost = adresaPost; }
    public String getQrCodeIdentifier() { return qrCodeIdentifier; }
    public void setQrCodeIdentifier(String qrCodeIdentifier) { this.qrCodeIdentifier = qrCodeIdentifier; }
    public User getBeneficiary() { return beneficiary; }
    public void setBeneficiary(User beneficiary) { this.beneficiary = beneficiary; }
    public User getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(User createdByAdmin) { this.createdByAdmin = createdByAdmin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}