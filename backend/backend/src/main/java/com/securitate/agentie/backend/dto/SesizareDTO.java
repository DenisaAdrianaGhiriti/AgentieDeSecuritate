package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.StatusSesizare;

import java.time.LocalDateTime;

public class SesizareDTO {

    private Long id;
    private String titlu;
    private String descriere;
    private StatusSesizare status;
    private String pasiRezolvare;
    private LocalDateTime dataFinalizare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Beneficiar (creator)
    private Long beneficiarId;
    private String beneficiarNume;
    private String beneficiarPrenume;
    private String beneficiarEmail;

    // Admin asignat
    private Long adminId;
    private String adminNume;
    private String adminPrenume;
    private String adminEmail;

    public SesizareDTO() {}

    // --- Getters / Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }

    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }

    public StatusSesizare getStatus() { return status; }
    public void setStatus(StatusSesizare status) { this.status = status; }

    public String getPasiRezolvare() { return pasiRezolvare; }
    public void setPasiRezolvare(String pasiRezolvare) { this.pasiRezolvare = pasiRezolvare; }

    public LocalDateTime getDataFinalizare() { return dataFinalizare; }
    public void setDataFinalizare(LocalDateTime dataFinalizare) { this.dataFinalizare = dataFinalizare; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getBeneficiarId() { return beneficiarId; }
    public void setBeneficiarId(Long beneficiarId) { this.beneficiarId = beneficiarId; }

    public String getBeneficiarNume() { return beneficiarNume; }
    public void setBeneficiarNume(String beneficiarNume) { this.beneficiarNume = beneficiarNume; }

    public String getBeneficiarPrenume() { return beneficiarPrenume; }
    public void setBeneficiarPrenume(String beneficiarPrenume) { this.beneficiarPrenume = beneficiarPrenume; }

    public String getBeneficiarEmail() { return beneficiarEmail; }
    public void setBeneficiarEmail(String beneficiarEmail) { this.beneficiarEmail = beneficiarEmail; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public String getAdminNume() { return adminNume; }
    public void setAdminNume(String adminNume) { this.adminNume = adminNume; }

    public String getAdminPrenume() { return adminPrenume; }
    public void setAdminPrenume(String adminPrenume) { this.adminPrenume = adminPrenume; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
}
