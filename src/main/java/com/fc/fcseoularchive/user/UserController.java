package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.user.dto.LoginRequest;
import com.fc.fcseoularchive.user.dto.LoginResponse;
import com.fc.fcseoularchive.user.dto.UserCreateRequest;
import com.fc.fcseoularchive.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(userService.getUser(id)));
    }

    @Operation(summary = "유저 아이디로 1명 조회")
    @GetMapping("/user-id/{userId}")
    public ResponseEntity<UserResponse> getUserId(@PathVariable String userId) {
        User user = userService.getUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
    }

    @Operation(summary = "유저 닉네임으로 1명 조회")
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserResponse> getNickname(@PathVariable String nickname) {
        User user = userService.getNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req){
        LoginResponse login = userService.login(req);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }


}
