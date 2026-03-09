package com.fc.fcseoularchive.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    private Long gameId;
    private Long userId;
    private String title;
    private String content;
    private String ticketImage; // 일반 유저는 필수, 시즌권 유저는 null 가능
    private List<String> images; // 현장 사진 리스트
}
