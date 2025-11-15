package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.CreateUserRequest;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Echivalentul 'createUser'
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request,
                                        @AuthenticationPrincipal User adminCreator) { // Ia user-ul logat
        try {
            User newUser = userService.createUser(request, adminCreator);
            // Putem returna un DTO mai simplu, dar deocamdată e ok
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Echivalentul 'getUsersByRole'
    @GetMapping("/list/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable("role") String role,
                                            @AuthenticationPrincipal User adminCreator) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userService.getUsersByRole(roleEnum, adminCreator));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol invalid.");
        }
    }

    // Echivalentul 'createAdminAccount'
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminAccount(@RequestBody CreateUserRequest request) {
        // Securitatea (doar ADMINISTRATOR) e deja în SecurityConfig
        try {
            User newAdmin = userService.createAdminAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User currentUser) {
        // Prin adnotarea @AuthenticationPrincipal, Spring Security injectează
        // automat obiectul 'User' care a fost autentificat de JwtAuthenticationFilter
        // (este exact echivalentul lui 'req.user' din Node.js)

        return ResponseEntity.ok(currentUser);
    }
}