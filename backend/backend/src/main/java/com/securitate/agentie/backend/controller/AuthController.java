package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.AuthResponse;
import com.securitate.agentie.backend.dto.LoginRequest;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// Permite cererile de la frontend-ul tău React (care rulează pe alt port)
//@CrossOrigin(origins = "http://localhost:5173") // Sau portul tău de React, ex: 3001
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        // Aici Spring Security face echivalentul lui:
        // 1. User.findOne({ email })
        // 2. user.matchPassword(password)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Dacă autentificarea reușește, user-ul este în 'authentication.getPrincipal()'
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        // Generăm token-ul
        String token = jwtService.generateToken(user);

        // Returnăm răspunsul exact ca în Node.js
        return ResponseEntity.ok(new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getNume(),
                user.getRole(),
                token
        ));

        // Spring Security se ocupă automat de erorile 401 (Unauthorized)
        // dacă parola sau email-ul sunt greșite.
    }
}