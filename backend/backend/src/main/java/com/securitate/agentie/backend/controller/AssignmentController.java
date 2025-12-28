package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.AssignmentRequest;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.AssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignPaznici(@RequestBody AssignmentRequest request) {
        try {
            return ResponseEntity.ok(assignmentService.assignPaznici(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/unassign")
    public ResponseEntity<?> unassignPaznici(@RequestBody AssignmentRequest request) {
        try {
            return ResponseEntity.ok(assignmentService.unassignPaznici(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{beneficiaryId}/paznici")
    public ResponseEntity<?> getAssignedPaznici(@PathVariable Long beneficiaryId,
                                                @RequestParam("punct") String punct) {
        try {
            return ResponseEntity.ok(assignmentService.getAssignedPaznici(beneficiaryId, punct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}