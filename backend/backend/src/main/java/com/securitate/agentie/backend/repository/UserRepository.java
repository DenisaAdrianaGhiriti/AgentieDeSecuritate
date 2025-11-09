package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA înțelege automat și generează:
    // "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);
}