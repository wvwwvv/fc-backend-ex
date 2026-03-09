package com.fc.fcseoularchive.season_auth.dto;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.domain.enums.SeasonStatus;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Data
@NoArgsConstructor
public class SeasonResponse {


    private Long id;

    private String nickname; // 닉네임

    private String image;    // 이미지 url

    private SeasonStatus seasonStatus; // 상태 PENDING 만 나갈거긴 한데 혹시 모르니 같이 반환

    private LocalDateTime createdAt;

    public SeasonResponse(Long id, String nickname, String image, SeasonStatus seasonStatus, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.image = image;
        this.seasonStatus = seasonStatus;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public SeasonResponse(Seasonauth  seasonauth) {
        this.id = seasonauth.getId();
        this.nickname = seasonauth.getUser().getNickname();
        this.image = seasonauth.getImage();
        this.seasonStatus = seasonauth.getSeasonStatus();
        this.createdAt = seasonauth.getCreatedAt();
    }


}
