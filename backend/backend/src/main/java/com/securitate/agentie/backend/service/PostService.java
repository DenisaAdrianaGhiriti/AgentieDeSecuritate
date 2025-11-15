package com.securitate.agentie.backend.service;

import com.securitate.agentie.backend.model.Post;
import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.repository.PostRepository;
import com.securitate.agentie.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

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
}