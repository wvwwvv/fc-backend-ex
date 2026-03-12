package com.fc.fcseoularchive.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. PostController", description = "직관 기록 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    // 직관 기록 작성
    @Operation(summary = "직관 기록 작성")
    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestBody PostCreateRequest request
    ) {
        // userId null 체크
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().build();
        }

        postService.createPost(request.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // todo 본인 직관 인증 모든 게시물 일부 데이터 조회 - 개발중
    @GetMapping
    public String getPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return name; // User 의 userId

        /*PostResponse response = postService.getPosts();
        return ResponseEntity.ok(response);*/
    }

}
