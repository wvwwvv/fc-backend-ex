package com.fc.fcseoularchive.domain.bet.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BetHistoryIdsRequest {
    private List<Long> betHistoryIds;
}
