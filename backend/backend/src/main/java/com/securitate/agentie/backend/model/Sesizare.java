package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesizari")
public class Sesizare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titlu;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT e mai bun pentru descrieri lungi
    private String descriere;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSesizare status = StatusSesizare.PRELUCRATA; // Echivalentul 'default'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_beneficiary_id", nullable = false)
    private User createdByBeneficiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id", nullable = false)
    private User assignedAdmin;

    @Column(columnDefinition = "TEXT")
    private String pasiRezolvare;

    private LocalDateTime dataFinalizare;

    // Echivalentul { timestamps: true }
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private LocalDateTime expireAt;
    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }


    // --- Constructor gol (JPA) ---
    public Sesizare() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public StatusSesizare getStatus() { return status; }
    public void setStatus(StatusSesizare status) { this.status = status; }
    public User getCreatedByBeneficiary() { return createdByBeneficiary; }
    public void setCreatedByBeneficiary(User createdByBeneficiary) { this.createdByBeneficiary = createdByBeneficiary; }
    public User getAssignedAdmin() { return assignedAdmin; }
    public void setAssignedAdmin(User assignedAdmin) { this.assignedAdmin = assignedAdmin; }
    public String getPasiRezolvare() { return pasiRezolvare; }
    public void setPasiRezolvare(String pasiRezolvare) { this.pasiRezolvare = pasiRezolvare; }
    public LocalDateTime getDataFinalizare() { return dataFinalizare; }
    public void setDataFinalizare(LocalDateTime dataFinalizare) { this.dataFinalizare = dataFinalizare; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // NOU: Getter/Setter pentru expireAt
//    public LocalDateTime getExpireAt() { return expireAt; }
//    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
}