package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.CreateSesizareRequest;
import com.securitate.agentie.backend.dto.SesizareDTO;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.Sesizare;
import com.securitate.agentie.backend.model.StatusSesizare;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.SesizareRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SesizareService {

    private final SesizareRepository sesizareRepository;
    private final UserRepository userRepository;

    public SesizareService(SesizareRepository sesizareRepository, UserRepository userRepository) {
        this.sesizareRepository = sesizareRepository;
        this.userRepository = userRepository;
    }

    // ---------- Mapper Entity -> DTO ----------
    private SesizareDTO toDto(Sesizare s) {
        SesizareDTO dto = new SesizareDTO();

        dto.setId(s.getId());
        dto.setTitlu(s.getTitlu());
        dto.setDescriere(s.getDescriere());
        dto.setStatus(s.getStatus());
        dto.setPasiRezolvare(s.getPasiRezolvare());
        dto.setDataFinalizare(s.getDataFinalizare());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());

        // Beneficiar
        User b = s.getCreatedByBeneficiary();
        if (b != null) {
            dto.setBeneficiarId(b.getId());
            dto.setBeneficiarNume(b.getNume());
            dto.setBeneficiarPrenume(b.getPrenume());
            dto.setBeneficiarEmail(b.getEmail());
        }

        // Admin asignat
        User a = s.getAssignedAdmin();
        if (a != null) {
            dto.setAdminId(a.getId());
            dto.setAdminNume(a.getNume());
            dto.setAdminPrenume(a.getPrenume());
            dto.setAdminEmail(a.getEmail());
        }

        return dto;
    }

    // --- 1. CREARE SESIZARE ---
    public SesizareDTO createSesizare(CreateSesizareRequest request, User beneficiary) {
        if (request.getTitlu() == null || request.getTitlu().trim().isEmpty()
                || request.getDescriere() == null || request.getDescriere().trim().isEmpty()) {
            throw new IllegalArgumentException("Titlu și descriere sunt obligatorii.");
        }

        User assignedAdmin = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.ADMINISTRATOR)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nu există niciun admin în sistem pentru a prelua sesizarea."));

        Sesizare newSesizare = new Sesizare();
        newSesizare.setTitlu(request.getTitlu());
        newSesizare.setDescriere(request.getDescriere());
        newSesizare.setCreatedByBeneficiary(beneficiary);
        newSesizare.setAssignedAdmin(assignedAdmin);
        newSesizare.setStatus(StatusSesizare.PRELUCRATA);

        Sesizare saved = sesizareRepository.save(newSesizare);
        return toDto(saved);
    }

    // --- 2. ACTUALIZARE STATUS ---
    public SesizareDTO updateStatus(Long id, StatusSesizare newStatus) {
        Sesizare sesizare = sesizareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesizarea nu a fost găsită."));

        if (newStatus == StatusSesizare.REZOLVATA) {
            sesizare.setDataFinalizare(LocalDateTime.now());
        } else {
            sesizare.setDataFinalizare(null);
        }

        sesizare.setStatus(newStatus);
        Sesizare updated = sesizareRepository.save(sesizare);

        return toDto(updated);
    }

    // --- 3. ACTUALIZARE PAȘI DE REZOLVARE ---
    public SesizareDTO updatePasiRezolvare(Long id, String pasiRezolvare) {
        Sesizare sesizare = sesizareRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesizarea nu a fost găsită."));

        sesizare.setPasiRezolvare(pasiRezolvare);
        Sesizare updated = sesizareRepository.save(sesizare);

        return toDto(updated);
    }

    // --- 4. GET ALL SESIZARI (Admin) ---
    public List<SesizareDTO> getAllSesizari() {
        // Variante:
        // A) simplu (merge ok, dar poate face N+1)
        // return sesizareRepository.findAll().stream().map(this::toDto).toList();

        // B) recomandat (dacă implementezi findAllWithUsers în repository)
        return sesizareRepository.findAllWithUsers().stream().map(this::toDto).toList();
    }

    // --- 5. GET SESIZARI BY BENEFICIAR ---
    public List<SesizareDTO> getSesizariByBeneficiar(User beneficiary) {
        // simplu:
        return sesizareRepository.findByCreatedByBeneficiary(beneficiary).stream()
                .map(this::toDto)
                .toList();
    }

    // --- 6. DELETE SESIZARE ---
    public void deleteSesizare(Long id) {
        if (!sesizareRepository.existsById(id)) {
            throw new IllegalArgumentException("Sesizarea negăsită.");
        }
        sesizareRepository.deleteById(id);
    }
}
