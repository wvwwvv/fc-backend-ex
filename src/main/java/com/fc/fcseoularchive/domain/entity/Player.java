package com.fc.fcseoularchive.domain.entity;

import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.fc.fcseoularchive.domain.enums.PlayerStatus;
import com.fc.fcseoularchive.player.dto.CreatePlayerRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Getter
@Table(name = "players")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer backNumber;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerStatus status; // 현역, 임대, 이적

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerPosition position;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Player(CreatePlayerRequest req, String image) {
        this.name = req.getName();
        this.backNumber = req.getBackNumber();
        this.position = req.getPosition();
        this.image = image;
        this.status = req.getStatus();
        this.createdAt = LocalDateTime.now();
    }

    public void updateBackNumber(Integer backNumber) {
        this.backNumber = backNumber;
    }

    public void updatePosition(PlayerPosition position) {
        this.position = position;
    }


    public void updateStatus(PlayerStatus status) {
        this.status = status;
    }

    public void updateImage(String imagePath) {
        this.image = imagePath;
    }
}
