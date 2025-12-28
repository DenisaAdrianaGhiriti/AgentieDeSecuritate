package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.RaportEvenimentRequest;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.RaportEvenimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raport-eveniment")
public class RaportEvenimentController {

    private final RaportEvenimentService raportEvenimentService;

    public RaportEvenimentController(RaportEvenimentService raportEvenimentService) {
        this.raportEvenimentService = raportEvenimentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRaportEveniment(
            @RequestBody RaportEvenimentRequest request,
            @AuthenticationPrincipal User paznic) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    raportEvenimentService.createRaportEveniment(request, paznic)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la generarea documentului: " + e.getMessage());
        }
    }

    @GetMapping("/documente")
    public ResponseEntity<?> getDocumente() {
        // Autorizarea e în SecurityConfig
        return ResponseEntity.ok(raportEvenimentService.getDocumente());
    }
}