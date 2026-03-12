package com.fc.fcseoularchive.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fc.fcseoularchive.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "users")

@NoArgsConstructor(access = AccessLevel.PROTECTED) // PROTECTED : 외부에서 new User() 막기
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 50, unique = true)
    private String userId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String userId, String password, String nickname) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.role = Role.USER;
        this.points = 0;
        this.profileImage = null;
        this.lastLogin = null;
        this.deletedAt = null;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.role == null) {
            this.role = Role.USER;
        }
        if (this.points == null) {
            this.points = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

}