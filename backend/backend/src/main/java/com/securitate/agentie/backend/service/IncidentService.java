package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.IncidentDto;
import com.securitate.agentie.backend.model.Incident;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.IncidentRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository, UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    // -------------------------
    // Mapper entity -> dto
    // -------------------------
    private IncidentDto toDto(Incident i) {
        Long companieId = (i.getCompanie() != null) ? i.getCompanie().getId() : null;
        Long paznicId = (i.getPaznic() != null) ? i.getPaznic().getId() : null;
        Long postId = (i.getPost() != null) ? i.getPost().getId() : null;

        // IMPORTANT: la booleene, la tine probabil sunt primitive și getter-ele sunt isXxx()
        boolean restabilit = i.isRestabilit();
        boolean istoric = i.isIstoric();

        return new IncidentDto(
                i.getId(),
                i.getTitlu(),
                i.getDescriere(),
                i.getPunctDeLucru(),
                i.getDataIncident(),
                restabilit,
                istoric,
                companieId,
                paznicId,
                postId
        );
    }

    // GET toate incidentele (Admin) - DTO
    public List<IncidentDto> getIncidente() {
        return incidentRepository.findAll().stream().map(this::toDto).toList();
    }

    // GET incidente pentru beneficiarul logat - DTO
    public List<IncidentDto> getIncidenteByBeneficiar(User beneficiary) {
        return incidentRepository.findByCompanie(beneficiary).stream().map(this::toDto).toList();
    }

    // POST creare incident - DTO
    public IncidentDto createIncident(String titlu, String descriere, String punctDeLucru, Long companieId) {
        User companie = userRepository.findById(companieId)
                .orElseThrow(() -> new IllegalArgumentException("Compania (Beneficiarul) nu a fost găsită."));

        if (companie.getRole() != Role.BENEFICIAR) {
            throw new IllegalArgumentException("ID-ul specificat nu este un Beneficiar.");
        }

        Incident incident = new Incident();
        incident.setTitlu(titlu);
        incident.setDescriere(descriere);
        incident.setCompanie(companie);
        incident.setPunctDeLucru(punctDeLucru);
        incident.setCreatedAt(LocalDateTime.now());
        incident.setDataIncident(LocalDateTime.now());
        // paznic și post sunt opționale, le lăsăm null

        Incident saved = incidentRepository.save(incident);
        return toDto(saved);
    }

    // DELETE incident
    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidentul nu a fost găsit."));
        incidentRepository.delete(incident);
    }

    // POST restabilire incident - DTO
    public IncidentDto restabilireIncident(Long id) {
        Incident originalIncident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidentul nu a fost găsit."));

        // Marchează originalul ca istoric
        originalIncident.setIstoric(true);
        incidentRepository.save(originalIncident);

        // Creează incident verde (restabilit)
        Incident restabilire = new Incident();
        restabilire.setTitlu("Restabilire: " + originalIncident.getTitlu());
        restabilire.setCreatedAt(LocalDateTime.now());
        restabilire.setDescriere(originalIncident.getDescriere());
        restabilire.setCompanie(originalIncident.getCompanie());
        restabilire.setPunctDeLucru(originalIncident.getPunctDeLucru());
        restabilire.setRestabilit(true);
        restabilire.setIstoric(true); // și restabilirea intră direct în istoric
        restabilire.setDataIncident(LocalDateTime.now());

        Incident saved = incidentRepository.save(restabilire);
        return toDto(saved);
    }

    // GET incidente din istoric - DTO
    public List<IncidentDto> getIstoricIncidente() {
        return incidentRepository.findByIstoricTrue().stream().map(this::toDto).toList();
    }
}
