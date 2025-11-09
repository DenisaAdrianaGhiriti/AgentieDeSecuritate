package com.securitate.agentie.backend.config;

import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- CORECȚIE AICI ---
        // Verificăm același email pe care îl creăm
        String adminEmail = "admin@test.com";

        // Verificăm dacă user-ul administrator există deja
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail); // Folosim variabila
            // PAROLA TREBUIE HASH-UITĂ înainte de salvare!
            admin.setPassword(passwordEncoder.encode("test123"));
            admin.setNume("Pop");
            admin.setPrenume("Ion");
            admin.setRole(Role.ADMINISTRATOR);
            admin.setTelefon("0777777777");
            admin.setEsteActiv(true);

            userRepository.save(admin);
            System.out.println("--- Super Admin a fost creat cu succes! ---");
        }
    }
}