package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.CreateSesizareRequest;
import com.securitate.agentie.backend.dto.SesizareDTO;
import com.securitate.agentie.backend.model.StatusSesizare;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.SesizareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sesizari")
public class SesizareController {

    private final SesizareService sesizareService;

    public SesizareController(SesizareService sesizareService) {
        this.sesizareService = sesizareService;
    }

    @PostMapping
    public ResponseEntity<?> createSesizare(@RequestBody CreateSesizareRequest request,
                                            @AuthenticationPrincipal User beneficiary) {
        try {
            SesizareDTO dto = sesizareService.createSesizare(request, beneficiary);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSesizari() {
        return ResponseEntity.ok(sesizareService.getAllSesizari());
    }

    @GetMapping("/beneficiar")
    public ResponseEntity<?> getSesizariByBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(sesizareService.getSesizariByBeneficiar(beneficiary));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSesizare(@PathVariable Long id) {
        try {
            sesizareService.deleteSesizare(id);
            return ResponseEntity.ok("Sesizare ștearsă cu succes!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            StatusSesizare newStatus = StatusSesizare.valueOf(request.getStatus().toUpperCase());
            SesizareDTO dto = sesizareService.updateStatus(id, newStatus);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePasi(@PathVariable Long id, @RequestBody PasiRezolvareRequest request) {
        try {
            SesizareDTO dto = sesizareService.updatePasiRezolvare(id, request.getPasiRezolvare());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DTO-uri interne simple pentru request-uri
    public static class StatusUpdateRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class PasiRezolvareRequest {
        private String pasiRezolvare;
        public String getPasiRezolvare() { return pasiRezolvare; }
        public void setPasiRezolvare(String pasiRezolvare) { this.pasiRezolvare = pasiRezolvare; }
    }
}
