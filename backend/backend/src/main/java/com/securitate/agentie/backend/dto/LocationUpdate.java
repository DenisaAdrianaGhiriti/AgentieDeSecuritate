package com.securitate.agentie.backend.dto;

// DTO pentru actualizarea locației
public class LocationUpdate {
    private Double latitude;
    private Double longitude;

    // Getters și Setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}