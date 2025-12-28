package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Sesizare;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SesizareRepository extends JpaRepository<Sesizare, Long> {
    List<Sesizare> findByCreatedByBeneficiary(User createdByBeneficiary);
//    List<Sesizare> findByExpireAtBefore(java.time.LocalDateTime now);
    @Query("""
        select s from Sesizare s
        join fetch s.createdByBeneficiary
        join fetch s.assignedAdmin
    """)
List<Sesizare> findAllWithUsers();
}