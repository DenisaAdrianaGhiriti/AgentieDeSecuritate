package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.ProcesVerbalPredarePrimireRequest;
import com.securitate.agentie.backend.dto.ProcesVerbalPredarePrimireResponse;
import com.securitate.agentie.backend.model.ProcesVerbalPredarePrimire;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.ProcesVerbalPredarePrimireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proces-verbal-predare")
public class ProcesVerbalPredarePrimireController {

    private final ProcesVerbalPredarePrimireService pvprService;

    public ProcesVerbalPredarePrimireController(ProcesVerbalPredarePrimireService pvprService) {
        this.pvprService = pvprService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProcesVerbalPredarePrimire(
            @RequestBody ProcesVerbalPredarePrimireRequest request,
            @AuthenticationPrincipal User paznic
    ) {
        try {
            ProcesVerbalPredarePrimire pv = pvprService.createProcesVerbalPredarePrimire(request, paznic);

            ProcesVerbalPredarePrimireResponse response = new ProcesVerbalPredarePrimireResponse(
                    pv.getId(),
                    pv.getPontaj().getId(),
                    pv.getCaleStocarePDF(),
                    pv.getDataIncheierii(),
                    pv.getReprezentantBeneficiar(),
                    pv.getNumeReprezentantPrimire()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la generarea documentului: " + e.getMessage());
        }
    }

    @GetMapping("/documente")
    public ResponseEntity<?> getDocumente() {
        // Aici încă returnezi entity -> poate da iar proxy error.
        // Ideal: faci și aici un DTO list, dar dacă nu folosești endpoint-ul acum, poți lăsa temporar.
        return ResponseEntity.ok(pvprService.getDocumente());
    }
}
