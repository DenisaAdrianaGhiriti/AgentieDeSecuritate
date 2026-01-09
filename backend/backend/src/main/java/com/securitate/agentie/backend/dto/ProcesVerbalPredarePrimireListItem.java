package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

public record ProcesVerbalPredarePrimireListItem(
        Long id,
        String caleStocarePDF,
        LocalDateTime createdAt,
        LocalDateTime dataIncheierii,
        String reprezentantBeneficiar,
        String numeReprezentantPrimire
) {}
