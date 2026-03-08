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

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    // token 적용 전 개발 단계에서는 Bearer: 1 같이 id 만 임시로 담은 토큰 받아서 controller 에서 추출 후 메서드 호출하는 방식으로 구현
    // todo token 적용 후에도 userId 를 추출하고, parameter 에 userId 넣는 방식으로 개발 하나요?
    public void createPost(Long id, PostCreateRequest request) { // userId 사용할 거면 String 타입
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
                .ticketImage(request.getTicketImage())
                .status(status)
                .build();

        postRepository.save(post);

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
    }


}
