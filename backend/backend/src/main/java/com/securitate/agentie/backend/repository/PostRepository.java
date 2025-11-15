package com.securitate.agentie.backend.repository;

import com.securitate.agentie.backend.model.Post;
import com.securitate.agentie.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Traducerea lui: Post.findOne({ qr_code_identifier: qrCode })
    Optional<Post> findByQrCodeIdentifier(String qrCodeIdentifier);

    // Traducerea lui: Post.find({ createdByAdminId: req.user._id })
    List<Post> findByCreatedByAdmin(User createdByAdmin);
}