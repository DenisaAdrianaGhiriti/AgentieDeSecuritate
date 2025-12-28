package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.CreateUserRequest;
import com.securitate.agentie.backend.dto.SimpleUserDTO;
import com.securitate.agentie.backend.dto.UserUpdateRequest;
import com.securitate.agentie.backend.model.AssignedPazniciItem;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.model.Profile;
import com.securitate.agentie.backend.repository.AssignedPazniciItemRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssignedPazniciItemRepository assignedPazniciItemRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AssignedPazniciItemRepository assignedPazniciItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.assignedPazniciItemRepository = assignedPazniciItemRepository;
    }

    // --- CRUD DE BAZĂ ȘI LISTARE ---

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
        newUser.setCreatDeAdmin(adminCreator);
        newUser.setEsteActiv(true);

        return userRepository.save(newUser);
    }

    @Transactional
    public User createAdminAccount(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Acest email este deja înregistrat.");
        }

        User newAdmin = new User();
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(Role.ADMIN);
        newAdmin.setNume(request.getNume());
        newAdmin.setPrenume(request.getPrenume());
        newAdmin.setTelefon(request.getTelefon());

        if (request.getProfile() != null) {
            newAdmin.setProfile(request.getProfile());
        }

        newAdmin.setEsteActiv(true);
        return userRepository.save(newAdmin);
    }

    public List<User> getUsersByRole(Role role, User adminCreator) {
        if (adminCreator.getRole() == Role.ADMINISTRATOR) {
            return userRepository.findByRole(role);
        } else {
            return userRepository.findByRoleAndCreatDeAdmin(role, adminCreator);
        }
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost găsit."));

        if (request.getNume() != null) user.setNume(request.getNume());
        if (request.getPrenume() != null) user.setPrenume(request.getPrenume());
        if (request.getTelefon() != null) user.setTelefon(request.getTelefon());
        if (request.getEsteActiv() != null) user.setEsteActiv(request.getEsteActiv());
        if (request.getRole() != null) user.setRole(request.getRole());

        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Noul email este deja înregistrat.");
            }
            user.setEmail(request.getEmail());
        }

        // ✅ MERGE PROFILE (nu overwrite)
        if (request.getProfile() != null) {
            Profile current = user.getProfile();
            if (current == null) current = new Profile();

            Profile incoming = request.getProfile();

            // update câmpuri simple doar dacă vin nenule
            if (incoming.getNumeFirma() != null) current.setNumeFirma(incoming.getNumeFirma());
            if (incoming.getCui() != null) current.setCui(incoming.getCui());
            if (incoming.getNumeCompanie() != null) current.setNumeCompanie(incoming.getNumeCompanie());
            if (incoming.getNrLegitimatie() != null) current.setNrLegitimatie(incoming.getNrLegitimatie());

            // ✅ puncteDeLucru: append (fără dubluri), nu replace
            if (incoming.getPuncteDeLucru() != null && !incoming.getPuncteDeLucru().isEmpty()) {
                if (current.getPuncteDeLucru() == null) current.setPuncteDeLucru(new ArrayList<>());

                for (String p : incoming.getPuncteDeLucru()) {
                    if (p == null) continue;
                    String punct = p.trim();
                    if (punct.isEmpty()) continue;

                    if (!current.getPuncteDeLucru().contains(punct)) {
                        current.getPuncteDeLucru().add(punct);
                    }
                }
            }

            user.setProfile(current);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Parola trebuie să aibă minim 6 caractere.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost găsit."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, User currentUser) {
        if (id.equals(currentUser.getId())) {
            throw new IllegalArgumentException("Nu vă puteți șterge propriul cont.");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost găsit."));

        if (userToDelete.getRole() == Role.ADMINISTRATOR) {
            if (id.equals(1L)) {
                throw new IllegalArgumentException("Acțiunea a fost blocată. Nu se poate șterge contul Super Administrator.");
            }
        }

        userRepository.delete(userToDelete);
    }

    // --- LOGICĂ LISTARE USERI/ALOCĂRI (REFECTORIZATĂ) ---

    public List<SimpleUserDTO> getAllBeneficiari() {
        return userRepository.findByRole(Role.BENEFICIAR).stream()
                .map(u -> {
                    SimpleUserDTO dto = new SimpleUserDTO(u.getId(), u.getNume(), u.getPrenume(), u.getEmail(), u.getTelefon());
                    if (u.getProfile() != null) {
                        dto.setNumeCompanie(u.getProfile().getNumeFirma());
                        dto.setPuncteDeLucru(u.getProfile().getPuncteDeLucru());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // NOU: getBeneficiariForPaznic (Beneficiarii alocați unui paznic)
    public List<SimpleUserDTO> getBeneficiariForPaznic(User paznic) {
        // Căutăm AssignedPazniciItem unde acest paznic este listat
        List<AssignedPazniciItem> items = assignedPazniciItemRepository.findAll().stream()
                .filter(item -> item.getPaznici().stream()
                        .anyMatch(p -> p.getId().equals(paznic.getId())))
                .collect(Collectors.toList());

        // Extragem beneficiarii unici
        return items.stream()
                .map(AssignedPazniciItem::getBeneficiary)
                .distinct()
                .map(u -> {
                    SimpleUserDTO dto = new SimpleUserDTO(u.getId(), u.getNume(), u.getPrenume(), u.getEmail(), u.getTelefon());
                    if (u.getProfile() != null) {
                        dto.setNumeCompanie(u.getProfile().getNumeFirma());
                        dto.setPuncteDeLucru(u.getProfile().getPuncteDeLucru());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<SimpleUserDTO> getPaznici(User currentUser) {
        if (currentUser.getRole() == Role.PAZNIC) {
            SimpleUserDTO dto = new SimpleUserDTO(currentUser.getId(), currentUser.getNume(), currentUser.getPrenume(), currentUser.getEmail(), currentUser.getTelefon());
            if (currentUser.getProfile() != null) {
                dto.setNrLegitimatie(currentUser.getProfile().getNrLegitimatie());
            }
            return List.of(dto);
        }

        return userRepository.findByRole(Role.PAZNIC).stream()
                .map(u -> {
                    SimpleUserDTO dto = new SimpleUserDTO(u.getId(), u.getNume(), u.getPrenume(), u.getEmail(), u.getTelefon());
                    if (u.getProfile() != null) {
                        dto.setNrLegitimatie(u.getProfile().getNrLegitimatie());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // NOU: getAngajatiBeneficiar (Paznicii alocați unui Beneficiar)
    public List<SimpleUserDTO> getAngajatiBeneficiar(User beneficiary) {
        // Colectăm toți paznicii unici din toate AssignedPazniciItem ale acestui beneficiar

        List<User> uniquePaznici = beneficiary.getAssignedPazniciItems().stream()
                .flatMap(item -> item.getPaznici().stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(HashSet::new),
                        ArrayList::new));

        return uniquePaznici.stream()
                .map(u -> {
                    SimpleUserDTO dto = new SimpleUserDTO(u.getId(), u.getNume(), u.getPrenume(), u.getEmail(), u.getTelefon());
                    if (u.getProfile() != null) {
                        dto.setNrLegitimatie(u.getProfile().getNrLegitimatie());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}