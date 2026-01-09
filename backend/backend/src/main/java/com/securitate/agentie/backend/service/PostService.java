package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.model.Post;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PostRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.securitate.agentie.backend.dto.AssignedWorkpointsResponse;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // Avem nevoie de el să găsim beneficiarul

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // Logica din 'createPost'
    public Post createPost(String numePost, String adresaPost, Long beneficiaryId, User adminCreator) {
        User beneficiary = userRepository.findById(beneficiaryId)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiarul nu a fost găsit."));

        Post newPost = new Post();
        newPost.setNumePost(numePost);
        newPost.setAdresaPost(adresaPost);
        newPost.setBeneficiary(beneficiary);
        newPost.setCreatedByAdmin(adminCreator);
        newPost.setQrCodeIdentifier(UUID.randomUUID().toString()); // Echivalentul uuidv4()

        return postRepository.save(newPost);
    }

    // Logica din 'getPosts'
    public List<Post> getPosts(User adminCreator) {
        return postRepository.findByCreatedByAdmin(adminCreator);
    }
    public List<AssignedWorkpointsResponse> getMyAssignedWorkpoints(User paznic) {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .collect(java.util.stream.Collectors.groupingBy(p -> p.getBeneficiary().getId()))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long beneficiarId = entry.getKey();
                    List<Post> lista = entry.getValue();

                    User beneficiar = lista.get(0).getBeneficiary();

                    String numeCompanie = null;
                    if (beneficiar.getProfile() != null) {
                        numeCompanie = beneficiar.getProfile().getNumeFirma();
                    }
                    if (numeCompanie == null || numeCompanie.isBlank()) {
                        // fallback ca să vezi ceva în dropdown
                        numeCompanie = beneficiar.getEmail(); // sau beneficiar.getNume() + " " + beneficiar.getPrenume()
                    }

                    List<String> puncte = lista.stream()
                            .map(Post::getNumePost)
                            .filter(s -> s != null && !s.isBlank())
                            .distinct()
                            .toList();

                    return new AssignedWorkpointsResponse(beneficiarId, numeCompanie, puncte);
                })
                .toList();
    }
}