package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontajRepository extends JpaRepository<Pontaj, Long> {

    // Traducerea lui: Pontaj.findOne({ paznicId, ora_iesire: null })
    Optional<Pontaj> findByPaznicAndOraIesireIsNull(User paznic);

    // NOU: Pentru getActiveEmployees (Admin)
    List<Pontaj> findByOraIesireIsNull();

    // NOU: Pentru getActiveEmployeesForBeneficiar (Beneficiar)
    List<Pontaj> findByOraIesireIsNullAndBeneficiary(User beneficiary);

    // NOU: Pentru istoric 60 zile (Admin)
    List<Pontaj> findByOraIntrareGreaterThan(LocalDateTime date);

    // NOU: Pentru istoric 60 zile (Beneficiar)
    List<Pontaj> findByBeneficiaryAndOraIntrareGreaterThan(User beneficiary, LocalDateTime date);
}