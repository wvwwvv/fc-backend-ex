package com.fc.fcseoularchive.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /*
        개발 단계에서 token 은 `Bearer: 1` 과 같은 형태로 user 테이블의 id 만 넣는다
        todo token 적용할 때 token 에서 id 만 추출하는 과정 필요 (혹은 userId: String)
    */

    // 직관 기록 작성
    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestBody PostCreateRequest request // 임시로 userId 필드 추가
    ) {

        try {
            //Long id = Long.parseLong(userIdByString);
            postService.createPost(request.getUserId(), request);
        } catch (NumberFormatException e) {
            // id 가 숫자가 아닌 예외 처리
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
