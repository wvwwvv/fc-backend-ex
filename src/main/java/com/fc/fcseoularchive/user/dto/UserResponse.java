package com.fc.fcseoularchive.user.dto;

import com.fc.fcseoularchive.entity.Role;
import com.fc.fcseoularchive.entity.User;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class UserResponse {

    private Long id;

    private String userId;

    private String nickname;

    private Role role;

    private Integer points;

    private LocalDateTime seasonTicket;


    public UserResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
        this.seasonTicket = LocalDateTime.now();
    }

}
