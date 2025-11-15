package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.CreateUserRequest;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Logica din 'createUser'
    public User createUser(CreateUserRequest request, User adminCreator) {
        if (request.getRole() == Role.ADMINISTRATOR) {
            throw new IllegalArgumentException("Nu se poate crea un utilizator cu rol de administrator.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Acest email este deja înregistrat.");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole());
        newUser.setNume(request.getNume());
        newUser.setPrenume(request.getPrenume());
        newUser.setTelefon(request.getTelefon());
        newUser.setProfile(request.getProfile());
        newUser.setCreatDeAdmin(adminCreator); // Setează cine l-a creat
        newUser.setEsteActiv(true);

        return userRepository.save(newUser);
    }

    // Logica din 'getUsersByRole'
    public List<User> getUsersByRole(Role role, User adminCreator) {
        return userRepository.findByRoleAndCreatDeAdmin(role, adminCreator);
    }

    // Logica din 'createAdminAccount'
    public User createAdminAccount(CreateUserRequest request) {
        // Poate fi apelat doar de ADMINISTRATOR (verificat în SecurityConfig)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Acest email este deja înregistrat.");
        }

        User newAdmin = new User();
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN); // Setează rolul 'ADMIN'
        newAdmin.setNume(request.getNume());
        newAdmin.setPrenume(request.getPrenume());
        newAdmin.setProfile(request.getProfile());
        // 'creatDeAdmin' este null sau poți seta super-adminul dacă vrei
        newAdmin.setEsteActiv(true);

        return userRepository.save(newAdmin);
    }
}