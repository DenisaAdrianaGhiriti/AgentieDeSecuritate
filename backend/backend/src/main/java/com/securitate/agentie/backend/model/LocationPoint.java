package com.securitate.agentie.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/**
 * Reprezintă un punct de locație salvat în istoricul Pontajului.
 */
@Embeddable
public class LocationPoint {

    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // --- Constructor gol (JPA) ---
    public LocationPoint() {}
    public LocationPoint(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- Getters și Setters ---
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}