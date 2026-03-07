package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. UserController", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/join")
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest req){
        userService.createUser(req);
        return ResponseEntity.ok().build();
    }



}
