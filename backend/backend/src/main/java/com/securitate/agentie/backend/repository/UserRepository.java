package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Role; // IMPORT NOU
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // IMPORT NOU
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Traducerea lui: User.find({ role: role, creatDeAdminId: req.user._id })
    List<User> findByRoleAndCreatDeAdmin(Role role, User creatDeAdmin);
}