package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapoarte_evenimente")
public class RaportEveniment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_id", nullable = false)
    private User paznic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private User beneficiary;

    @Column(nullable = false)
    private String punctDeLucru; // Corespunde cu "numarPost" din Node.js

    private String numarRaport;

    @Column(nullable = false)
    private LocalDateTime dataRaport;

    @Column(nullable = false)
    private String numePaznic; // Numele complet al paznicului

    @Column(nullable = false)
    private String functiePaznic;

    @Column(nullable = false)
    private String societate; // Numele firmei beneficiarului

    @Column(nullable = false)
    private LocalDateTime dataConstatare;

    @Column(nullable = false)
    private String oraConstatare; // String (HH:mm)

    @Column(nullable = false)
    private String numeFaptuitor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descriereFapta;

    @Column(nullable = false)
    private String cazSesizatLa;

//    @Column(nullable = false)
//    private LocalDateTime dataExpirare;

    @Column(nullable = false)
    private String caleStocarePDF;

    // Timestamps
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
//        dataExpirare = LocalDateTime.now().plusDays(60);
    }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public RaportEveniment() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPaznic() { return paznic; }
    public void setPaznic(User paznic) { this.paznic = paznic; }
    public User getBeneficiary() { return beneficiary; }
    public void setBeneficiary(User beneficiary) { this.beneficiary = beneficiary; }
    public String getPunctDeLucru() { return punctDeLucru; }
    public void setPunctDeLucru(String punctDeLucru) { this.punctDeLucru = punctDeLucru; }
    public String getNumarRaport() { return numarRaport; }
    public void setNumarRaport(String numarRaport) { this.numarRaport = numarRaport; }
    public LocalDateTime getDataRaport() { return dataRaport; }
    public void setDataRaport(LocalDateTime dataRaport) { this.dataRaport = dataRaport; }
    public String getNumePaznic() { return numePaznic; }
    public void setNumePaznic(String numePaznic) { this.numePaznic = numePaznic; }
    public String getFunctiePaznic() { return functiePaznic; }
    public void setFunctiePaznic(String functiePaznic) { this.functiePaznic = functiePaznic; }
    public String getSocietate() { return societate; }
    public void setSocietate(String societate) { this.societate = societate; }
    public LocalDateTime getDataConstatare() { return dataConstatare; }
    public void setDataConstatare(LocalDateTime dataConstatare) { this.dataConstatare = dataConstatare; }
    public String getOraConstatare() { return oraConstatare; }
    public void setOraConstatare(String oraConstatare) { this.oraConstatare = oraConstatare; }
    public String getNumeFaptuitor() { return numeFaptuitor; }
    public void setNumeFaptuitor(String numeFaptuitor) { this.numeFaptuitor = numeFaptuitor; }
    public String getDescriereFapta() { return descriereFapta; }
    public void setDescriereFapta(String descriereFapta) { this.descriereFapta = descriereFapta; }
    public String getCazSesizatLa() { return cazSesizatLa; }
    public void setCazSesizatLa(String cazSesizatLa) { this.cazSesizatLa = cazSesizatLa; }
//    public LocalDateTime getDataExpirare() { return dataExpirare; }
//    public void setDataExpirare(LocalDateTime dataExpirare) { this.dataExpirare = dataExpirare; }
    public String getCaleStocarePDF() { return caleStocarePDF; }
    public void setCaleStocarePDF(String caleStocarePDF) { this.caleStocarePDF = caleStocarePDF; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}