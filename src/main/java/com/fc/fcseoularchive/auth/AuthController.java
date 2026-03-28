package com.fc.fcseoularchive.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.fcseoularchive.auth.dto.AccessTokenResponse;
import com.fc.fcseoularchive.auth.dto.CallbackRequest;
import com.fc.fcseoularchive.auth.dto.TokenResponse;
import com.fc.fcseoularchive.error.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@Tag(name = "8. AuthController")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 프론트 Callback.tsx에서 호출
    @Operation(summary = "콜백 API , 프론트 -> 서버로 인가코드, Verifier 보내주면 AccessToken 발급")
    @PostMapping("/callback")
    public ResponseEntity<AccessTokenResponse> callback(@RequestBody CallbackRequest request, HttpServletResponse response) {
        TokenResponse token = authService.exchangeCodeForToken(
                request.getCode(),
                request.getCodeVerifier()
        );
        // access_token만 프론트로 반환
        return ResponseEntity.status(HttpStatus.OK).body(new AccessTokenResponse(token.getAccessToken()));
    }

    // 프론트 인터셉터에서 401 시 자동 호출
    @Operation(summary = "refreshToken API AccessToken 만 헤더로 보내주면 됨")
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestHeader("Authorization") String bearerToken) {

        // 만료된 access_token에서 userId 추출
        String expiredToken = bearerToken.replace("Bearer ", "");
        String userId = parseUserIdFromExpiredJwt(expiredToken);

        TokenResponse token = authService.refreshToken(userId);

        return ResponseEntity.status(HttpStatus.OK).body(new AccessTokenResponse(token.getAccessToken()));
    }

    // jwt 에서 유저의 id 파싱 하기
    // ex) "sub": "dae19fe0-e5e7-43ac-a492-e1eb395c2662" // 키클락에서 uuid 로 저장함
    private String parseUserIdFromExpiredJwt(String jwt) {
        // 만료돼도 payload 파싱은 가능
        String payload = jwt.split("\\.")[1];
        String decoded = new String(Base64.getUrlDecoder().decode(payload));
        try {
            JsonNode node = new ObjectMapper().readTree(decoded);
            return node.get("sub").asText();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "401", "잘못된 JWT 입니다." );
        }
    }
}