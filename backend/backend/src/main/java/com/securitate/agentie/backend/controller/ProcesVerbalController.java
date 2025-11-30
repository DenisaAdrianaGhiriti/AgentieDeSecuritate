package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.CreateProcesVerbalRequest;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.ProcesVerbalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proces-verbal")
public class ProcesVerbalController {

    private final ProcesVerbalService procesVerbalService;

    public ProcesVerbalController(ProcesVerbalService procesVerbalService) {
        this.procesVerbalService = procesVerbalService;
    }

    /**
     * Echivalentul rutei:
     * POST /api/proces-verbal/:pontajId
     * Autorizare: Paznic, Administrator
     */
    @PostMapping("/{pontajId}")
    public ResponseEntity<?> createProcesVerbal(
            @PathVariable Long pontajId,
            @RequestBody CreateProcesVerbalRequest request,
            @AuthenticationPrincipal User paznic) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    procesVerbalService.createProcesVerbal(pontajId, request, paznic)
            );
        } catch (IllegalArgumentException e) {
            // Ex: Pontajul nu a fost găsit
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            // Ex: Paznicul nu are dreptul pe acest pontaj
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Orice altă eroare (ex: generare PDF eșuată)
            System.err.println("Eroare la crearea procesului verbal: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare de server: " + e.getMessage());
        }
    }
}