package com.fc.fcseoularchive.game;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "2. GameController", description = "경기 관련 API")
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @Operation(summary = "경기 전체 일정 조회")
    @GetMapping
    public ResponseEntity<List<GameResponse>> getGames() {
        List<GameResponse> response = gameService.getAllGames();

        return ResponseEntity.ok(response);
    }


    /*@Operation(summary = "특정 년도의 경기 조회")
    @GetMapping("/{year}")
    public ResponseEntity<List<GameResponse>> getGamesByYear(@PathVariable int year) {
        List<GameResponse> response = gameService.getAllGamesByYear(year);

        return ResponseEntity.ok(response);
    }*/
}
