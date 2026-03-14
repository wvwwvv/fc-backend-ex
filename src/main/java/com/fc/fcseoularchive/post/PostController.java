package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.error.ApiException;
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
import java.util.Objects;

@Tag(name = "3. PostController", description = "직관 기록 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 직관 기록 작성
    @Operation(summary = "직관 기록 작성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createPost(Authentication authentication, @Valid @ModelAttribute PostCreateRequest request) throws IOException {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long loginId = Long.parseLong(jwt.getClaim("id"));

        postService.createPost(loginId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 본인 직관 인증 모든 게시물 데이터 조회
    @Operation(summary = "본인 직관 게시물 전체 조회 (일부 데이터)")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long loginId = Long.parseLong(jwt.getClaim("id"));

        List<PostResponse> response = postService.getPosts(loginId);
        return ResponseEntity.ok(response);
    }

    // 본인 직관 인증 게시물 상세 데이터 조회 : PostResponseDetail dto
    @Operation(summary = "본인 직관 게시물 1개 조회 (상세 데이터)")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDetail> getPostDetail(Authentication authentication, @PathVariable Long postId) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long loginId = Long.parseLong(jwt.getClaim("id"));

        PostResponseDetail response = postService.getPostDetail(postId, loginId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "본인 직관 게시물 1개 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable Long postId) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long loginId = Long.parseLong(jwt.getClaim("id"));

        postService.deletePost(postId, loginId);

        return ResponseEntity.noContent().build();

    }



}
