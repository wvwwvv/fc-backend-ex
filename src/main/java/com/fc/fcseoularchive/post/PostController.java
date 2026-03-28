package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.config.CurrentUserProvider;
import com.fc.fcseoularchive.post.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "3. PostController", description = "직관 기록 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CurrentUserProvider currentUserProvider;

    // 직관 기록 작성
    @Operation(summary = "직관 기록 작성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(Authentication authentication, @Valid @ModelAttribute PostCreateRequest request) throws IOException {
        Long loginId = currentUserProvider.getCurrentUserId(authentication);
        postService.createPost(loginId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 본인 직관 인증 모든 게시물 데이터 조회 (승,무,패)
    @Operation(summary = "본인 직관 게시물 전체 조회 + 본인 승률")
    @GetMapping
    public ResponseEntity<PostGetAllResponse> getPosts(Authentication authentication) {
        Long loginId = currentUserProvider.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPosts(loginId));
    }

    // 본인 직관 인증 게시물 상세 데이터 조회 : PostResponseDetail dto
    @Operation(summary = "본인 직관 게시물 1개 조회 (상세 데이터)")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDetail> getPostDetail(Authentication authentication, @PathVariable Long postId) {
        Long loginId = currentUserProvider.getCurrentUserId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostDetail(postId, loginId));
    }

    @Operation(summary = "본인 직관 게시물 1개 수정")
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updatePost(Authentication authentication, @Valid @ModelAttribute PostUpdateRequest request, @PathVariable Long postId) throws IOException {
        Long loginId = currentUserProvider.getCurrentUserId(authentication);
        postService.updatePost(postId, loginId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "본인 직관 게시물 1개 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable Long postId) {
        Long loginId = currentUserProvider.getCurrentUserId(authentication);
        postService.deletePost(postId, loginId);
        return ResponseEntity.noContent().build();

    }



}
