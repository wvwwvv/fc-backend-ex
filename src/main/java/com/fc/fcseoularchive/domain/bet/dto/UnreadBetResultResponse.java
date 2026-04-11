package com.fc.fcseoularchive.domain.bet.dto;

import com.fc.fcseoularchive.domain.game.GameResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // 캐시 사용 - 역직렬화 대비 미리 작성해둠
@AllArgsConstructor
public class UnreadBetResultResponse {
    private Long betId; // bet 의 PK
    private Long betHistoryId; // betHistory 의 PK
    private Long gameId; // game 의 PK
    private String opponent;
    private LocalDateTime gameDate;
    private GameResult gameResult; // W,D,L
    private Long totalPoint; // 내가 베팅한 총액
    private Long payoutPoint; // 이번 베팅으로 얻은 금액 -
}
