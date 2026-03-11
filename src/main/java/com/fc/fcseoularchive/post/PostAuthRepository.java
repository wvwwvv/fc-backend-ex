package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.entity.PostAuth;
import com.fc.fcseoularchive.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostAuthRepository extends JpaRepository<PostAuth, Long> {
    Optional<PostAuth> findByPost_Id(Long postId);
    List<PostAuth> findAllByStatus(PostStatus status);
}
