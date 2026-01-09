package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public class PontajResponse {
    private Long id;
    private LocalDateTime oraIntrare;
    private LocalDateTime oraIesire;
    private Long paznicId;
    private Long beneficiaryId;

    public PontajResponse() {}

    public PontajResponse(Long id, LocalDateTime oraIntrare, LocalDateTime oraIesire, Long paznicId, Long beneficiaryId) {
        this.id = id;
        this.oraIntrare = oraIntrare;
        this.oraIesire = oraIesire;
        this.paznicId = paznicId;
        this.beneficiaryId = beneficiaryId;
    }

    public Long getId() { return id; }
    public LocalDateTime getOraIntrare() { return oraIntrare; }
    public LocalDateTime getOraIesire() { return oraIesire; }
    public Long getPaznicId() { return paznicId; }
    public Long getBeneficiaryId() { return beneficiaryId; }
}
