package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.model.ProcesVerbal;
import com.securitate.agentie.backend.repository.ProcesVerbalRepository;
import com.securitate.agentie.backend.service.ProcesVerbalPredarePrimireService;
import com.securitate.agentie.backend.service.RaportEvenimentService;
import com.securitate.agentie.backend.service.SesizareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cleanup")
public class CleanupController {

    private final ProcesVerbalRepository procesVerbalRepository;
    private final ProcesVerbalPredarePrimireService pvprService;
    private final RaportEvenimentService reService;
    private final SesizareService sesizareService;

    public CleanupController(ProcesVerbalRepository procesVerbalRepository, ProcesVerbalPredarePrimireService pvprService, RaportEvenimentService reService, SesizareService sesizareService) {
        this.procesVerbalRepository = procesVerbalRepository;
        this.pvprService = pvprService;
        this.reService = reService;
        this.sesizareService = sesizareService;
    }

    @DeleteMapping("/documente-expirate")
    public ResponseEntity<?> deleteDocumenteExpirate() {
        int totalDeleted = 0; // Contor

//        // 1. Proces Verbal (folosește direct Repository, dar corect):
//        List<ProcesVerbal> deletedPV = procesVerbalRepository.findByDataExpirareBefore(now);
//        procesVerbalRepository.deleteAll(deletedPV);
//        totalDeleted += deletedPV.size();
//
//        // 2. Proces Verbal Predare Primire (folosește Service-ul):
//        // Îi cerem Service-ului să facă și ștergerea
//        int deletedPVPPCount = pvprService.deleteExpirate();
//        totalDeleted += deletedPVPPCount;
//
//        // 3. Raport Eveniment (folosește Service-ul):
//        int deletedRECount = reService.deleteExpirate();
//        totalDeleted += deletedRECount;
//
//        // 4. Sesizări (folosește Service-ul):
//        int deletedSesizariCount = sesizareService.deleteExpirate();
//        totalDeleted += deletedSesizariCount;


        return ResponseEntity.ok(
                String.format("Documente expirate șterse. Total: %d (Logica de ștergere automată a fost eliminată).",
                        totalDeleted)
        );
    }
}