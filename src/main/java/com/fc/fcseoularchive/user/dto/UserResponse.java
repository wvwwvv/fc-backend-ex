package com.fc.fcseoularchive.user.dto;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.domain.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {

    private Long id;

    private String userId;

    private String nickname;

    private Role role;

    private Integer points;

    private Integer seasonTicket;


    public UserResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
        this.seasonTicket = LocalDateTime.now().getYear();
    }

    public UserResponse(Seasonauth seasonauth) {
        this.id = seasonauth.getUser().getId();
        this.userId = seasonauth.getUser().getUserId();
        this.nickname = seasonauth.getUser().getNickname();
        this.role = seasonauth.getUser().getRole();
        this.points = seasonauth.getUser().getPoints();
        this.seasonTicket = seasonauth.getCreatedAt().getYear();
    }
}
