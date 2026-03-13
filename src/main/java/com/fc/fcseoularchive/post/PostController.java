package com.fc.fcseoularchive.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 본인 직관 인증 모든 게시물 데이터 조회
    @Operation(summary = "본인 직관 게시물 전체 조회 (일부 데이터)")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdByString = authentication.getName();
        Long loginId = Long.parseLong(userIdByString); // 로그인 유저의 id

        List<PostResponse> response = postService.getPosts(loginId);
        return ResponseEntity.ok(response);
    }

    // 본인 직관 인증 게시물 상세 데이터 조회 : PostResponseDetail dto
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDetail> getPostDetail(@PathVariable Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdByString = authentication.getName();
        Long loginId = Long.parseLong(userIdByString); // 로그인 유저의 id

        PostResponseDetail response = postService.getPostDetail(postId, loginId);
        return ResponseEntity.ok(response);
    }



}
