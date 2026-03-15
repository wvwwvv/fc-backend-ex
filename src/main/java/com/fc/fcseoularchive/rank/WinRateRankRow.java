package com.fc.fcseoularchive.rank;

// Projection 에 사용되는 interface
public interface WinRateRankRow {
    String getNickname();
    Long getWinCount(); // 승리 횟수
    Long getTotalCount(); // 전체 경기 횟수 (W, L, D가 있는 경기만)
}
