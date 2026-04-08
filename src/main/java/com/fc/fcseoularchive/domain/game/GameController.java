package com.fc.fcseoularchive.domain.game;

import com.fc.fcseoularchive.security.CurrentUserProvider;
import com.fc.fcseoularchive.domain.game.dto.GameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2. GameController", description = "경기 관련 API")
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "경기 전체 일정 조회")
    @GetMapping("/all")
    public ResponseEntity<List<GameResponse>> getGames(Authentication authentication) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);

        List<GameResponse> response = gameService.getAllGames(loginId,null, null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "경기 전체 일정 조회 (년, 월 필터링)")
    @GetMapping
    public ResponseEntity<List<GameResponse>> getGames(Authentication authentication, @RequestParam Integer year, @RequestParam Integer month) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);

        List<GameResponse> response = gameService.getAllGames(loginId,year, month);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "경기 전체 일정 조회 - 로그인 전 (년, 월 필터링)")
    @GetMapping("/guest")
    public ResponseEntity<List<GameResponse>> getGamesForGuest(
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        List<GameResponse> response;

        response = gameService.getAllGamesForGuest(null, year, month);

        return ResponseEntity.ok(response);
    }

    // 경기 정보 가져 오기 200
    @Operation(summary = "경기 정보 검색")
    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGame (Authentication authentication, @PathVariable Long gameId) {
        String loginId = currentUserProvider.getCurrentUserId(authentication);

        GameResponse game = gameService.getGameById(loginId, gameId);
        return ResponseEntity.status(HttpStatus.OK).body(game);
    }




}
