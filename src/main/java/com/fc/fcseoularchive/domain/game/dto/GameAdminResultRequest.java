package com.fc.fcseoularchive.domain.game.dto;

import com.fc.fcseoularchive.domain.game.GameResult;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameAdminResultRequest {
    private Integer homeScore;

    private Integer awayScore;

    private GameResult result;
}
