package com.fc.fcseoularchive.post.querydsl;

import com.fc.fcseoularchive.domain.entity.Post;

import java.util.List;

public interface PostRepositoryQueryDsl {
    // 특정 유저가 특정 경기에 대한 직관 인증 작성 했는지
    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    // 특정 유저가 작성한 모든 직관 인증 게시글 조회
    List<Post> findByUser_Id(Long userId);
}
