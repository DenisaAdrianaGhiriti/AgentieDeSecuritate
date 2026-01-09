package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO folosit ca răspuns pentru endpoint-ul de creare Proces Verbal Predare-Primire.
 * Evită serializarea entităților Hibernate (lazy proxies) în JSON.
 */
public record ProcesVerbalPredarePrimireResponse(
        Long id,
        Long pontajId,
        String caleStocarePDF,
        LocalDateTime dataIncheierii,
        String reprezentantBeneficiar,
        String numeReprezentantPrimire
) {
}
