package com.securitate.agentie.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "procese_verbale")
public class ProcesVerbal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Un proces verbal este legat de O SINGURĂ tură de pontaj
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pontaj_id", nullable = false, unique = true)
    private Pontaj pontaj;

    // Paznicul care a completat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paznic_id", nullable = false)
    private User paznic;

    // Postul unde s-a întâmplat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // --- Câmpurile de pe prima pagină a PDF-ului ---
    private String reprezentantBeneficiar;
    private LocalDateTime oraDeclansareAlarma;
    private LocalDateTime oraPrezentareEchipaj;
    private LocalDateTime oraIncheiereMisiune;
    @Column(columnDefinition = "TEXT")
    private String observatiiGenerale;

    // --- Câmpul pentru tabelul de evenimente ---
    /**
     * @ElementCollection - Spune JPA că aceasta este o colecție de elemente
     * (în cazul nostru, elemente @Embeddable Eveniment).
     * @CollectionTable - Specifică numele tabelului care va stoca aceste elemente.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "proces_verbal_evenimente", joinColumns = @JoinColumn(name = "proces_verbal_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "dataOraReceptionarii", column = @Column(name = "data_ora_receptionarii")),
            @AttributeOverride(name = "tipulAlarmei", column = @Column(name = "tipul_alarmei")),
            @AttributeOverride(name = "echipajAlarmat", column = @Column(name = "echipaj_alarmat")),
            @AttributeOverride(name = "oraSosirii", column = @Column(name = "ora_sosirii")),
            @AttributeOverride(name = "cauzeleAlarmei", column = @Column(name = "cauzele_alarmei")),
            @AttributeOverride(name = "modulDeSolutionare", column = @Column(name = "modul_de_solutionare")),
            @AttributeOverride(name = "observatii", column = @Column(name = "observatii"))
    })
    private List<Eveniment> evenimente = new ArrayList<>();


    // Calea către PDF-ul generat
    @Column(nullable = false)
    private String caleStocarePDF;

    // Echivalentul { timestamps: true }
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Constructor gol (JPA) ---
    public ProcesVerbal() {
    }

    // --- Getters și Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pontaj getPontaj() {
        return pontaj;
    }

    public void setPontaj(Pontaj pontaj) {
        this.pontaj = pontaj;
    }

    public User getPaznic() {
        return paznic;
    }

    public void setPaznic(User paznic) {
        this.paznic = paznic;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getReprezentantBeneficiar() {
        return reprezentantBeneficiar;
    }

    public void setReprezentantBeneficiar(String reprezentantBeneficiar) {
        this.reprezentantBeneficiar = reprezentantBeneficiar;
    }

    public LocalDateTime getOraDeclansareAlarma() {
        return oraDeclansareAlarma;
    }

    public void setOraDeclansareAlarma(LocalDateTime oraDeclansareAlarma) {
        this.oraDeclansareAlarma = oraDeclansareAlarma;
    }

    public LocalDateTime getOraPrezentareEchipaj() {
        return oraPrezentareEchipaj;
    }

    public void setOraPrezentareEchipaj(LocalDateTime oraPrezentareEchipaj) {
        this.oraPrezentareEchipaj = oraPrezentareEchipaj;
    }

    public LocalDateTime getOraIncheiereMisiune() {
        return oraIncheiereMisiune;
    }

    public void setOraIncheiereMisiune(LocalDateTime oraIncheiereMisiune) {
        this.oraIncheiereMisiune = oraIncheiereMisiune;
    }

    public String getObservatiiGenerale() {
        return observatiiGenerale;
    }

    public void setObservatiiGenerale(String observatiiGenerale) {
        this.observatiiGenerale = observatiiGenerale;
    }

    public List<Eveniment> getEvenimente() {
        return evenimente;
    }

    public void setEvenimente(List<Eveniment> evenimente) {
        this.evenimente = evenimente;
    }

    public String getCaleStocarePDF() {
        return caleStocarePDF;
    }

    public void setCaleStocarePDF(String caleStocarePDF) {
        this.caleStocarePDF = caleStocarePDF;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

