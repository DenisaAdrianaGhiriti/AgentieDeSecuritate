package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.RaportEveniment;
import org.springframework.data.jpa.repository.JpaRepository;
//import java.time.LocalDateTime;
import java.util.List;

public interface RaportEvenimentRepository extends JpaRepository<RaportEveniment, Long> {

    // Pentru rută: /documente (doar cele neexpirate)
//    List<RaportEveniment> findByDataExpirareAfter(LocalDateTime now);

    // Pentru cleanup
//    List<RaportEveniment> findByDataExpirareBefore(LocalDateTime now);
}