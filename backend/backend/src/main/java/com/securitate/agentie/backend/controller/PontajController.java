package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.PontajService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pontaj")
public class PontajController {

    private final PontajService pontajService;

    public PontajController(PontajService pontajService) {
        this.pontajService = pontajService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, String> body,
                                     @AuthenticationPrincipal User paznic) {
        try {
            String qrCode = body.get("qrCode");
            if (qrCode == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lipseste qrCode.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(pontajService.checkIn(qrCode, paznic));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // IllegalArgument (QR greșit) sau IllegalState (tură activă)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal User paznic) {
        try {
            return ResponseEntity.ok(pontajService.checkOut(paznic));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}