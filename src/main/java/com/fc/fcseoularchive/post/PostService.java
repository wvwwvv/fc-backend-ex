package com.fc.fcseoularchive.post;

import com.fc.fcseoularchive.domain.entity.*;
import com.fc.fcseoularchive.image.ImageRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    // PostCreateRequest 에
    @Transactional
    public void createPost(Long id, PostCreateRequest request) { // Long 타입의 id 사용 주의
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

        if (postRepository.existsByUserIdAndGameId(id, game.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 직관 인증을 작성한 경기입니다.");
        }

        // Post 저장
        Post post = Post.builder()
                .user(user)
                .game(game)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);

        // Image 저장
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
    }

   /* @Transactional(readOnly = true)
    public List<PostResponse> getPosts() {
        return postAuthRepository.f
    }*/


    // admin : 모든 인증 게시글 조회
    @Transactional(readOnly = true)
    public List<PostAdminResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostAdminResponse::from)
                .collect(Collectors.toList());
    }


}
