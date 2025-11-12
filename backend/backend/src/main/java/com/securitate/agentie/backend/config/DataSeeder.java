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

        // --- Beneficiar (Exemplu) ---
        String beneficiarEmail = "beneficiar@test.com";
        if (userRepository.findByEmail(beneficiarEmail).isEmpty()) {
            User beneficiar = new User();
            beneficiar.setEmail(beneficiarEmail);
            beneficiar.setPassword(passwordEncoder.encode("parola123")); // Parola beneficiar
            beneficiar.setNume("Firma");
            beneficiar.setPrenume("SRL");
            beneficiar.setRole(Role.BENEFICIAR);
            beneficiar.setTelefon("0711111111");
            beneficiar.setEsteActiv(true);
            userRepository.save(beneficiar);
            System.out.println("--- Beneficiar a fost creat cu succes! ---");
        }

        // --- Paznic (Exemplu) ---
        String paznicEmail = "paznic@test.com";
        if (userRepository.findByEmail(paznicEmail).isEmpty()) {
            User paznic = new User();
            paznic.setEmail(paznicEmail);
            paznic.setPassword(passwordEncoder.encode("paznic123")); // Parola paznic
            paznic.setNume("Vasile");
            paznic.setPrenume("Gheorghe");
            paznic.setRole(Role.PAZNIC);
            paznic.setTelefon("0722222222");
            paznic.setEsteActiv(true);
            userRepository.save(paznic);
            System.out.println("--- Paznic a fost creat cu succes! ---");
        }
    }
}