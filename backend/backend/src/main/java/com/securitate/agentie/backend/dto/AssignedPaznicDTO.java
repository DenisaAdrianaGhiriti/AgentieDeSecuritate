package com.securitate.agentie.backend.dto;

import com.securitate.agentie.backend.model.Profile;

// DTO pentru a returna paznicii alocați (include detalii din Profile)
public class AssignedPaznicDTO extends SimpleUserDTO {
    public AssignedPaznicDTO(Long id, String nume, String prenume, String email, Profile profile) {
        super(id, nume, prenume, email, null); // Ignorăm telefonul în acest context

        if (profile != null) {
            super.setNrLegitimatie(profile.getNrLegitimatie());
        }
    }
}