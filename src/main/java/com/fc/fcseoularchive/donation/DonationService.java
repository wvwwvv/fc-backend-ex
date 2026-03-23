package com.fc.fcseoularchive.donation;

import com.fc.fcseoularchive.domain.entity.Donation;
import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.player.PlayerRepository;
import com.fc.fcseoularchive.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    @CacheEvict(value = "allPlayers", allEntries = true)
    public void create(Long userId, Long playerId, Integer point){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "존재하지 않은 아이디입니다."));


        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 선수입니다."));

        // User , 선수 전부 확인 후
        if(user.getPoints() < point){
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "잔액이 부족합니다." );
        }

        // 후원 시 음수로 받아오는것 에러 처리
        if(point <= 0 ){
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "1원 이상부터 가능합니다." );
        }

        user.subtractPoints(point); // 포인트 차감 완료

        // donation 에 user, player fetch join
        // 여기선 user 의 point 도 사용해야 하기 때문에 fetch join 필요
        // point 도 donation 에 반정규화 하는것은 과하다
        Optional<Donation> optDonation = donationRepository.findByUserIdAndPlayerId(userId, playerId);

        // 기존에 없다면 새롭게 생성 후 저장
        if(optDonation.isEmpty()){
            Donation newDonation = new Donation(point, user, player, user.getNickname());
            donationRepository.save(newDonation);
            return;
        }

        // 기존에 있다면 포인트만 더 하고 끝 (더티체킹으로 따로 저장 x)
        optDonation.get().addPoint(point);
    }


}
