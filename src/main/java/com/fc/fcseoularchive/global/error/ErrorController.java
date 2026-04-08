package com.fc.fcseoularchive.global.error;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "99. ErrorController", description = "예외 출력 참고용도")
@RestController
@RequestMapping("/api/error")
public class ErrorController {

    @GetMapping("/not-found")
    public void notFound() {
        throw new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "유저를 찾을 수 없습니다");
    }

    @GetMapping("/bad-request")
    public void badRequest() {
        throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "잘못된 요청입니다.");
    }

}
