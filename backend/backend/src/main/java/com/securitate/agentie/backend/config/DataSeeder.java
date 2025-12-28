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

        // --- NOU: Administrator (Super-Admin) ---
        String superAdminEmail = "superadmin@test.com"; // Email diferit
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            User superAdmin = new User();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
            superAdmin.setNume("Super");
            superAdmin.setPrenume("Admin");
            superAdmin.setRole(Role.ADMINISTRATOR); // <-- Rolul corect
            superAdmin.setTelefon("0700000000");
            superAdmin.setEsteActiv(true);

            userRepository.save(superAdmin);
            System.out.println("--- Super Administrator a fost creat cu succes! ---");
        }

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
            admin.setRole(Role.ADMIN);
            admin.setTelefon("0777777777");
            admin.setEsteActiv(true);

            userRepository.save(admin);
            System.out.println("---  Admin a fost creat cu succes! ---");
        }

        String adminEmail2 = "admin2@test.com";
        if (userRepository.findByEmail(adminEmail2).isEmpty()) {
            User admin2 = new User();
            admin2.setEmail(adminEmail2);
            admin2.setPassword(passwordEncoder.encode("adminnou123")); // Parolă: adminnou123
            admin2.setNume("Popescu");
            admin2.setPrenume("Andrei");
            admin2.setRole(Role.ADMIN); // Rolul este ADMIN
            admin2.setTelefon("0733333333");
            admin2.setEsteActiv(true);

            userRepository.save(admin2);
            System.out.println("--- Admin 2 (admin2@test.com) a fost creat cu succes! ---");
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