package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.ProcesVerbal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime; // NOU
import java.util.List; // NOU

public interface ProcesVerbalRepository extends JpaRepository<ProcesVerbal, Long> {
    // Aici pot fi adăugate metode de căutare specifice dacă e nevoie
    // De exemplu: findByPaznic(User paznic);
//    List<ProcesVerbal> findByDataExpirareBefore(LocalDateTime now);
}