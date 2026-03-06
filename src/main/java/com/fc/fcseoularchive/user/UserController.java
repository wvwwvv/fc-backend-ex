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






    /**
     * 테스트용 (JWT , Security 등등 제외)
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest req) {
        userService.createUser(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<User> getUser(@RequestParam String userId) {
        return ResponseEntity.ok().body(userService.getUser(userId));
    }



}
