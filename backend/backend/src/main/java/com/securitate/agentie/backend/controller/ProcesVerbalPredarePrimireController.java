package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.ProcesVerbalPredarePrimireRequest;
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
            @AuthenticationPrincipal User paznic) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    pvprService.createProcesVerbalPredarePrimire(request, paznic)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la generarea documentului: " + e.getMessage());
        }
    }

    @GetMapping("/documente")
    public ResponseEntity<?> getDocumente() {
        // Autorizarea e în SecurityConfig
        return ResponseEntity.ok(pvprService.getDocumente());
    }
}