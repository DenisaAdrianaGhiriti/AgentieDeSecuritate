package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.securitate.agentie.backend.model.Post;

@Entity
@Table(name = "pontaj")
public class Pontaj {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_id", nullable = false)
    private User paznic;

    // --- MODIFICARE AICI: Schimbat Post cu Beneficiar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private User beneficiary;

    @Column(nullable = false)
    private LocalDateTime oraIntrare;

    private LocalDateTime oraIesire; // Null cât timp tura e activă

    // --- NOU: Location History ---
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "pontaj_location_history", joinColumns = @JoinColumn(name = "pontaj_id"))
    private List<LocationPoint> locationHistory = new ArrayList<>();

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

    // MODIFICARE AICI
    public User getBeneficiary() { return beneficiary; }
    public void setBeneficiary(User beneficiary) { this.beneficiary = beneficiary; }
    // END MODIFICARE

    public LocalDateTime getOraIntrare() { return oraIntrare; }
    public void setOraIntrare(LocalDateTime oraIntrare) { this.oraIntrare = oraIntrare; }
    public LocalDateTime getOraIesire() { return oraIesire; }
    public void setOraIesire(LocalDateTime oraIesire) { this.oraIesire = oraIesire; }

    // NOU: Location History
    public List<LocationPoint> getLocationHistory() { return locationHistory; }
    public void setLocationHistory(List<LocationPoint> locationHistory) { this.locationHistory = locationHistory; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}