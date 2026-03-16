package com.fc.fcseoularchive.player;

import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.player.dto.CreatePlayerRequest;
import com.fc.fcseoularchive.player.dto.PlayerResponse;
import com.fc.fcseoularchive.player.dto.PlayerResponseRank;
import com.fc.fcseoularchive.player.dto.UpdatePlayerReqeust;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Tag(name = "4. PlayerController")
@RequestMapping("/api/players")
@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "선수 전체 조회 (현역/임대/은퇴 모두)")
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayersV1() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersV1());
    }

    @Operation(summary = "선수 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getPlayer(id));
    }

    @Operation(summary = "현역 선수 전체 조회 + 랭킹")
    @GetMapping("/active")
    public ResponseEntity<List<PlayerResponseRank>> getAllPlayers() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersV2());
    }

    @Operation(summary = "FW + 현역 선수 전체 조회 + 랭킹")
    @GetMapping("/active/FW")
    public ResponseEntity<List<PlayerResponseRank>> getAllPlayersFW() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersFW());
    }

    @Operation(summary = "MF + 현역 선수 전체 조회 + 랭킹")
    @GetMapping("/active/MF")
    public ResponseEntity<List<PlayerResponseRank>> getAllPlayersMF() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersMF());
    }

    @Operation(summary = "DF + 선수 전체 조회 + 랭킹")
    @GetMapping("/active/DF")
    public ResponseEntity<List<PlayerResponseRank>> getAllPlayersDF() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersDF());
    }

    @Operation(summary = "GK + 선수 전체 조회 + 랭킹")
    @GetMapping("/active/GK")
    public ResponseEntity<List<PlayerResponseRank>> getAllPlayersGK() {
        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllPlayersGK());
    }







//    @Operation(summary = "현역 선수 전체 조회 (랭킹 포함x)")
//    @GetMapping("/active/old")
//    public ResponseEntity<List<PlayerResponse>> getActivePlayers() {
//        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllActivePlayers());
//    }
//
//    @Operation(summary = "현역 + FW 전체 조회 (랭킹 포함x)")
//    @GetMapping("/active/fw/old")
//    public ResponseEntity<List<PlayerResponse>> getActiveFWPlayers() {
//        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllFWActivePlayers());
//    }
//
//    @Operation(summary = "현역 + MF 전체 조회 (랭킹 포함x)")
//    @GetMapping("/active/mf/old")
//    public ResponseEntity<List<PlayerResponse>> getActiveMFPlayers() {
//        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllMFActivePlayers());
//    }
//
//    @Operation(summary = "현역 + DF 전체 조회 (랭킹 포함x)")
//    @GetMapping("/active/df/old")
//    public ResponseEntity<List<PlayerResponse>> getActiveDFPlayers() {
//        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllDFActivePlayers());
//    }
//
//    @Operation(summary = "현역 + GK 전체 조회 (랭킹 포함x)")
//    @GetMapping("/active/gk/old")
//    public ResponseEntity<List<PlayerResponse>> getActiveGKPlayers() {
//        return ResponseEntity.status(HttpStatus.OK).body(playerService.getAllGKActivePlayers());
//    }


}
