package com.fc.fcseoularchive.season_auth;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.season_auth.dto.SeasonResponse;
import com.fc.fcseoularchive.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForInstant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeasonauthService {

    private final SeasonauthRepository seasonauthRepository;
    private final UserRepository userRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @Transactional
    public void create(Long id, MultipartFile image) throws IOException {

        // 회원이 존재하는 사람인지 확인
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));

        // 업로드 폴더 없으면 폴더 생성하기
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        // 파일 저장하기
        String imagepath = null;

        // 이미지가 null 이 아니고, 비어있지 않다면
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename(); // UUID_파일이름 으로 파일 이름 지정
            File dest = new File(uploadDir + fileName); // 경로+파일이름으로 File 만들기
            image.transferTo(dest); // 실제로 저장하기
            imagepath = "/uploads/" + fileName; // 이미지 경로
        }

        Seasonauth seasonauth = new Seasonauth(user, imagepath);
        seasonauthRepository.save(seasonauth);

    }

    // 전부 get 하기 (관리자만 가능 -> 시큐리티에서 토큰으로 검증하기 때문에)
    public List<SeasonResponse> getAll() {
        List<Seasonauth> seasonList = seasonauthRepository.getSeasonList();

        return seasonList.stream()
                .map((seasonauth) -> new SeasonResponse(seasonauth))
                .toList();
    }

    // 관리자 승인해주기
    @Transactional
    public void approve(Long id){
        Seasonauth seasonauth = seasonauthRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "시즌권 ID 불일치입니다."));
        seasonauth.approve();
    }

    // 관리자 거절해주기
    @Transactional
    public void rejected(Long id){
        Seasonauth seasonauth = seasonauthRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "시즌권 ID 불일치입니다."));
        seasonauth.reject();
    }


}