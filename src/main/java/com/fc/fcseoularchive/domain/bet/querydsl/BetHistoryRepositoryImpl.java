package com.fc.fcseoularchive.domain.bet.querydsl;

import com.fc.fcseoularchive.domain.bet.QBet;
import com.fc.fcseoularchive.domain.bet.QBetHistory;
import com.fc.fcseoularchive.domain.bet.dto.UnreadBetResultResponse;
import com.fc.fcseoularchive.domain.game.QGame;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BetHistoryRepositoryImpl implements BetHistoryRepositoryQuerydsl{

    private final JPAQueryFactory jpaqueryFactory;

    @Override
    public List<UnreadBetResultResponse> getUnreadBetResults(String loginId) {
        QBetHistory betHistory = QBetHistory.betHistory;
        QBet bet = QBet.bet;
        QGame game = QGame.game;

        return jpaqueryFactory
                .select(Projections.constructor(
                        UnreadBetResultResponse.class,
                        bet.id,
                        betHistory.id,
                        game.id,
                        new CaseBuilder() // opponent 계산
                                .when(game.homeTeam.eq("FC서울")).then(game.awayTeam)
                                .otherwise(game.homeTeam),
                        game.date,
                        game.result,
                        betHistory.totalPoint,
                        betHistory.payoutPoint
                ))
                .from(betHistory)
                .join(betHistory.game, game)
                .leftJoin(bet).on(bet.game.id.eq(game.id))
                .where(
                        betHistory.user.id.eq(loginId),
                        betHistory.isSettled.isTrue(),
                        betHistory.isChecked.isFalse()
                )
                .orderBy(game.date.desc())
                .fetch();
    }
}
