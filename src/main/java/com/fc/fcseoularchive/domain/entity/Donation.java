package com.fc.fcseoularchive.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** User <-> Player 후원 N:M 중간 다리 테이블 */
@Entity @Getter
@NoArgsConstructor
@Table(name = "donation")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    // 반정규화 - donation 랭킹에 사용되는 nickname - fetch join 없이 바로 접근
    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

    public Donation(Integer point, User user, Player player, String nickname) {
        this.point = point;
        this.user = user;
        this.player = player;
        this.nickname = nickname;
    }

    public void addPoint(Integer point) {
        this.point += point;
    }

}
