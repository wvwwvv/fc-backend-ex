package com.fc.fcseoularchive.post;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {
    @NotNull
    private Long gameId;

    @NotNull
    private Long userId; // User 테이블의 id(PK)

    @NotNull
    private String title;

    @NotNull
    private String content;

    private List<MultipartFile> images; // 현장 사진 리스트
}
