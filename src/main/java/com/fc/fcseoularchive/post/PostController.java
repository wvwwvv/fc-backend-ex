package com.fc.fcseoularchive.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. PostController", description = "직관 기록 API")
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
    @Operation(summary = "직관 기록 작성")
    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestBody PostCreateRequest request
    ) {

        try {
            // userId null 체크
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest().build();
            }

            postService.createPost(request.getUserId(), request);
        } catch (NumberFormatException e) {
            // id 가 숫자가 아닌 예외 처리
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
