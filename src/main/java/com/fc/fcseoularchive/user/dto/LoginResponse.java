package com.fc.fcseoularchive.user.dto;

import com.fc.fcseoularchive.config.jwt.JwtToken;

import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.domain.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponse {

    private String grantType; // JWT에 대한 인증 타입, Bearer 인증 방식 사용할거임
    private String accessToken;
    private String refreshToken;

    private Long id;

    private String userId;

    private String nickname;

    private Role role;

    private Integer points;

    private Integer seasonTicket;

    public LoginResponse(JwtToken jwtToken, User user) {
        this.grantType = jwtToken.getGrantType();
        this.accessToken = jwtToken.getAccessToken();
        this.refreshToken = jwtToken.getRefreshToken();
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
        this.seasonTicket = LocalDateTime.now().getYear();
    }
}
