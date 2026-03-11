package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.entity.PostAuth;
import com.fc.fcseoularchive.entity.PostStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostAdminResponse {
    private Long postAuthId;
    private String nickname;
    private Long gameId;
    private LocalDateTime date;
    private String ticketImage;
    private PostStatus status;
    private LocalDateTime createdAt;

    public static PostAdminResponse from(PostAuth postAuth) {
        PostAdminResponse response = new PostAdminResponse();

        response.setPostAuthId(postAuth.getId());
        response.setNickname(postAuth.getPost().getUser().getNickname());
        response.setGameId(postAuth.getPost().getGame().getId());
        response.setDate(postAuth.getPost().getGame().getDate());
        response.setTicketImage(postAuth.getTicketImage());
        response.setStatus(postAuth.getStatus());
        response.setCreatedAt(postAuth.getPost().getCreatedAt());
        return response;
    }
}
