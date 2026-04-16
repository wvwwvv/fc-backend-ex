package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.game.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity @Getter
@NoArgsConstructor
@Table(name = "bet")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    private Long bettors = 0L; // 베팅한 전체 인원 수

    @Column(name = "total_point")
    private Long totalPoint = 0L;

    @Column(name = "win_point")
    private Long winPoint = 0L;

    @Column(name = "draw_point")
    private Long drawPoint = 0L;

    @Column(name = "lose_point")
    private Long losePoint = 0L;


    public Bet(Game game) {
        this.game = game;
        this.bettors = 0L;
        this.totalPoint = 0L;
        this.winPoint = 0L;
        this.drawPoint = 0L;
        this.losePoint = 0L;
    }

    // bet_history 업데이트 먼저 하고, 베팅 신규 유저인 경우 판단 필요
    public void addBetting(Long win, Long draw, Long lose, boolean isNewBettor) {
        if (isNewBettor) {
            this.bettors++;
        }
        this.winPoint += win;
        this.drawPoint += draw;
        this.losePoint += lose;
        this.totalPoint += (win + draw + lose);
    }

}
