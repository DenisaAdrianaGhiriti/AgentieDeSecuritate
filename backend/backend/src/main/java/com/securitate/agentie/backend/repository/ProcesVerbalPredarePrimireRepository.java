package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.ProcesVerbalPredarePrimire;
import com.securitate.agentie.backend.model.Pontaj;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
//import java.time.LocalDateTime;
//import java.util.List;

public interface ProcesVerbalPredarePrimireRepository extends JpaRepository<ProcesVerbalPredarePrimire, Long> {
    Optional<ProcesVerbalPredarePrimire> findByPontaj(Pontaj pontaj);

    // Pentru rută: /documente (doar cele neexpirate)
//    List<ProcesVerbalPredarePrimire> findByDataExpirareAfter(LocalDateTime now);

    // Pentru cleanup
//    List<ProcesVerbalPredarePrimire> findByDataExpirareBefore(LocalDateTime now);
}