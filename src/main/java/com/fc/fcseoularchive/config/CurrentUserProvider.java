package com.fc.fcseoularchive.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * 시큐리티 컨텍스트에 있는 Authentication 에서 User의 id 만 꺼내주는 프로바이더
 */

@Component
public class CurrentUserProvider {

    public Long getCurrentUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return Long.parseLong(jwt.getClaim("id"));
    }

}
