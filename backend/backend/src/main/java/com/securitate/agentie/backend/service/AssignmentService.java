package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.dto.AssignedPaznicDTO;
import com.securitate.agentie.backend.dto.AssignmentRequest;
import com.securitate.agentie.backend.model.AssignedPazniciItem;
import com.securitate.agentie.backend.model.Role;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.AssignedPazniciItemRepository; // NOU! Trebuie să creezi acest Repository
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final UserRepository userRepository;
    private final AssignedPazniciItemRepository assignedPazniciItemRepository; // NOU

    public AssignmentService(UserRepository userRepository, AssignedPazniciItemRepository assignedPazniciItemRepository) {
        this.userRepository = userRepository;
        this.assignedPazniciItemRepository = assignedPazniciItemRepository;
    }

    // REPOZITORIUL NOU NECESAR:
    // package com.securitate.agentie.backend.repository;
    // import org.springframework.data.jpa.repository.JpaRepository;
    // public interface AssignedPazniciItemRepository extends JpaRepository<AssignedPazniciItem, Long> {
    //     Optional<AssignedPazniciItem> findByBeneficiaryAndPunct(User beneficiary, String punct);
    // }

    /**
     * Alocă paznici unui beneficiar într-un punct de lucru.
     */
    @Transactional
    public User assignPaznici(AssignmentRequest request) {
        User beneficiary = userRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        if (beneficiary.getRole() != Role.BENEFICIAR) {
            throw new IllegalArgumentException("ID-ul specificat nu aparține unui beneficiar.");
        }

        List<User> pazniciToAssign = userRepository.findAllById(request.getPazniciIds());
        if (pazniciToAssign.size() != request.getPazniciIds().size()) {
            throw new IllegalArgumentException("Unul sau mai mulți paznici nu au fost găsiți.");
        }

        // Căutăm dacă există deja un AssignedPazniciItem pentru acest punct
        Optional<AssignedPazniciItem> existingItemOpt = assignedPazniciItemRepository
                .findByBeneficiaryAndPunct(beneficiary, request.getPunct());

        AssignedPazniciItem item;
        if (existingItemOpt.isPresent()) {
            item = existingItemOpt.get();
            // Punctul există: adăugăm paznicii noi (folosind proprietăți Java)
            for (User paznic : pazniciToAssign) {
                if (!item.getPaznici().contains(paznic)) {
                    item.getPaznici().add(paznic);
                }
            }
        } else {
            // Punctul nu există: creăm un nou AssignedPazniciItem
            item = new AssignedPazniciItem();
            item.setBeneficiary(beneficiary);
            item.setPunct(request.getPunct());
            item.setPaznici(pazniciToAssign);

            // Trebuie să adăugăm manual noul item la lista Beneficiarului (pentru a fi salvat)
            beneficiary.getAssignedPazniciItems().add(item);

            // Adăugăm punctul de lucru în lista Beneficiarului
            if (!beneficiary.getProfile().getPuncteDeLucru().contains(request.getPunct())) {
                beneficiary.getProfile().getPuncteDeLucru().add(request.getPunct());
            }
        }

        // Salvarea Beneficiarului (care va salva și AssignedPazniciItem)
        return userRepository.save(beneficiary);
    }

    /**
     * Dezalocă paznici unui beneficiar dintr-un punct de lucru.
     */
    @Transactional
    public User unassignPaznici(AssignmentRequest request) {
        User beneficiary = userRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        if (beneficiary.getRole() != Role.BENEFICIAR) {
            throw new IllegalArgumentException("ID-ul specificat nu aparține unui beneficiar.");
        }

        Optional<AssignedPazniciItem> existingItemOpt = assignedPazniciItemRepository
                .findByBeneficiaryAndPunct(beneficiary, request.getPunct());

        if (existingItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Punctul de lucru nu a fost găsit pentru acest beneficiar.");
        }

        AssignedPazniciItem item = existingItemOpt.get();
        List<User> pazniciList = item.getPaznici();

        // Eliminăm paznicii care au ID-urile specificate
        pazniciList.removeIf(p -> request.getPazniciIds().contains(p.getId()));

        // Dacă lista de paznici devine goală, ștergem AssignedPazniciItem-ul
        if (pazniciList.isEmpty()) {
            assignedPazniciItemRepository.delete(item);
        } else {
            assignedPazniciItemRepository.save(item); // Salvarea modificării
        }

        // Deoarece am folosit orphanRemoval=true, nu este strict necesară salvarea explicită
        // a Beneficiarului, dar o facem pentru consistență.
        return userRepository.findById(beneficiary.getId()).orElse(null);
    }

    /**
     * Preia toți paznicii alocați unui anumit beneficiar într-un punct de lucru.
     */
    public List<AssignedPaznicDTO> getAssignedPaznici(Long beneficiaryId, String punct) {
        User beneficiary = userRepository.findById(beneficiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        Optional<AssignedPazniciItem> punctObj = assignedPazniciItemRepository
                .findByBeneficiaryAndPunct(beneficiary, punct);


        if (punctObj.isEmpty()) {
            return List.of();
        }

        // Returnează lista de Useri mapați la DTO
        return punctObj.get().getPaznici().stream()
                .map(p -> new AssignedPaznicDTO(p.getId(), p.getNume(), p.getPrenume(), p.getEmail(), p.getProfile()))
                .collect(Collectors.toList());
    }
}