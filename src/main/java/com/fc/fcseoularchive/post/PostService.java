package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.image.ImageRepository;
import com.fc.fcseoularchive.entity.*;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.game.GameRepository;
import com.fc.fcseoularchive.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PostAuthRepository postAuthRepository;

    // PostCreateRequest 에
    public void createPost(Long id, PostCreateRequest request) { // Long 타입의 id 사용 주의
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

        // 시즌권 사용 여부에 따라 Post 의 상태 정의
        PostStatus status = (user.getSeasonTicket() != null)
                ? PostStatus.APPROVED // 시즌권 유저는 바로 APPROVED
                : PostStatus.PENDING;

        // Post 저장
        Post post = Post.builder()
                .user(user)
                .game(game)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);

        // Image 저장
        // Post 테이블에 저장되는 이미지는 ticketImage가 유일
        // 이미지들은 따로 Image 테이블에 저장
        // 프론트의 request 에 image 필드가 있고, 비어있지 않을 때
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<Image> images = request.getImages().stream().map(url -> Image.builder()
                    .user(user)
                    .game(game)
                    .image(url)
                    .build()
            ).toList();
            imageRepository.saveAll(images);
        }

        // PostAuth 저장
        PostAuth postAuth = PostAuth.builder()
                .post(post)
                .ticketImage(request.getTicketImage())
                .status(status)
                .build();

        postAuthRepository.save(postAuth);

    }

   /* @Transactional(readOnly = true)
    public List<PostResponse> getPosts() {
        return postAuthRepository.f
    }*/


    // admin : 모든 인증 게시글 조회
    @Transactional(readOnly = true)
    public List<PostAdminResponse> getAllPosts() {
        return postAuthRepository.findAll()
                .stream()
                .map(PostAdminResponse::from)
                .collect(Collectors.toList());
    }

    // admin : status 필터링 직관 인증 게시글 조회
    @Transactional(readOnly = true)
    public List<PostAdminResponse> getPostsByStatus(PostStatus status) {
        return postAuthRepository.findAllByStatus(status)
                .stream()
                .map(PostAdminResponse::from)
                .collect(Collectors.toList());
    }


    // admin : 직관 인증 게시글 승인
    public void approvePost(Long postAuthId) {
        PostAuth postAuth = postAuthRepository.findById(postAuthId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "해당 게시글의 인증 정보를 찾을 수 없습니다."));

        postAuth.approve();
    }

    // admin : 직관 인증 게시글 거절
    public void rejectPost(Long postAuthId) {
        PostAuth postAuth = postAuthRepository.findById(postAuthId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "해당 게시글의 인증 정보를 찾을 수 없습니다."));

        postAuth.reject();
    }

    // admin : 직관 인증 게시글 PENDING 으로
    public void resetPostToPending(Long postAuthId) {
        PostAuth postAuth = postAuthRepository.findById(postAuthId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "해당 게시글의 인증 정보를 찾을 수 없습니다."));

        postAuth.resetToPending();
    }

    // admin : 직관 인증 게시글 DRAFT 으로
    public void resetPostToDraft(Long postAuthId) {
        PostAuth postAuth = postAuthRepository.findById(postAuthId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "해당 게시글의 인증 정보를 찾을 수 없습니다."));

        postAuth.resetToDraft();
    }


    // admin : 직관 인증 게시글 PENDING 인 것만 전부 APPROVE
    public void approveAll() {
        List<Long> pendingPostIds = postAuthRepository.findAllByStatus(PostStatus.PENDING)
                .stream()
                .map(postAuth -> postAuth.getPost().getId())
                .toList();

        pendingPostIds.forEach(postId -> approvePost(postId));
    }
}
