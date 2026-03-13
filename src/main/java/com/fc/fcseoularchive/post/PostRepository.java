package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.domain.entity.Post;
import com.fc.fcseoularchive.post.querydsl.PostRepositoryQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryQueryDsl {

    // 게시물 id 와 유저 id로 post 찾기
    @Query("SELECT p FROM Post p JOIN FETCH p.game g JOIN FETCH p.user u WHERE p.id = :postId AND u.id = :loginId")
    Optional<Post> findByIdAndUserIdWithGame(@Param("postId") Long postId, @Param("loginId") Long loginId);
}
