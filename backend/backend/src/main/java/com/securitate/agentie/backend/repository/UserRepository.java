package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Role; // Asigură-te că ai acest import
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Asigură-te că ai acest import
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);
    // --- ADAUGĂ ACESTE DOUĂ METODE ---

    /**
     * Găsește toți utilizatorii cu un anumit rol.
     * (Folosit de ADMINISTRATOR care vede pe toți).
     */
    List<User> findByRole(Role role);

    /**
     * Găsește utilizatorii după rol ȘI după adminul care i-a creat.
     * (Folosit de ADMIN care vede doar utilizatorii creați de el).
     */
    List<User> findByRoleAndCreatDeAdmin(Role role, User creatDeAdmin);

    // --- SFÂRȘIT ADAUGĂ ---
}