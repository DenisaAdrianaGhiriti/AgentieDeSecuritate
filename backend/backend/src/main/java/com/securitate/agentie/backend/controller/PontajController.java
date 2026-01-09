package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.LocationUpdate;
import com.securitate.agentie.backend.dto.PontajDTO;
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

    // ------------------- CHECK-IN -------------------
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody LocationUpdate locationUpdate,
                                     @AuthenticationPrincipal User paznic) {
        try {
            Pontaj newPontaj = pontajService.checkIn(locationUpdate, paznic);

            var resp = new com.securitate.agentie.backend.dto.PontajResponse(
                    newPontaj.getId(),
                    newPontaj.getOraIntrare(),
                    newPontaj.getOraIesire(),
                    newPontaj.getPaznic().getId(),
                    newPontaj.getBeneficiary().getId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message", "Check-in efectuat cu succes!", "pontaj", resp)
            );
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // ------------------- CHECK-OUT -------------------
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal User paznic) {
        try {
            pontajService.checkOut(paznic);
            return ResponseEntity.ok("Check-out efectuat cu succes. Tura a fost încheiată.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ------------------- ACTIVE PONTAJ (pt paznic) -------------------
    @GetMapping("/active")
    public ResponseEntity<?> getActivePontaj(@AuthenticationPrincipal User user) {
        Optional<Pontaj> pontajOpt = pontajService.getActivePontaj(user);

        if (pontajOpt.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        Pontaj p = pontajOpt.get();
        var resp = new com.securitate.agentie.backend.dto.PontajResponse(
                p.getId(), p.getOraIntrare(), p.getOraIesire(),
                p.getPaznic().getId(), p.getBeneficiary().getId()
        );

        return ResponseEntity.ok(resp);
    }

    // ------------------- ANGAJATI ACTIVI (Admin/Administrator) -------------------
    @GetMapping("/angajati-activi")
    public ResponseEntity<?> getActiveEmployees() {
        // IMPORTANT: DTO ca să evităm proxy/lazy serialization errors
        return ResponseEntity.ok(pontajService.getActiveEmployeesDTO());
    }

    // ------------------- ANGAJATI ACTIVI (Beneficiar) -------------------
    @GetMapping("/angajati-activi-beneficiar")
    public ResponseEntity<?> getActiveEmployeesForBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(pontajService.getActiveEmployeesForBeneficiarDTO(beneficiary));
    }

    // ------------------- UPDATE LOCATION (Paznic) -------------------
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

    // ------------------- LATEST LOCATION (Admin/Beneficiar) -------------------
    @GetMapping("/locatie/{paznicId}")
    public ResponseEntity<?> getLatestLocation(@PathVariable Long paznicId) {
        try {
            LocationPoint latestLocation = pontajService.getLatestLocation(paznicId);
            return ResponseEntity.ok(latestLocation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ------------------- ISTORIC 60 ZILE (Admin/Administrator) -------------------
    @GetMapping("/istoric-60zile")
    public ResponseEntity<?> getIstoricPontaje() {
        return ResponseEntity.ok(pontajService.getIstoricPontajeDTO());
    }

    // ------------------- ISTORIC 60 ZILE (Beneficiar) -------------------
    @GetMapping("/istoric-60zile-beneficiar")
    public ResponseEntity<?> getIstoricBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(pontajService.getIstoricBeneficiarDTO(beneficiary));
    }
}
