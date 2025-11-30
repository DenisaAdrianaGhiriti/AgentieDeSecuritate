package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PontajRepository extends JpaRepository<Pontaj, Long> {

    // Traducerea lui: Pontaj.findOne({ paznicId, ora_iesire: null })
    Optional<Pontaj> findByPaznicAndOraIesireIsNull(User paznic);
}
