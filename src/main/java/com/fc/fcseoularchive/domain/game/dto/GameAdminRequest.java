package com.fc.fcseoularchive.domain.game.dto;

import com.fc.fcseoularchive.domain.game.GameResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GameAdminRequest {

    private LocalDateTime date;

    private String stadium;

    private Integer round;

    private String homeTeam;

    private String awayTeam;

    private Integer homeScore;

    private Integer awayScore;

    private GameResult result;

    private LocalDateTime deletedAt;

}
