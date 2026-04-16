package com.fc.fcseoularchive.domain.user;

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
    @Column(length = 36) // '_' 하이픈 포함 UUID 로 사용
    private String id;

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // default 값 지움 -> 무조건 User 들어옴

    @Column(nullable = false)
    private Integer points = 0;

    @Column(name = "profile_image", length = 512)
    private String profileImage;

    @Column(name = "attendance_streak")
    private Integer attendanceStreak;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public User(String userId, String nickname, Role role) {
        this.id = userId;
        this.nickname = nickname;
        this.role = role;
        this.points = 0;
        this.attendanceStreak = 0;
        this.profileImage = null;
        this.lastLogin = null;
        this.deletedAt = null;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.points == null) {
            this.points = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void subtractPoints(int point) {
        this.points = this.points - point;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public void lastLoginUpdate(){
        this.lastLogin = LocalDateTime.now();
    }

    public void addAttendanceStreak() {
        this.attendanceStreak += 1 ;
    }

    public void initAttendanceStreak() {
        this.attendanceStreak = 1;
    }

}