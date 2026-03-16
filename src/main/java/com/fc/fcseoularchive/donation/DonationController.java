package com.fc.fcseoularchive.donation;

import com.fc.fcseoularchive.config.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "5. DonationController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/donation")
public class DonationController {

    private final DonationService donationService;
    private final CurrentUserProvider currentUserProvider;


    // User 는 Authentication 에서 꺼내서 쓰고 Player 는 PathVariable 로 받기
    // 기존에 있다면 += point 없다면 새롭게 생성하기. (그럼 기존에 있는거 어떻게 조회? 는 user.id , player.id 로 비교)
    // User -> Player 후원 기능 (필수)
    @Operation(summary = "선수에게 후원")
    @PostMapping("/{player_id}")
    public ResponseEntity<Void> createDonation(Authentication authentication, @PathVariable("player_id") Long playerId, Integer point) {
        Long userId = currentUserProvider.getCurrentUserId(authentication);
        donationService.create(userId, playerId, point);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    // 전체 랭킹 가져오기 TOP 3 (필수)




    // 해당 선수만에 대한 랭킹?



    // 내가 누구한테 후원했는지?



}
