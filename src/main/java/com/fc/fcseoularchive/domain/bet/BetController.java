package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.bet.dto.BetCreateRequest;
import com.fc.fcseoularchive.domain.bet.dto.BetResponse;
import com.fc.fcseoularchive.domain.bet.dto.BetSummaryResponse;
import com.fc.fcseoularchive.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> createBet(Authentication authentication, @Valid BetCreateRequest request) {

        String loginId = currentUserProvider.getCurrentUserId(authentication);
        betService.createBet(loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
