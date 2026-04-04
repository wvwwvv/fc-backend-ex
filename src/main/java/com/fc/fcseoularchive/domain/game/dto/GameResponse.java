package com.fc.fcseoularchive.domain.game.dto;

import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.game.GameResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse implements Serializable {
    private Long id;
    private LocalDateTime date;
    private Integer round;

    private String homeTeam;
    private String awayTeam;
    private String opponent; // 상대팀

    private String stadium;

    // nullable
    private Integer homeScore;
    private Integer awayScore;

    private String status; // SCHEDULED (경기전) | FINISHED (경기후)
    private GameResult result; // W, D, L

    private Boolean isAttended; // 직관 기록 작성한 경기 인지

    public GameResponse(Game game,String opponent, String status, Boolean isAttended) {
        this.id = game.getId();
        this.date = game.getDate();
        this.round = game.getRound();
        this.homeTeam = game.getHomeTeam();
        this.awayTeam = game.getAwayTeam();
        this.opponent = opponent;
        this.stadium = game.getStadium();
        this.homeScore = game.getHomeScore();
        this.awayScore = game.getAwayScore();
        this.status = status;
        this.result = game.getResult();
        this.isAttended = isAttended;
    }
}
