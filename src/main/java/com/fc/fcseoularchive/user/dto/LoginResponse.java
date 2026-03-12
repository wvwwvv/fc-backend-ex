package com.fc.fcseoularchive.user.dto;

import com.fc.fcseoularchive.config.jwt.JwtToken;

import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.domain.enums.Role;
import lombok.Getter;


@Getter
public class LoginResponse {

    private final String grantType; // JWT에 대한 인증 타입, Bearer 인증 방식 사용할거임
    private final String accessToken;
    private final String refreshToken;

    private final Long id;

    private final String userId;

    private final String nickname;

    private final Role role;

    private final Integer points;

    public LoginResponse(JwtToken jwtToken, User user) {
        this.grantType = jwtToken.getGrantType();
        this.accessToken = jwtToken.getAccessToken();
        this.refreshToken = jwtToken.getRefreshToken();
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
    }
}
