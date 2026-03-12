package com.fc.fcseoularchive.post;


import com.fc.fcseoularchive.domain.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponse {
    private Long postId;
    private Long gameId;
    private String title; // 게시글 제목
    private String content;
    private LocalDateTime gameDate;
    private String thumbnail;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();

        // todo thumbnail

        response.setPostId(post.getId());
        response.setGameId(post.getGame().getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setGameDate(post.getGame().getDate());
        //response.setThumbnail();
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
