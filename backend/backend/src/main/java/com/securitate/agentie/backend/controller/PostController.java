package com.securitate.agentie.backend.controller;

import com.securitate.agentie.backend.model.User;
import com.securitate.agentie.backend.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // DTO simplu pentru request-ul de creare post
    // (Puteam face un fișier separat, dar e simplu și aici)
    static class CreatePostRequest {
        public String numePost;
        public String adresaPost;
        public Long beneficiaryId;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request,
                                        @AuthenticationPrincipal User adminCreator) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    postService.createPost(request.numePost, request.adresaPost, request.beneficiaryId, adminCreator)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getPosts(@AuthenticationPrincipal User adminCreator) {
        return ResponseEntity.ok(postService.getPosts(adminCreator));
    }

    @GetMapping("/my-assigned-workpoints")
    public ResponseEntity<?> getMyAssignedWorkpoints(@AuthenticationPrincipal User paznic) {
        return ResponseEntity.ok(postService.getMyAssignedWorkpoints(paznic));
    }
}