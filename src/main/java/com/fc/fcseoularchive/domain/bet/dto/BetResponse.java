package com.fc.fcseoularchive.domain.bet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetResponse {
    private String userId; // user pk
    private Long betId; // bet pk
    private Long gameId; // game pk
    private LocalDateTime gameDate = null;

    private String opponent;

    private Long totalBettors; // 총 베팅한 사람 수
    private Long totalPoint; // 베팅 걸린 전체(모든 유저의) 포인트
    private Long winPoint;
    private Long drawPoint;
    private Long losePoint;

    private Long myWinPoint; // 내가 win 에 건 전체 포인트
    private Long myDrawPoint;
    private Long myLosePoint;
}
