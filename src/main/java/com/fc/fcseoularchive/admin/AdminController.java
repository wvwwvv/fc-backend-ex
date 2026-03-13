package com.fc.fcseoularchive.admin;

import com.fc.fcseoularchive.domain.entity.Game;
import com.fc.fcseoularchive.game.GameAdminRequest;
import com.fc.fcseoularchive.game.GameService;
import com.fc.fcseoularchive.post.PostAdminResponse;
import com.fc.fcseoularchive.post.PostService;
import com.fc.fcseoularchive.user.dto.UserResponse;
import com.fc.fcseoularchive.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "0. AdminController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final GameService gameService;
    private final RequestService requestBuilder;

    @Operation(summary = "회원 전체 조회")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }


    // 모든 status 에 대해 param 으로 조회
    @Operation(summary = "모든 직관 인증 게시글 조회")
    @GetMapping("/verifications/posts/all")
    public ResponseEntity<List<PostAdminResponse>> getAllPosts() {
        List<PostAdminResponse> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }



    // 경기 정보 추가 201
    @Operation(summary = "경기 정보 추가")
    @PostMapping("/game")
    public ResponseEntity<Void> addGame(@RequestBody GameAdminRequest request) {
        gameService.addGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 경기 정보 가져 오기 200
    @Operation(summary = "경기 정보 검색")
    @GetMapping("/game/{gameId}")
    public ResponseEntity<Game> getGame (@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        return ResponseEntity.status(HttpStatus.OK).body(game);
    }

    // 경기 정보 수정 하기 200 (리소스 생성이 아닌, 기존 리소스를 수정하므로)
    @Operation(summary = "경기 정보 수정")
    @PutMapping("/game/{gameId}")
    public ResponseEntity<Game> updateGame (
            @PathVariable Long gameId,
            @RequestBody GameAdminRequest request
    ) {
        Game game = gameService.updateGame(gameId, request);
        return ResponseEntity.status(HttpStatus.OK).body(game);

    }

    // 경기 정보 삭제 하기 204
    @Operation(summary = "경기 정보 삭제")
    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<Void> deleteGame (@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

}
