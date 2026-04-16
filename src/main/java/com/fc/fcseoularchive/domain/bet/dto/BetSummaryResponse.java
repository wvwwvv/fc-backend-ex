package com.fc.fcseoularchive.domain.bet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 캐시 사용 - 역직렬화 대비 미리 작성해둠
@AllArgsConstructor
public class BetSummaryResponse {
    private String userId; // user 의 PK
    private Long totalNumber;
    private Long gain;
    private Long loss;
}
