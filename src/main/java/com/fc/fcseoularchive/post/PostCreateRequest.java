package com.fc.fcseoularchive.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    private Long gameId;
    private Long userId; // User 테이블의 id(PK)
    private String title;
    private String content;
    private List<String> images; // 현장 사진 리스트
}
