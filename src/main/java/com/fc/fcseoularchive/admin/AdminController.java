package com.fc.fcseoularchive.admin;

import com.fc.fcseoularchive.domain.entity.Game;
import com.fc.fcseoularchive.game.GameAdminRequest;
import com.fc.fcseoularchive.game.GameService;
import com.fc.fcseoularchive.post.PostAdminResponse;
import com.fc.fcseoularchive.post.PostService;
import com.fc.fcseoularchive.user.dto.UserResponse;
import com.fc.fcseoularchive.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
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
    private final GameService gameService;
    private final RequestService requestBuilder;

    @Operation(summary = "회원 전체 조회")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll());
    }

    /*@Operation(summary = "시즌권 전체 수락 (PENDING->APPROVED)")
    @PostMapping("/season-auth/approveAll")
    public ResponseEntity<Void> seasonApproveAll(){
        seasonauthService.approveAll();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @Operation(summary = "시즌권 대기 전체 조회")
    @GetMapping("/season-auth/pending")
    public ResponseEntity<List<SeasonResponse>> seasonGetAll(){
        return ResponseEntity.status(HttpStatus.OK).body(seasonauthService.getAll());
    }


    @Operation(summary = "시즌권 수락 단건")
    @PostMapping("/season-auth/approved/{seasonAuthId}")
    public ResponseEntity<Void> seasonApprove(@PathVariable Long seasonAuthId){
        seasonauthService.approve(seasonAuthId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "시즌권 거절 단건")
    @PostMapping("/season-auth/rejected/{seasonAuthId}")
    public ResponseEntity<Void> seasonReject(@PathVariable Long seasonAuthId){
        seasonauthService.rejected(seasonAuthId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }*/


    // 모든 status 에 대해 param 으로 조회
    @Operation(summary = "모든 직관 인증 게시글 조회")
    @GetMapping("/verifications/posts/all")
    public ResponseEntity<List<PostAdminResponse>> getAllPosts() {
        List<PostAdminResponse> response = postService.getAllPosts();
        return ResponseEntity.ok(response);
    }


    /*// 모든 status 에 대해 param 으로 조회
    @Operation(summary = "status로 직관 인증 게시글 조회")
    @GetMapping("/verifications/posts")
    public ResponseEntity<List<PostAdminResponse>> getPostsByStatus(
            @RequestParam(name = "status") PostStatus status
    ) {
        List<PostAdminResponse> response = postService.getPostsByStatus(status);
        return ResponseEntity.ok(response);
    }*/


    /*// 직관 인증 수락 - 200 ok
    @Operation(summary = "게시글 status APPROVED 로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/approve")
    public ResponseEntity<Void> approvePost(
            @PathVariable Long postAuthId
    ) {
        postService.approvePost(postAuthId);
        return ResponseEntity.ok().build();
    }

    // 직관 인증 거절 - 204 No Content
    @Operation(summary = "게시글 status REJECTED 로 변경")
    @PostMapping("/verifications/posts/{postAuthId}/reject")
    public ResponseEntity<Void> rejectPost(
            @PathVariable Long postAuthId
    ) {
        postService.rejectPost(postAuthId);
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

    // PENDING 게시물 전부 APPROVE 로 수락
    @Operation(summary = "PENDING 인 모든 게시글 APPROVED 로 변경")
    @PostMapping("/verifications/posts/all/approve")
    public ResponseEntity<Void> postApproveAll() {
        postService.approveAll();
        return ResponseEntity.ok().build();
    }*/





    // 경기 정보 추가 201
    @Operation(summary = "경기 정보 추가")
    @PostMapping("/game")
    public ResponseEntity<Void> addGame(@RequestBody GameAdminRequest request) {
        gameService.addGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 경기 정보 가져 오기 200
    @Operation(summary = "경기 정보 검색")
    @GetMapping("/game/{gameId}")
    public ResponseEntity<Game> getGame (@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        return ResponseEntity.status(HttpStatus.OK).body(game);
    }

    // 경기 정보 수정 하기 200 (리소스 생성이 아닌, 기존 리소스를 수정하므로)
    @Operation(summary = "경기 정보 수정")
    @PutMapping("/game/{gameId}")
    public ResponseEntity<Game> updateGame (
            @PathVariable Long gameId,
            @RequestBody GameAdminRequest request
    ) {
        Game game = gameService.updateGame(gameId, request);
        return ResponseEntity.status(HttpStatus.OK).body(game);

    }

    // 경기 정보 삭제 하기 204
    @Operation(summary = "경기 정보 삭제")
    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<Void> deleteGame (@PathVariable Long gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

}
