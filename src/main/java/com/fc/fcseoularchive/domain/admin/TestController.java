package com.fc.fcseoularchive.domain.admin;

import com.fc.fcseoularchive.security.CurrentUserProvider;
import com.fc.fcseoularchive.global.error.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "98. TestController")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CurrentUserProvider currentUserProvider;

    /**
     * 권한이나 이름 잘꺼내지는지 테스트
     */
    @Operation(summary = "토큰에 id, nickname, role 을 꺼내보는 테스트 API")
    @GetMapping("/token")
    public ResponseEntity<TestDto> test(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String id = jwt.getClaim("id");


//        String newId = jwt.getId();

        String username = jwt.getClaimAsString("nickname"); // 유저 네임 꺼내는 방법

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        List<String> roles = List.of();
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roleList) {
            roles = roleList.stream()
                    .filter(obj -> String.class.isInstance(obj))
                    .map(obj1 -> String.class.cast(obj1))
                    .toList();
        }

        return ResponseEntity.ok(new TestDto(id, username, roles));
    }

    /**
     * 토큰 구조가 조금 바뀌어서 유저 id 꺼내지는지 테스트
     */
    @Operation(summary = "ADMIN 계정만 통과하는 API")
    @GetMapping("/me")
    public ResponseEntity<TestDto2> me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();


        String id = jwt.getClaim("id");
        String nickname = jwt.getClaim("nickname");
        List<String> list1 = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority().toString())
                .toList();

        if(list1.contains("ROLE_USER")){
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "ROLE_USER가 들어있습니돠!!!");
        }

        TestDto2 testDto2 = new TestDto2(id, nickname, list1);

        // ROLE_ADMIN or ROLE_USER 만 들어있지만, 테스트용으로 ROLE_ADMIN 만 통과
        return ResponseEntity.status(HttpStatus.OK).body(testDto2);


    }

    @GetMapping("/auth_get")
    public ResponseEntity<String> getAuth(Authentication authentication) {
        String authId = currentUserProvider.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(authId);
    }



    // 1. 테스트용 DTO
    public record TestDto(
            String id,
            String username,
            List<String> roles
    ) {
    }

    // 2. 테스트용 DTO
    public record TestDto2(
            String id,
            String nickname,
            List<String> roles
    ){

    }


}
