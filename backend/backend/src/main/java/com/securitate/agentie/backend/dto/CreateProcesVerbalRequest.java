package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.Eveniment;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapează corpul cererii (req.body) pentru crearea unui Proces Verbal.
 */
public class CreateProcesVerbalRequest {

    // Câmpurile din prima pagină
    private String reprezentant_beneficiar;
    private LocalDateTime ora_declansare_alarma;
    private LocalDateTime ora_prezentare_echipaj;
    private LocalDateTime ora_incheiere_misiune;
    private String observatii_generale;

    // Câmpul pentru tabel (Pagina 2)
    // Folosim direct clasa @Embeddable Eveniment pentru simplitate
    private List<Eveniment> evenimente;

    // --- Getters și Setters ---
    public String getReprezentant_beneficiar() { return reprezentant_beneficiar; }
    public void setReprezentant_beneficiar(String reprezentant_beneficiar) { this.reprezentant_beneficiar = reprezentant_beneficiar; }
    public LocalDateTime getOra_declansare_alarma() { return ora_declansare_alarma; }
    public void setOra_declansare_alarma(LocalDateTime ora_declansare_alarma) { this.ora_declansare_alarma = ora_declansare_alarma; }
    public LocalDateTime getOra_prezentare_echipaj() { return ora_prezentare_echipaj; }
    public void setOra_prezentare_echipaj(LocalDateTime ora_prezentare_echipaj) { this.ora_prezentare_echipaj = ora_prezentare_echipaj; }
    public LocalDateTime getOra_incheiere_misiune() { return ora_incheiere_misiune; }
    public void setOra_incheiere_misiune(LocalDateTime ora_incheiere_misiune) { this.ora_incheiere_misiune = ora_incheiere_misiune; }
    public String getObservatii_generale() { return observatii_generale; }
    public void setObservatii_generale(String observatii_generale) { this.observatii_generale = observatii_generale; }
    public List<Eveniment> getEvenimente() { return evenimente; }
    public void setEvenimente(List<Eveniment> evenimente) { this.evenimente = evenimente; }
}