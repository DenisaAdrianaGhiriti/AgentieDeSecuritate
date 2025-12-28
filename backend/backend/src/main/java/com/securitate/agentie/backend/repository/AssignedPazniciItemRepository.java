package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.AssignedPazniciItem;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AssignedPazniciItemRepository extends JpaRepository<AssignedPazniciItem, Long> {
    // Folosită de AssignmentService pentru a găsi o alocare specifică
    Optional<AssignedPazniciItem> findByBeneficiaryAndPunct(User beneficiary, String punct);
}