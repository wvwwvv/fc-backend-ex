package com.fc.fcseoularchive.domain.entity;

import com.fc.fcseoularchive.domain.enums.SeasonStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "season_auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seasonauth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", unique = true)
    private User user;

    @Column( length = 255 ,nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    private SeasonStatus seasonStatus;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Seasonauth(User user, String image) {
        this.user = user;
        this.image = image;
        this.seasonStatus = SeasonStatus.PENDING;
        this.updatedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public void approve(){
        this.seasonStatus = SeasonStatus.APPROVED;
    }

    public void reject(){
        this.seasonStatus = SeasonStatus.REJECTED;
    }


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
