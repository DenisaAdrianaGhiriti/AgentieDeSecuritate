package com.securitate.agentie.backend.dto;

import java.util.List;

public class AssignmentRequest {
    private Long beneficiaryId;
    private String punct;
    private List<Long> pazniciIds;

    // Getters și Setters
    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }
    public String getPunct() { return punct; }
    public void setPunct(String punct) { this.punct = punct; }
    public List<Long> getPazniciIds() { return pazniciIds; }
    public void setPazniciIds(List<Long> pazniciIds) { this.pazniciIds = pazniciIds; }
}