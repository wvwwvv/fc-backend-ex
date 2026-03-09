package com.fc.fcseoularchive.admin;

import com.fc.fcseoularchive.entity.PostStatus;
import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.post.PostAdminResponse;
import com.fc.fcseoularchive.post.PostService;
import com.fc.fcseoularchive.user.dto.UserResponse;
import com.fc.fcseoularchive.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "0. AdminController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    @Operation(summary = "관리자용 회원 전체 조회")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<User> all = userService.getAll();

        List<UserResponse> list = all.stream()
                .map(user -> new UserResponse(user))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }




    // 모든 status 에 대해 param 으로 조회
    @Operation(summary = "status로 직관 인증 게시글 조회")
    @GetMapping("/verifications/posts")
    public ResponseEntity<List<PostAdminResponse>> getPostsByStatus(
            @RequestParam(name = "status") PostStatus status
    ) {
        List<PostAdminResponse> response = postService.getPostsByStatus(status);
        return ResponseEntity.ok(response);
    }


    // 직관 인증 수락 - 200 ok
    @Operation(summary = "게시글 status APPROVED 로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/approve")
    public ResponseEntity<Void> approvePost(
            @PathVariable Long postAuthId
    ) {
        postService.ApprovePost(postAuthId);
        return ResponseEntity.ok().build();
    }

    // 직관 인증 거절 - 204 No Content
    @Operation(summary = "게시글 status REJECTED 로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/reject")
    public ResponseEntity<Void> rejectPost(
            @PathVariable Long postAuthId
    ) {
        postService.RejectPost(postAuthId);
        return ResponseEntity.noContent().build();
    }

    // 직관 인증 게시물 pending 으로 되돌리기 - 개발자용
    @Operation(summary = "게시글 status PENDING 으로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/pending")
    public ResponseEntity<Void> resetPostToPending(
            @PathVariable Long postAuthId
    ) {
        postService.resetPostToPending(postAuthId);
        return ResponseEntity.ok().build();
    }

    // 직관 인증 게시물 draft 로 되돌리기 - 개발자용
    @Operation(summary = "게시글 status DRAFT 으로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/draft")
    public ResponseEntity<Void> resetPostToDraft(
            @PathVariable Long postAuthId
    ) {
        postService.resetPostToDraft(postAuthId);
        return ResponseEntity.ok().build();
    }

}
