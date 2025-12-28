package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Incident;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCompanie(User companie);
    List<Incident> findByIstoricTrue();
    // Aici nu mai avem nevoie de logica de findByBeneficiar,
    // deoarece getIncidenteByBeneficiar folosește findByCompanie
}