package com.fc.fcseoularchive.domain.post.dto;

import com.fc.fcseoularchive.domain.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponseDetail {
    private Long postId;
    private Long gameId;
    private String title; // 게시글 제목
    private String content;
    private LocalDateTime gameDate;
    private List<String> images;
    private LocalDateTime createdAt;

    public static PostResponseDetail from(Post post) {
        PostResponseDetail response = new PostResponseDetail();

        response.setPostId(post.getId());
        response.setGameId(post.getGame().getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setGameDate(post.getGame().getDate());
        // images 는 서비스 로직에서 처리
        response.setCreatedAt(post.getCreatedAt());

        return response;
    }
}
