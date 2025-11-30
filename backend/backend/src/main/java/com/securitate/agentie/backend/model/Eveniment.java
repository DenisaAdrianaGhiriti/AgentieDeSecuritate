package com.securitate.agentie.backend.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Reprezintă un singur rând (eveniment) din tabelul procesului verbal.
 * Echivalentul Mongoose sub-schema 'evenimentSchema'.
 *
 * @Embeddable - Spune JPA că această clasă este încorporată în altă entitate
 * (în cazul nostru, în ProcesVerbal).
 */
@Embeddable
public class Eveniment {

    private LocalDateTime dataOraReceptionarii;
    private String tipulAlarmei;
    private String echipajAlarmat;
    private LocalDateTime oraSosirii;
    private String cauzeleAlarmei;
    private String modulDeSolutionare;
    private String observatii;

    // --- Constructor gol (JPA) ---
    public Eveniment() {}

    // --- Getters și Setters ---
    public LocalDateTime getDataOraReceptionarii() {
        return dataOraReceptionarii;
    }
    public void setDataOraReceptionarii(LocalDateTime dataOraReceptionarii) {
        this.dataOraReceptionarii = dataOraReceptionarii;
    }
    public String getTipulAlarmei() {
        return tipulAlarmei;
    }
    public void setTipulAlarmei(String tipulAlarmei) {
        this.tipulAlarmei = tipulAlarmei;
    }
    public String getEchipajAlarmat() {
        return echipajAlarmat;
    }
    public void setEchipajAlarmat(String echipajAlarmat) {
        this.echipajAlarmat = echipajAlarmat;
    }
    public LocalDateTime getOraSosirii() {
        return oraSosirii;
    }
    public void setOraSosirii(LocalDateTime oraSosirii) {
        this.oraSosirii = oraSosirii;
    }
    public String getCauzeleAlarmei() {
        return cauzeleAlarmei;
    }
    public void setCauzeleAlarmei(String cauzeleAlarmei) {
        this.cauzeleAlarmei = cauzeleAlarmei;
    }
    public String getModulDeSolutionare() {
        return modulDeSolutionare;
    }
    public void setModulDeSolutionare(String modulDeSolutionare) {
        this.modulDeSolutionare = modulDeSolutionare;
    }
    public String getObservatii() {
        return observatii;
    }
    public void setObservatii(String observatii) {
        this.observatii = observatii;
    }
}