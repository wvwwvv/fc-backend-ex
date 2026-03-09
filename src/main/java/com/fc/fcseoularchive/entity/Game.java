package com.fc.fcseoularchive.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="games")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 80)
    private String stadium;

    @Column(nullable = false)
    private Integer round;

    @Column(name="home_team", nullable = false, length = 30)
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 30)
    private String awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private GameResult result;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Game(LocalDateTime date, String stadium, Integer round,
                String homeTeam, String awayTeam, Integer homeScore,
                Integer awayScore, GameResult result, LocalDateTime deletedAt) {
        this.date = date;
        this.stadium = stadium;
        this.round = round;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.result = result;
        this.deletedAt = deletedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateScore(Integer homeScore, Integer awayScore, GameResult result) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.result = result;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // admin : 게임 정보 update
    public void adminUpdate(LocalDateTime date, String stadium, Integer round,
                            String homeTeam, String awayTeam, Integer homeScore,
                            Integer awayScore, GameResult result, LocalDateTime deletedAt) {
        this.date = date;
        this.stadium = stadium;
        this.round = round;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.result = result;
        this.deletedAt = deletedAt;
        this.updatedAt = LocalDateTime.now();
    }
}
