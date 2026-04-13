package com.fc.fcseoularchive.domain.admin;

import com.fc.fcseoularchive.domain.bet.BetService;
import com.fc.fcseoularchive.domain.game.GameService;
import com.fc.fcseoularchive.domain.game.dto.GameAdminResultRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final GameService gameService;
    private final BetService betService;


    // admin : 경기 입력과 정산 동시에
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "betGame", allEntries = true),
            @CacheEvict(value = "betHistory", allEntries = true),
            @CacheEvict(value = "betSummary", allEntries = true)
    })
    public void updateGameAndSettle(Long gameId, GameAdminResultRequest request) {

        // 경기 결과 입력
        gameService.updateGameResult(gameId, request);

        // 베팅 정산
        betService.settleAllBet(gameId);
    }
}
