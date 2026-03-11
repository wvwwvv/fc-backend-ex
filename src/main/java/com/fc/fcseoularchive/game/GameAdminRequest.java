package com.fc.fcseoularchive.game;

import com.fc.fcseoularchive.entity.GameResult;
import jakarta.persistence.*;
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
