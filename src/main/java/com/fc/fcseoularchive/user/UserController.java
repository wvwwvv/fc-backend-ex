package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
