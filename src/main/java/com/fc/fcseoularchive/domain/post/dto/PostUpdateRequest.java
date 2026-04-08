package com.fc.fcseoularchive.domain.post.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {

    @NotNull
    private String title;

    @NotNull
    private String content;

    private Long gameId;

    private List<String> existingImages;

    private List<MultipartFile> images;

}
