package com.fc.fcseoularchive.domain.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WinRateRankResponse {
    private int rank;
    private String nickname;
    private double winRate;
}
