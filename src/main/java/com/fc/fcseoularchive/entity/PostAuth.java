package com.fc.fcseoularchive.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "post_auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // nullable
    // 시즌권 사용자는 null : 프론트에서 넘겨줄 필요 없음
    @Column(name = "ticket_image", length = 512)
    private String ticketImage; // null 이면 시즌권 사용자 의미

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.PENDING;




    @Builder
    public PostAuth(Post post, String ticketImage, PostStatus status) {
        this.post = post;
        this.ticketImage = ticketImage;
        this.status = status;

    }


    public void approve() {
        this.status = PostStatus.APPROVED;
    }

    public void reject() {
        this.status = PostStatus.REJECTED;
    }

    public void resetToPending() {
        this.status = PostStatus.PENDING;
    }

    public void resetToDraft() {
        this.status = PostStatus.DRAFT;
    }
}
