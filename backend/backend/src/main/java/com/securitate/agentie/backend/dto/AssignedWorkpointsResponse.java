package com.securitate.agentie.backend.dto;

import java.util.List;

public record AssignedWorkpointsResponse(
        Long beneficiarId,
        String numeCompanie,
        List<String> puncteDeLucru
) {}
