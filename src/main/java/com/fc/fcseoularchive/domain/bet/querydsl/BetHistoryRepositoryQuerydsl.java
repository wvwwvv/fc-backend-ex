package com.fc.fcseoularchive.domain.bet.querydsl;

import com.fc.fcseoularchive.domain.bet.BetHistory;
import com.fc.fcseoularchive.domain.bet.dto.UnreadBetResultResponse;

import java.util.List;

public interface BetHistoryRepositoryQuerydsl {
    // 읽지 않은 베팅 정산 결과를 game, bet 과 fetch join 하고 dto 타입으로 반환
    List<UnreadBetResultResponse> getUnreadBetResults(String loginId);
}
