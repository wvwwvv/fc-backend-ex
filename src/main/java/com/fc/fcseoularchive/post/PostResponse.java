package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.entity.PostAuth;
import com.fc.fcseoularchive.entity.PostStatus;
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
    private PostStatus status;
    private String thumbnail;
    private LocalDateTime createdAt;

    public static PostResponse from(PostAuth postAuth) {
        PostResponse response = new PostResponse();

        // Image db의 game_id == gameId 를 만족하는 첫 데이터의 image를 thumbnail로
        Long gameId = postAuth.getPost().getGame().getId();


        response.setPostId(postAuth.getPost().getId());
        response.setGameId(postAuth.getPost().getGame().getId());
        response.setTitle(postAuth.getPost().getTitle());
        response.setContent(postAuth.getPost().getContent());
        response.setGameDate(postAuth.getPost().getGame().getDate());
        response.setStatus(postAuth.getStatus());
        //response.setThumbnail();
        response.setCreatedAt(postAuth.getPost().getCreatedAt());
        return response;
    }
}
