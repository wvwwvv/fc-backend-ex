package com.fc.fcseoularchive.domain.post.dto;


import com.fc.fcseoularchive.domain.post.Post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostAdminResponse {
    private Long postId;
    private String nickname;
    private Long gameId;
    private LocalDateTime date;
    private LocalDateTime createdAt;

    public static PostAdminResponse from(Post post) {
        PostAdminResponse response = new PostAdminResponse();

        response.setPostId(post.getId());
        response.setNickname(post.getUser().getNickname());
        response.setGameId(post.getGame().getId());
        response.setDate(post.getGame().getDate());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
