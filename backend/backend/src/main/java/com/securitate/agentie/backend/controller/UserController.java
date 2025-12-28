package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.dto.CreateUserRequest;
import com.securitate.agentie.backend.dto.PasswordUpdateRequest;
import com.securitate.agentie.backend.dto.UserUpdateRequest;
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

    // --- 1. CRUD & CREARE CONTURI ---

    // POST /api/users/create (Creare Paznic/Beneficiar de către Admin)
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request,
                                        @AuthenticationPrincipal User adminCreator) {
        System.out.println("--- Utilizator logat care încearcă să creeze user: " + adminCreator.getEmail());
        System.out.println("--- Rolul utilizatorului: " + adminCreator.getRole().name()); // Afișează rolul din obiectul User
        try {
            User newUser = userService.createUser(request, adminCreator);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // POST /api/users/create-admin (Creare Admin de către ADMINISTRATOR)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminAccount(@RequestBody CreateUserRequest request) {
        try {
            User newAdmin = userService.createAdminAccount(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PUT /api/users/{id} (Actualizare profil de către Admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // PUT /api/users/{id}/password (Schimbă parola de către Admin)
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody PasswordUpdateRequest request) {
        try {
            userService.changePassword(id, request.getNewPassword());
            return ResponseEntity.ok("Parola a fost schimbată cu succes.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // DELETE /api/users/{id} (Șterge utilizator de către Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        try {
            userService.deleteUser(id, currentUser);
            return ResponseEntity.ok("Utilizator șters cu succes!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- 2. GET PROFIL & DETALII ---

    // GET /api/users/profile (Profilul utilizatorului logat)
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User currentUser) {
        // Obiectul User este injectat direct de Spring Security.
        return ResponseEntity.ok(currentUser);
    }

    // GET /api/users/{id} (Detalii utilizator după ID)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        try {
            User user = userService.findUserById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost găsit."));

            // Logică de verificare suplimentară pentru Beneficiar
            if (currentUser.getRole() == Role.BENEFICIAR && !id.equals(currentUser.getId())) {
                // Verificăm dacă Beneficiarul are dreptul să vadă acest ID
                boolean isAssignedPaznic = userService.getAngajatiBeneficiar(currentUser).stream()
                        .anyMatch(dto -> dto.getId().equals(id));

                if (!isAssignedPaznic) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acces interzis. Nu ai permisiunea de a vizualiza detaliile acestui angajat.");
                }
            }

            // Eliminăm parola înainte de a returna
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- 3. LISTARE UTILIZATORI ȘI ALOCĂRI ---

    // GET /api/users/list/{role} (Listare utilizatori după rol pentru Admini)
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

    // GET /api/users/paznici (Listare Paznici pentru Admin / Auto-profil pentru Paznic)
    @GetMapping("/paznici")
    public ResponseEntity<?> getPaznici(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getPaznici(currentUser));
    }

    // GET /api/users/beneficiari (Listare Beneficiari. Paznicul vede doar pe cei alocați)
    @GetMapping("/beneficiari")
    public ResponseEntity<?> getBeneficiari(@AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == Role.PAZNIC) {
            // Paznicul vede doar beneficiarii la care e alocat
            return ResponseEntity.ok(userService.getBeneficiariForPaznic(currentUser));
        }
        // Admin/Administrator vede toți beneficiarii
        return ResponseEntity.ok(userService.getAllBeneficiari());
    }

    // GET /api/users/beneficiar/angajati (Paznicii alocați Beneficiarului logat)
    @GetMapping("/beneficiar/angajati")
    public ResponseEntity<?> getAngajatiBeneficiar(@AuthenticationPrincipal User beneficiary) {
        try {
            if (beneficiary.getRole() != Role.BENEFICIAR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acces interzis. Doar pentru Beneficiar.");
            }
            return ResponseEntity.ok(userService.getAngajatiBeneficiar(beneficiary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare server: " + e.getMessage());
        }
    }
}