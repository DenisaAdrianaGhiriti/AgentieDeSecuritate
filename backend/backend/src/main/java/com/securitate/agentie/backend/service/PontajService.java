package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.LocationUpdate;
import com.securitate.agentie.backend.dto.SimpleUserDTO;
import com.securitate.agentie.backend.model.*;
import com.securitate.agentie.backend.repository.AssignedPazniciItemRepository;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.ProcesVerbalPredarePrimireRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PontajService {

    private final PontajRepository pontajRepository;
    private final ProcesVerbalPredarePrimireRepository pvprRepository;
    private final UserRepository userRepository;
    private final AssignedPazniciItemRepository assignedPazniciItemRepository;

    public PontajService(PontajRepository pontajRepository, ProcesVerbalPredarePrimireRepository pvprRepository, UserRepository userRepository, AssignedPazniciItemRepository assignedPazniciItemRepository) {
        this.pontajRepository = pontajRepository;
        this.pvprRepository = pvprRepository;
        this.userRepository = userRepository;
        this.assignedPazniciItemRepository = assignedPazniciItemRepository;
    }

    // Logica din 'checkIn'
    public Pontaj checkIn(LocationUpdate locationUpdate, User paznic) {
        if (locationUpdate.getLatitude() == null || locationUpdate.getLongitude() == null) {
            throw new IllegalArgumentException("Datele de localizare sunt obligatorii pentru a începe tura.");
        }

        // Verifică dacă există deja o tură activă
        if (pontajRepository.findByPaznicAndOraIesireIsNull(paznic).isPresent()) {
            throw new IllegalStateException("Aveți deja o tură activă. Vă rugăm să faceți check-out mai întâi.");
        }

        // --- Caută beneficiarul alocat (LOGICĂ CORECTATĂ) ---
        User beneficiary = assignedPazniciItemRepository.findAll().stream()
                // 1. Filtrează AssignedPazniciItem care conțin Paznicul logat
                .filter(item -> item.getPaznici().stream()
                        .anyMatch(p -> p.getId().equals(paznic.getId())))
                // 2. Mapează rezultatul la Beneficiar
                .map(AssignedPazniciItem::getBeneficiary)
                // 3. Ia primul Beneficiar găsit
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nu sunteți alocat la niciun beneficiar. Vă rugăm contactați administratorul."));
        // --- SFÂRȘIT LOGICĂ ---

        Pontaj newPontaj = new Pontaj();
        newPontaj.setPaznic(paznic);
        newPontaj.setBeneficiary(beneficiary); // Folosim beneficiarul găsit
        newPontaj.setOraIntrare(LocalDateTime.now());

        // Adaugă primul punct de localizare
        newPontaj.getLocationHistory().add(
                new LocationPoint(locationUpdate.getLatitude(), locationUpdate.getLongitude())
        );

        return pontajRepository.save(newPontaj);
    }

    // Logica din 'checkOut'
    public Pontaj checkOut(User paznic) {
        Pontaj pontajActiv = pontajRepository.findByPaznicAndOraIesireIsNull(paznic)
                .orElseThrow(() -> new IllegalStateException("Nu a fost găsită nicio tură activă pentru check-out."));

        // Verifică existența Procesului Verbal Predare-Primire (Logica din Node.js)
        if (pvprRepository.findByPontaj(pontajActiv).isEmpty()) {
            throw new IllegalStateException("EROARE: Procesul Verbal de Predare-Primire nu a fost găsit. Tura nu poate fi încheiată.");
        }

        // Echivalentul lui findOneAndUpdate
        pontajActiv.setOraIesire(LocalDateTime.now());
        return pontajRepository.save(pontajActiv);
    }

    // NOU: getActivePontaj (Admin/Paznic)
    public Optional<Pontaj> getActivePontaj(User paznic) {
        return pontajRepository.findByPaznicAndOraIesireIsNull(paznic);
    }

    // NOU: getActiveEmployees (Admin/Administrator)
    public List<Pontaj> getActiveEmployees() {
        return pontajRepository.findByOraIesireIsNull();
    }

    // NOU: getActiveEmployeesForBeneficiar (Beneficiar)
    public List<Pontaj> getActiveEmployeesForBeneficiar(User beneficiary) {
        return pontajRepository.findByOraIesireIsNullAndBeneficiary(beneficiary);
    }

    // NOU: updateLocation (Paznic)
    public Pontaj updateLocation(LocationUpdate locationUpdate, User paznic) {
        if (locationUpdate.getLatitude() == null || locationUpdate.getLongitude() == null) {
            throw new IllegalArgumentException("Coordonate invalide!");
        }

        Pontaj pontaj = pontajRepository.findByPaznicAndOraIesireIsNull(paznic)
                .orElseThrow(() -> new IllegalStateException("Nu există tura activă pentru acest paznic."));

        pontaj.getLocationHistory().add(
                new LocationPoint(locationUpdate.getLatitude(), locationUpdate.getLongitude())
        );

        return pontajRepository.save(pontaj);
    }

    // NOU: getLatestLocation (Admin/Beneficiar)
    public LocationPoint getLatestLocation(Long paznicId) {
        User paznic = userRepository.findById(paznicId)
                .orElseThrow(() -> new IllegalArgumentException("Paznicul nu a fost găsit."));

        Pontaj pontaj = pontajRepository.findByPaznicAndOraIesireIsNull(paznic)
                .orElseThrow(() -> new IllegalStateException("Nicio locație găsită pentru acest paznic (fără tură activă)."));

        if (pontaj.getLocationHistory().isEmpty()) {
            throw new IllegalStateException("Nu există istoric de locație pentru tura activă.");
        }

        // Returnează ultima locație (cea mai recentă)
        return pontaj.getLocationHistory().stream()
                .max(Comparator.comparing(LocationPoint::getTimestamp))
                .orElseThrow(() -> new IllegalStateException("Nu există istoric de locație pentru tura activă."));
    }

    // NOU: getIstoricPontaje (Admin) - pe ultimele 60 de zile
    public List<Pontaj> getIstoricPontaje() {
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(60);
        return pontajRepository.findByOraIntrareGreaterThan(dateLimit);
    }

    // NOU: getIstoricBeneficiar (Beneficiar) - pe ultimele 60 de zile
    public List<Pontaj> getIstoricBeneficiar(User beneficiary) {
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(60);
        return pontajRepository.findByBeneficiaryAndOraIntrareGreaterThan(beneficiary, dateLimit);
    }
}