package com.fc.fcseoularchive.domain.bet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BetCreateRequest {
    @NotNull
    private Long gameId;

    @NotNull
    private Long winPoint;  // w, d, l 중 하나만 값이 있고 나머지는 0

    @NotNull
    private Long drawPoint;

    @NotNull
    private Long losePoint;
}
