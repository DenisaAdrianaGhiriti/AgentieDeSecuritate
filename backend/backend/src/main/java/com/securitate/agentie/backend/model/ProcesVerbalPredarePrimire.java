package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "procese_verbale_predare_primire")
public class ProcesVerbalPredarePrimire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pontaj_id", nullable = false, unique = true)
    private Pontaj pontaj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_predare_id", nullable = false)
    private User paznicPredare;

    @Column(nullable = false)
    private LocalDateTime dataIncheierii;

    @Column(nullable = false)
    private String numeReprezentantPrimire;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String obiectePredate;

    @Column(nullable = false)
    private String caleStocarePDF;

    @Column(nullable = false)
    private String reprezentantBeneficiar; // Numele Beneficiarului (firma)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reprezentant_vigilent_id", nullable = false)
    private User reprezentantVigilent; // Reprezentantul (paznicul) Vigilent

    // --- LINIE ȘTEARSĂ: private LocalDateTime dataExpirare;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // --- LINIE ȘTEARSĂ: dataExpirare = LocalDateTime.now().plusDays(60);
    }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // --- Constructor gol (JPA) ---
    public ProcesVerbalPredarePrimire() {}

    // --- Getters și Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pontaj getPontaj() { return pontaj; }
    public void setPontaj(Pontaj pontaj) { this.pontaj = pontaj; }
    public User getPaznicPredare() { return paznicPredare; }
    public void setPaznicPredare(User paznicPredare) { this.paznicPredare = paznicPredare; }
    public LocalDateTime getDataIncheierii() { return dataIncheierii; }
    public void setDataIncheierii(LocalDateTime dataIncheierii) { this.dataIncheierii = dataIncheierii; }
    public String getNumeReprezentantPrimire() { return numeReprezentantPrimire; }
    public void setNumeReprezentantPrimire(String numeReprezentantPrimire) { this.numeReprezentantPrimire = numeReprezentantPrimire; }
    public String getObiectePredate() { return obiectePredate; }
    public void setObiectePredate(String obiectePredate) { this.obiectePredate = obiectePredate; }
    public String getCaleStocarePDF() { return caleStocarePDF; }
    public void setCaleStocarePDF(String caleStocarePDF) { this.caleStocarePDF = caleStocarePDF; }
    public String getReprezentantBeneficiar() { return reprezentantBeneficiar; }
    public void setReprezentantBeneficiar(String reprezentantBeneficiar) { this.reprezentantBeneficiar = reprezentantBeneficiar; }
    public User getReprezentantVigilent() { return reprezentantVigilent; }
    public void setReprezentantVigilent(User reprezentantVigilent) { this.reprezentantVigilent = reprezentantVigilent; }
    // --- LINII ȘTERSE: dataExpirare Getters/Setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}