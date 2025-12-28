package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.LocationUpdate; // NOU
import com.securitate.agentie.backend.model.LocationPoint;
import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.PontajService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pontaj")
public class PontajController {

    private final PontajService pontajService;

    public PontajController(PontajService pontajService) {
        this.pontajService = pontajService;
    }

    // Rută actualizată pentru a primi LocationUpdate
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody LocationUpdate locationUpdate,
                                     @AuthenticationPrincipal User paznic) {
        try {
            Pontaj newPontaj = pontajService.checkIn(locationUpdate, paznic);
            // Reține că mesajul din Node.js (cu numele companiei) ar trebui adăugat la client
            return ResponseEntity.status(HttpStatus.CREATED).body(newPontaj);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal User paznic) {
        try {
            pontajService.checkOut(paznic);
            return ResponseEntity.ok("Check-out efectuat cu succes. Tura a fost încheiată.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // NOU: getActivePontaj
    @GetMapping("/active")
    public ResponseEntity<?> getActivePontaj(@AuthenticationPrincipal User user) {
        // Userul poate fi Paznic sau Administrator (SecurityConfig)
        Optional<Pontaj> pontaj = pontajService.getActivePontaj(user);
        return ResponseEntity.ok(pontaj.orElse(null));
    }

    // NOU: getActiveEmployees (Admin/Administrator)
    @GetMapping("/angajati-activi")
    public ResponseEntity<?> getActiveEmployees() {
        return ResponseEntity.ok(pontajService.getActiveEmployees());
    }

    // NOU: getActiveEmployeesForBeneficiar (Beneficiar)
    @GetMapping("/angajati-activi-beneficiar")
    public ResponseEntity<?> getActiveEmployeesForBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(pontajService.getActiveEmployeesForBeneficiar(beneficiary));
    }

    // NOU: updateLocation (Paznic)
    @PostMapping("/update-location")
    public ResponseEntity<?> updateLocation(@RequestBody LocationUpdate locationUpdate,
                                            @AuthenticationPrincipal User paznic) {
        try {
            pontajService.updateLocation(locationUpdate, paznic);
            return ResponseEntity.ok("Locația a fost actualizată.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // NOU: getLatestLocation (Admin/Beneficiar)
    @GetMapping("/locatie/{paznicId}")
    public ResponseEntity<?> getLatestLocation(@PathVariable Long paznicId) {
        try {
            LocationPoint latestLocation = pontajService.getLatestLocation(paznicId);
            return ResponseEntity.ok(latestLocation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // NOU: getIstoricPontaje (Admin)
    @GetMapping("/istoric-60zile")
    public ResponseEntity<?> getIstoricPontaje() {
        return ResponseEntity.ok(pontajService.getIstoricPontaje());
    }

    // NOU: getIstoricBeneficiar (Beneficiar)
    @GetMapping("/istoric-60zile-beneficiar")
    public ResponseEntity<?> getIstoricBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(pontajService.getIstoricBeneficiar(beneficiary));
    }
}