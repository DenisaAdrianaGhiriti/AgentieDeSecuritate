package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.IncidentDto;
import com.securitate.agentie.backend.dto.IncidentRequest;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidente")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<?> createIncident(@RequestBody IncidentRequest request) {
        try {
            IncidentDto created = incidentService.createIncident(
                    request.getTitlu(),
                    request.getDescriere(),
                    request.getPunctDeLucru(),
                    request.getCompanieId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<IncidentDto>> getIncidente() {
        return ResponseEntity.ok(incidentService.getIncidente());
    }

    @GetMapping("/beneficiar")
    public ResponseEntity<List<IncidentDto>> getIncidenteByBeneficiar(@AuthenticationPrincipal User beneficiary) {
        return ResponseEntity.ok(incidentService.getIncidenteByBeneficiar(beneficiary));
    }

    @PostMapping("/{id}/restabilire")
    public ResponseEntity<?> restabilireIncident(@PathVariable Long id) {
        try {
            IncidentDto created = incidentService.restabilireIncident(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncident(@PathVariable Long id) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.ok("Incident șters cu succes!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/istoric")
    public ResponseEntity<List<IncidentDto>> getIstoricIncidente() {
        return ResponseEntity.ok(incidentService.getIstoricIncidente());
    }
}
