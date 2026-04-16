package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "bet_history",
        uniqueConstraints ={
                @UniqueConstraint(columnNames = {"user_id", "game_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;

    @Column(name = "total_point")
    private Long totalPoint = 0L;

    @Column(name = "win_point")
    private Long winPoint = 0L;

    @Column(name = "draw_point")
    private Long drawPoint = 0L;

    @Column(name = "lose_point")
    private Long losePoint = 0L;

    @Column(nullable = false)
    private String opponent;

    @Column(name = "is_settled", nullable = false)
    private boolean isSettled = false; // 정산 완료되면 true

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked = false; // 유저가 모달창 확인하면 true

    @Column(name = "payout_point")
    private Long payoutPoint = 0L; // 베팅으로 얻은 포인트

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Builder
    public BetHistory(User user, Game game, LocalDateTime gameDate, String opponent) {
        this.user = user;
        this.game = game;
        this.gameDate = gameDate;
        this.opponent = opponent;
        this.totalPoint = 0L;
        this.winPoint = 0L;
        this.drawPoint = 0L;
        this.losePoint = 0L;
    }

    // 베팅 금액 추가
    public void addPoints(Long win, Long draw, Long lose) {
        this.winPoint += win;
        this.drawPoint += draw;
        this.losePoint += lose;
        this.totalPoint += (win + draw + lose);
    }

    // 정산 처리
    public void settle(Long payout) {
        this.isSettled = true;
        this.payoutPoint = payout;
        this.isChecked = false; // 정산되면 유저가 확인 필요
    }

    // 유저가 정산 알림 모달 닫으면 호출
    public void markAsChecked() {
        this.isChecked = true;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
