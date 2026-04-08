package com.fc.fcseoularchive.security;

import com.fc.fcseoularchive.global.error.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 시큐리티 컨텍스트에 있는 Authentication 에서 User의 id 만 꺼내주는 프로바이더
 */

@Component
public class CurrentUserProvider {

    /* id 꺼내오기 */
    public String getCurrentUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return (jwt.getClaimAsString("id")); // Claim 을 바로 String으로 변환
    }

    /* role 꺼내오기*/
    public String getCurrentRole(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        List<String> roles = jwt.getClaimAsStringList("role");

        if(roles == null){
            throw new ApiException(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED","401","잘못된 토큰 입니다.");
        }

        if(roles.contains("ADMIN")){
            return "ADMIN";
        }

        return "USER"; // 토큰 발급 받은 사람의 Default 는 "USER" 로 처리
    }


}
