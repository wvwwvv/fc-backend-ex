package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.entity.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// todo admin 관련은 admin 패키지에 작성 : merge 할 때 분리
// Auth 관련 parameter 는 제외하고 작성

@RestController
@RequestMapping("/api/admin/verifications")
@RequiredArgsConstructor
public class PostAdminController {

    private final PostService postService;

    // 모든 status 에 대해 param 으로 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostAdminResponse>> getPostsByStatus(
            @RequestParam(name = "status")PostStatus status
    ) {
        List<PostAdminResponse> response = postService.getPostsByStatus(status);
        return ResponseEntity.ok(response);
    }


    // 직관 인증 수락 - 200 ok
    @PostMapping("/posts/{verificationId}/approve")
    public ResponseEntity<Void> approvePost(
            @PathVariable Long verificationId
    ) {
        postService.ApprovePost(verificationId);
        return ResponseEntity.ok().build();
    }

    // 직관 인증 거절 - 204 No Content
    @PostMapping("/posts/{verificationId}/reject")
    public ResponseEntity<Void> rejectPost(
            @PathVariable Long verificationId
    ) {
        postService.RejectPost(verificationId);
        return ResponseEntity.noContent().build();
    }

    // 직관 인증 게시물 pending 으로 되돌리기 - 개발자용
    @PostMapping("/posts/{verificationId}/pending")
    public ResponseEntity<Void> resetPostToPending(
            @PathVariable Long verificationId
    ) {
        postService.resetPostToPending(verificationId);
        return ResponseEntity.ok().build();
    }

    // 직관 인증 게시물 draft 로 되돌리기 - 개발자용
    @PostMapping("/posts/{verificationId}/draft")
    public ResponseEntity<Void> resetPostToDraft(
            @PathVariable Long verificationId
    ) {
        postService.resetPostToDraft(verificationId);
        return ResponseEntity.ok().build();
    }




}
