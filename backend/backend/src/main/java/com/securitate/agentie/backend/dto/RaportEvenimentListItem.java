package com.securitate.agentie.backend.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public record RaportEvenimentListItem(
        Long id,
        String caleStocarePDF,
        LocalDateTime createdAt,
        String numarRaport,
        LocalDateTime dataRaport,
        String punctDeLucru,
        String numePaznic,
        String societate
) {}
