package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

    // 특정 유저가 특정 경기에 대한 직관 인증 작성 했는지
    boolean existsByUserIdAndGameId(Long userId, Long gameId);


}
