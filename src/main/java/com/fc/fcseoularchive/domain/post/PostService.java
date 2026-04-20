package com.fc.fcseoularchive.domain.post;

import com.fc.fcseoularchive.domain.game.GameResult;
import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.image.Image;
import com.fc.fcseoularchive.domain.image.ImageCompressionService;
import com.fc.fcseoularchive.domain.post.dto.*;
import com.fc.fcseoularchive.domain.image.ImageRepository;
import com.fc.fcseoularchive.global.error.ApiException;
import com.fc.fcseoularchive.domain.game.GameRepository;
import com.fc.fcseoularchive.domain.user.User;
import com.fc.fcseoularchive.domain.user.UserRepository;
import com.fc.fcseoularchive.global.util.FileNameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ImageCompressionService imageCompressionService;

    private final String uploadDir = System.getProperty("user.dir") + "/upload/post";

    // PostCreateRequest 에
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "attendanceRank", allEntries = true),
            @CacheEvict(value = "winRateRank", allEntries = true)
    })
    public void createPost(String loginId, PostCreateRequest request) throws IOException { // Long 타입의 id 사용 주의
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

        if (postRepository.existsByUserIdAndGameId(loginId, game.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 직관 인증을 작성한 경기입니다.");
        }
        // 작성자 == 로그인 유저 확인
        if (!Objects.equals(request.getUserId(), loginId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "작성하려는 게시글의 유저 아이디와 현재 로그인된 유저의 아이디가 다릅니다.");
        }

        // Post 저장
        Post post = Post.builder()
                .user(user)
                .game(game)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Image 저장
        // 이미지들은 따로 Image 테이블에 저장
        // 프론트의 request 에 image 필드가 있고, 비어있지 않을 때
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<Image> images = new ArrayList<>();

            for (MultipartFile file : request.getImages()) {
                if (file == null || file.isEmpty()) continue;

                String rawFileName = file.getOriginalFilename();
                if (rawFileName == null || rawFileName.isBlank()) continue;

                // ✅ 확장자 떼고 .webp 붙이기
                String nameWithoutExt = FileNameUtils.stripExtension(rawFileName);
                String fileName = UUID.randomUUID() + "_" + nameWithoutExt + ".webp";

                File destination = new File(uploadDir, fileName);
                byte[] compressed = imageCompressionService.compress(file);
                try (FileOutputStream fos = new FileOutputStream(destination)) {
                    fos.write(compressed);
                }

                String imagePath = "/upload/post/" + fileName;

                images.add(Image.builder()
                        .user(user)
                        .game(game)
                        .image(imagePath)
                        .build()
                );
            }

            if (!images.isEmpty())
                imageRepository.saveAll(images);

            // 게시물 작성하면 500 포인트
        }
        user.addPoints(500);
    }

    // user : 본인의 게시물 전부 조회
    @Transactional(readOnly = true)
    public PostGetAllResponse getPosts(String loginId) { // 로그인 유저의 PK

        // 내가 직관한 경기 post만 전부 가져오기
        // user, game fetch join
        List<Post> postAll = postRepository.findByUser_Id(loginId);

        // 기존꺼 그대로 사용
        List<PostResponse> list = postAll
                .stream()
                .map(post -> {
                    PostResponse response = PostResponse.from(post);
                    imageRepository.findFirstByGame_IdAndUser_IdOrderByIdAsc(post.getGame().getId(), loginId) // post 는 fetch join 이므로 getGame(), getUser() 로 필드 접근 해도 N+1 문제 발생 안함
                            .ifPresent(image -> response.setThumbnail(image.getImage()));
                    return response;
                })
                .toList();

        // 승/무/패 구하기
        int win = 0;
        int lose = 0;
        int draw = 0;
        int count = 0;

        // 승 무 패 필터걸기
        for (Post post : postAll) {
            // 프론트에서 경기 결과가 null 이면 직관 작성 못하게 2중으로 처리 필요
            if (post.getGame().getResult() == null) continue;

            if (post.getGame().getResult().equals(GameResult.W)) win++;
            else if (post.getGame().getResult().equals(GameResult.L)) lose++;
            else draw++;
        }
        count = win + lose + draw;

        return new PostGetAllResponse(list, win, lose, draw, count);

    }

    // user : 본인의 게시물 1개 상세 조회
    @Transactional(readOnly = true)
    public PostResponseDetail getPostDetail(Long postId, String loginId) {
        Post post = postRepository.findByIdAndUserIdWithGame(postId, loginId) // fetch join
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getUser().getId(), loginId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "조회하려는 게시글의 유저 아이디와 현재 로그인된 유저의 아이디가 다릅니다.");
        }

        // post 는 fetch join 이므로 getGame(), getUser() 로 필드 접근 해도 N+1 문제 발생 안함
        List<String> images = imageRepository.findByGame_IdAndUser_Id(post.getGame().getId(), loginId)
                .stream()
                .map(Image::getImage)
                .toList();

        PostResponseDetail response = PostResponseDetail.from(post);
        response.setImages(images);

        return response;
    }

    // 수정
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "attendanceRank", allEntries = true),
            @CacheEvict(value = "winRateRank", allEntries = true)
    })
    public void updatePost(Long postId, String loginId, PostUpdateRequest request) throws IOException {
        Post post = postRepository.findByIdAndUserIdWithGame(postId, loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getUser().getId(), loginId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "수정하려는 게시글의 유저 아이디와 현재 로그인된 유저의 아이디가 다릅니다.");
        }

        // 제목, 내용 수정
        post.update(request.getTitle(), request.getContent());

        // 프론트에서 유지하겠다고 보낸 기존 이미지 목록
        List<String> keepImages = request.getExistingImages() != null
                ? request.getExistingImages()
                : new ArrayList<>();

        // 현재 저장된 기존 이미지 목록
        List<Image> oldImages = imageRepository.findByGame_IdAndUser_Id(post.getGame().getId(), loginId);

        // 유지 목록에 없는 이미지만 삭제
        for (Image image : oldImages) {
            String imagePath = image.getImage();
            if (imagePath == null || imagePath.isBlank()) continue;

            if (!keepImages.contains(imagePath)) {
                String relativePath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
                File file = new File(System.getProperty("user.dir"), relativePath);
                if (file.exists()) {
                    file.delete();
                }

                imageRepository.delete(image);
            }
        }

        // 새 이미지 저장
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            List<Image> newImages = new ArrayList<>();
            for (MultipartFile file : request.getImages()) {
                if (file == null || file.isEmpty()) continue;

                String rawFileName = file.getOriginalFilename();
                if (rawFileName == null || rawFileName.isBlank()) continue;

//                String fileName = UUID.randomUUID() + "_" + rawFileName;
//                file.transferTo(destination);

                String fileName = FileNameUtils.generateWebpFileName(rawFileName);
                File destination = new File(uploadDir, fileName);
                byte[] compressed = imageCompressionService.compress(file);
                try (FileOutputStream fos = new FileOutputStream(destination)) {
                    fos.write(compressed);
                }

                String imagePath = "/upload/post/" + fileName;

                newImages.add(Image.builder()
                        .user(post.getUser())
                        .game(post.getGame())
                        .image(imagePath)
                        .build());
            }

            if (!newImages.isEmpty()) {
                imageRepository.saveAll(newImages);
            }
        }
    }

    // 삭제
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "attendanceRank", allEntries = true),
            @CacheEvict(value = "winRateRank", allEntries = true)
    })
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findByIdAndUserIdWithGame(postId, loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getUser().getId(), loginId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "삭제하려는 게시글의 유저 아이디와 현재 로그인된 유저의 아이디가 다릅니다.");
        }

        // 삭제, 포인트 반환 로직 - 포인트 악용 방지
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        if (user.getPoints() < 500) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "반환 포인트가 부족해 게시글을 삭제할 수 없습니다.");
        }

        List<Image> images = imageRepository.findByGame_IdAndUser_Id(post.getGame().getId(), loginId); // 삭제하려는 이미지들

        for (Image image : images) {
            String imagePath = image.getImage(); // 예: /upload/post/uuid_name.jpg
            if (imagePath == null || imagePath.isBlank()) continue;

            // db의 /upload/...  경로 upload/... 로 변환
            //File 에서 절대경로 사용하면 앞의 user.fir 무시할 수 있어서 상대경로로 바꾸는 작업 필요
            String relativePath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
            File file = new File(System.getProperty("user.dir"), relativePath);

            if (file.exists()) {
                file.delete();
            }
        }

        imageRepository.deleteByGame_IdAndUser_Id(post.getGame().getId(), loginId);
        postRepository.delete(post);
        user.subtractPoints(500);
    }


    // admin : 모든 인증 게시글 조회
    @Transactional(readOnly = true)
    public List<PostAdminResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostAdminResponse::from)
                .collect(Collectors.toList());
    }


}
