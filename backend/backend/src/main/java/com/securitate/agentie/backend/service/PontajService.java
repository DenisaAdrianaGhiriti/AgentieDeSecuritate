package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.model.Pontaj;
import com.securitate.agentie.backend.model.Post;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PontajRepository;
import com.securitate.agentie.backend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PontajService {

    private final PontajRepository pontajRepository;
    private final PostRepository postRepository;

    public PontajService(PontajRepository pontajRepository, PostRepository postRepository) {
        this.pontajRepository = pontajRepository;
        this.postRepository = postRepository;
    }

    // Logica din 'checkIn'
    public Pontaj checkIn(String qrCode, User paznic) {
        Post post = postRepository.findByQrCodeIdentifier(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("Cod QR invalid sau post inexistent."));

        // Verifică dacă există deja o tură activă
        if (pontajRepository.findByPaznicAndOraIesireIsNull(paznic).isPresent()) {
            throw new IllegalStateException("Aveți deja o tură activă.");
        }

        Pontaj newPontaj = new Pontaj();
        newPontaj.setPaznic(paznic);
        newPontaj.setPost(post);
        newPontaj.setOraIntrare(LocalDateTime.now());

        return pontajRepository.save(newPontaj);
    }

    // Logica din 'checkOut'
    public Pontaj checkOut(User paznic) {
        Pontaj pontajActiv = pontajRepository.findByPaznicAndOraIesireIsNull(paznic)
                .orElseThrow(() -> new IllegalStateException("Nu a fost găsită nicio tură activă pentru check-out."));

        // Echivalentul lui findOneAndUpdate
        pontajActiv.setOraIesire(LocalDateTime.now());
        return pontajRepository.save(pontajActiv);
    }
}