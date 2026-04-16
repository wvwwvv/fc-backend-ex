package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.bet.dto.*;
import com.fc.fcseoularchive.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "7. BetController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bet")
public class BetController {

    private final BetService betService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "탭 바 표시 - 베팅 전적")
    @GetMapping("/record")
    public ResponseEntity<BetSummaryResponse> getBetSummary(Authentication authentication) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        BetSummaryResponse response = betService.getBetSummary(loginId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "베팅중인 경기")
    @GetMapping()
    public ResponseEntity<BetResponse> getBet(Authentication authentication) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        BetResponse response = betService.getBet(loginId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "베팅 하기")
    @PostMapping
    public ResponseEntity<Void> createBet(Authentication authentication, @Valid @RequestBody BetCreateRequest request) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        betService.createBet(loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "이전 베팅 기록들")
    @GetMapping("/history")
    public ResponseEntity<List<BetHistoryResponse>> getBetHistory(Authentication authentication) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        List<BetHistoryResponse> response = betService.getBetHistory(loginId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "확인하지 않은 베팅 정산 결과 - 로그인 후 호출")
    @GetMapping("/unread")
    public ResponseEntity<List<UnreadBetResultResponse>> getUnreadBetResult(Authentication authentication) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        List<UnreadBetResultResponse> response = betService.getUnreadBetResult(loginId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "확인하지 않은 베팅 정산 결과 -> 확인 했음으로 변경")
    @PutMapping("/unread/check")
    public ResponseEntity<Void> checkUnreadBetResult(Authentication authentication, @RequestBody BetHistoryIdsRequest request) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        betService.checkUnreadBetResult(loginId, request);

        // 204 ok
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
