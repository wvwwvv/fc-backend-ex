package com.fc.fcseoularchive.domain.auth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.fcseoularchive.domain.auth.dto.TokenResponse;
import com.fc.fcseoularchive.global.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${keycloak.url}") private String keycloakUrl;
    @Value("${keycloak.realm}") private String realm;
    @Value("${keycloak.client-id}") private String clientId;
    @Value("${keycloak.client-secret}") private String clientSecret;
    @Value("${frontend.redirect-uri}") private String redirectUri;

    private final TokenCacheService tokenCacheService; // 스프링 캐시 쓰기
    private final RestClient restClient = RestClient.create(); // Keyclaok과 통신하기 위함

    private String tokenUrl() {
        return keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }


    // 프론트에서 들어온 인가 코드 , codeVerifier 로 AccessToken, RefreshToken 발급 받기!
    public TokenResponse exchangeCodeForToken(String code, String codeVerifier) {
        TokenResponse token = requestToken(Map.of(
                "grant_type",    "authorization_code",
                "client_id",     clientId,
                "client_secret", clientSecret,
                "redirect_uri",  redirectUri,
                "code",          code,
                "code_verifier", codeVerifier
        ));
        saveRefreshToken(token);
        return token;
    }

    // 리프레시 토큰 반환하기
    public TokenResponse refreshToken(String userId) {
        String savedRefreshToken = tokenCacheService.getRefreshToken(userId);
        if (savedRefreshToken == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "500","리프레시 토큰을 찾을 수 없습니다.. 다시 로그인 해 주세요." );
        }

        TokenResponse token = requestToken(Map.of(
                "grant_type",    "refresh_token",
                "client_id",     clientId,
                "client_secret", clientSecret,
                "refresh_token", savedRefreshToken
        ));

        saveRefreshToken(token);
        return token;
    }

    // 리프레시 토큰 저장하기
    private void saveRefreshToken(TokenResponse token) {
        String userId = parseUserIdFromJwt(token.getAccessToken());
        tokenCacheService.saveRefreshToken(userId, token.getRefreshToken());
    }

    // param 으로 들어온 정보를 통해 토큰 POST 해서 발급 받기
    private TokenResponse requestToken(Map<String, String> params) {
        System.out.println("📌 tokenUrl: " + tokenUrl());
        System.out.println("📌 params: " + params);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        params.forEach(body::add);

        return restClient.post()
                .uri(tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TokenResponse.class);
    }

    // jwt 에서 유저의 id 파싱 하기
    // ex) "sub": "dae19fe0-e5e7-43ac-a492-e1eb395c2662" // 키클락에서 uuid 로 저장함
    private String parseUserIdFromJwt(String jwt) {
        String payload = jwt.split("\\.")[1];
        String decoded = new String(Base64.getUrlDecoder().decode(payload));
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(decoded);
            return node.get("sub").asText();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "401", "잘못된 JWT 입니다." );
        }
    }

}