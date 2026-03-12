package com.fc.fcseoularchive.game;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GameResponse {
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
    private String result; // W, D, L

    private Boolean isAttended; // 직관 기록 작성한 경기 인지
}
