package com.fc.fcseoularchive.domain.admin;

import com.fc.fcseoularchive.domain.bet.Bet;
import com.fc.fcseoularchive.domain.bet.BetRepository;
import com.fc.fcseoularchive.domain.bet.BetService;
import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.game.GameRepository;
import com.fc.fcseoularchive.domain.game.GameService;
import com.fc.fcseoularchive.domain.game.dto.GameAdminResultRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final GameService gameService;
    private final BetService betService;
    private final BetRepository betRepository;
    private final GameRepository gameRepository;


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

    // 여집합 (games - bet) 에 해당 하는 경기로 bet 초깃값 생성
    @Transactional
    public int updateBetDB() {
        List<Bet> betList = betRepository.findAll();
        List<Game> gameList = gameRepository.findAll();

        // bet 의 gameId로 된 set
        Set<Long> existingBetGameIds = betList.stream()
                .map(bet -> bet.getGame().getId())
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        // existingBetGameIds 에 없는 gameId 만 모으고 bet 생성해서 missingBets 에 모으기
        List<Bet> missingBets = gameList.stream()
                .filter(game -> !existingBetGameIds.contains(game.getId()))
                .map(Bet::new)
                .toList();

        if (!missingBets.isEmpty()) {
            betRepository.saveAll(missingBets);
        }

        return missingBets.size();
    }
}
