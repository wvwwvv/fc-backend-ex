package com.fc.fcseoularchive.domain.bet.querydsl;

import com.fc.fcseoularchive.domain.bet.BetHistory;
import com.fc.fcseoularchive.domain.bet.dto.BetHistoryResponse;
import com.fc.fcseoularchive.domain.bet.dto.UnreadBetResultResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BetHistoryRepositoryQuerydsl {
    // 읽지 않은 베팅 정산 결과를 game, bet 과 fetch join 하고 dto 타입으로 반환 (N+1 방지 Projection)
    List<UnreadBetResultResponse> getUnreadBetResults(String loginId);

    // 과거 경기 기준 유저 베팅 이력 조회 (N+1 방지 Projection)
    List<BetHistoryResponse> getBetHistory(String loginId, LocalDateTime now);

    // gameId로 찾고 user 와 fetch join
    List<BetHistory> findAllByGame_IdAndFetchUser(Long gameId);
}
