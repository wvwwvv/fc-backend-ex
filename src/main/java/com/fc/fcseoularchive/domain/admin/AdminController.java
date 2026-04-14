package com.fc.fcseoularchive.domain.admin;

import com.fc.fcseoularchive.domain.bet.BetService;
import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.game.GameRepository;
import com.fc.fcseoularchive.domain.game.dto.GameAdminRequest;
import com.fc.fcseoularchive.domain.game.GameService;
import com.fc.fcseoularchive.domain.game.dto.GameAdminResultRequest;
import com.fc.fcseoularchive.domain.player.PlayerService;
import com.fc.fcseoularchive.domain.player.dto.CreatePlayerRequest;
import com.fc.fcseoularchive.domain.player.dto.UpdatePlayerReqeust;
import com.fc.fcseoularchive.domain.post.dto.PostAdminResponse;
import com.fc.fcseoularchive.domain.post.PostService;
import com.fc.fcseoularchive.domain.user.dto.UserResponse;
import com.fc.fcseoularchive.domain.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "0. AdminController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final GameService gameService;
    private final PlayerService playerService;
    private final BetService betService;
    private final AdminService adminService;

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

    @Operation(summary = "선수 생성")
    @PostMapping(value = "/player", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPlayer(@Valid @ModelAttribute CreatePlayerRequest req) throws IOException {
        playerService.createPlayer(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "선수 정보 업데이트")
    @PutMapping(value = "/player/{id}", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePlayer(@PathVariable long id, @ModelAttribute UpdatePlayerReqeust req) throws IOException {
        playerService.updatePlayer(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "선수 삭제, 사용 x, update로 처리하기")
    @DeleteMapping("/player/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 추가예정

    @Operation(summary = "경기 결과 입력하고 바로 베팅 정산 적용, 해당 경기에 대해 정산을 완료한 유저가 한 명이라도 있으면 THROW")
    @PutMapping("/bet/settle/{gameId}")
    public ResponseEntity<Void> updateGameAndSettle(@PathVariable Long gameId, @RequestBody GameAdminResultRequest request) {
        adminService.updateGameAndSettle(gameId, request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "bet에 없는 경기로 bet 초깃값 생성")
    @PostMapping("/bet/db-update")
    public ResponseEntity<String> updateBetDB() {
        int createdCount = adminService.updateBetDB();
        return ResponseEntity.ok("bet 초기 데이터 " + createdCount + "건이 생성되었습니다.");
    }

}
