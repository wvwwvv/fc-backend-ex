package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. UserController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입")
    @PostMapping("/join")
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest req) {
        userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "id로 유저 1명 조회")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long loginId = Long.parseLong(jwt.getClaim("id"));

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(loginId));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req){
        LoginResponse login = userService.login(req);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }

    @Operation(summary = "리프레시 토큰 및 일반 토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshReqeust req){
        return ResponseEntity.status(HttpStatus.OK).body(userService.refresh(req));
    }

    //    @Operation(summary = "유저 아이디로 1명 조회")
//    @GetMapping("/user-id")
//    public ResponseEntity<UserResponse> getUserId(@RequestParam String userId) {
//        User user = userService.getUserId(userId);
//        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
//    }
//
//    @Operation(summary = "유저 닉네임으로 1명 조회")
//    @GetMapping("/nickname")
//    public ResponseEntity<UserResponse> getNickname(@RequestParam String nickname) {
//        User user = userService.getNickname(nickname);
//        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
//    }

    /**
     * 로그아웃은 별도 API 로 만들지 않는다.
     * 로그아웃 방법
     * 1. 프론트엔드에서 토큰 삭제
     *  -> 가장 간단함
     *  -> 단, 서버에서는 토큰이 유효함
     *
     * 2. 토큰 블랙리스트 관리
     *  -> 토큰 유효 관리 가능
     *  -> 단, 매 요청마다 블랙리스트 확인 즉, stateless 불가능
     *
     * 3. 리프레시 토큰의 만료처리
     *  -> 토큰 재발급 차단 가능
     *  -> 단, 2번 과 마찬가지로 stateless 불가능
     *
     *  2,3 은 구현이 난이도 증가 및 stateless 불가능 그래도 보안관점 좋음
     *  1 은 구현 난이도 쉬움 대신 stateless 가능 보안관점 안좋음 (우리는 시간이 많지 않고 토큰 유실은 어차피 클라이언트 잘못이라 1번으로 채택했었음)
     */

}
