package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.config.badword.BadWordFiltering;
import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BadWordFiltering badWordFiltering = new BadWordFiltering(); // 비속어 필터 외부 라이브러리

    /**
     * 회원 가입
     */
    @Transactional
    public void createUser(UserCreateRequest req) {
        // 닉네임 비속어 필터 (욕설 포함 시 ture 반환)
        if(badWordFiltering.check(req.getNickname())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "닉네임에 비속어가 포함되어있습니다.");
        }

        // 유저 아이디 중복 검사
        if (userRepository.findByUserId(req.getUserId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 존재하는 아이디입니다.");
        }
        // 닉네임 중복 검사
        if (userRepository.findByNickname(req.getUserId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 존재하는 닉네임입니다.");
        }
        // 비밀번호 암호화
        String password = passwordEncoder.encode(req.getPassword());
        // 회원 생성
        User user = new User(req.getUserId(), password, req.getNickname());
        userRepository.save(user);
    }


    /**
     * 회원 정보 조회 + 출석 확인
     * 조히여서 ReadOnly = true 하고 싶지만.. 출석 체크 때문에 풀어둠
     */
    @Transactional
    public UserResponseMe getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));

        int checkPoint = 0; // 반환용 포인트

        // 이때 지금 시간이랑 마지막 로그인 시간 비교해서 1일 1회 출석체크 추가해주기 dayOfMonth 사용
        LocalDateTime lastLogin = user.getLastLogin();
        LocalDateTime now = LocalDateTime.now();

        // 첫 로그인인 경우
        if (lastLogin == null) {
            checkPoint = 1000;
            user.lastLoginUpdate();
            user.addPoints(1000);
            user.initAttendanceStreak(); // =1 일
        } else {
            // 첫 로그인이 아닌 경우
            if (lastLogin.getDayOfMonth() != now.getDayOfMonth()) {
                // 날짜가 다르다면, 1000 포인트 지급
                user.addPoints(1000);
                checkPoint = 1000;
                user.lastLoginUpdate();

                // 연속 출석 체크? (년도 같고, 월 같고, 일만 하루 차이!)
                if (lastLogin.getYear() == now.getYear()
                        && lastLogin.getMonth() == now.getMonth() && lastLogin.getDayOfMonth() + 1 == now.getDayOfMonth()) {
                    user.addAttendanceStreak(); // +1 일
                    // 만약에 7배수 라면 추가 포인트 지급 (3000)
                    if (user.getAttendanceStreak() % 7 == 0) {
                        checkPoint += 3000;
                        user.addPoints(3000);
                    }
                } else {
                    // 연속 출석이 아니라면 오늘 부터 1일
                    user.initAttendanceStreak(); // =1 일
                }
            } // End 첫 로그인 아닌 경우
            // 오늘 여러번 로그인한 경우는 그냥 패스
        }

        return new UserResponseMe(user, checkPoint);
    }

    /**
     * 유저 닉네임 변경
     */
    @Transactional
    @CacheEvict(value = "allPlayers", allEntries = true)
    public void updateNickname(Long Id, String newNickname) {

        if(badWordFiltering.check(newNickname)){
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "닉네임에 비속어가 포함되어있습니다.");
        }

        User user = userRepository.findById(Id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));

        Optional<User> byNickname = userRepository.findByNickname(newNickname);

        if (byNickname.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "이미 존재하는 닉네임입니다.");
        }
        user.updateNickname(newNickname);
    }


    /**
     * 관리자용 API
     */
    // 관리자용 전체 회원 조회
    public List<UserResponse> getAll() {
        return userRepository.getUserAll().stream()
                .map(UserResponse::new)
                .toList();
    }


    /** 쓸모없을 것 같음 */
//    //  userId 로 회원 조회
//    public User getUserId(String userId) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));
//        return null;
//    }
//
//    // 닉네임으로 회원 조회
//    public User getNickname(String nickname) {
//        return userRepository.findByNickname(nickname)
//                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));
//    }

    /** 안 쓰는 것 */
    /** 로그인 안씀 왜? 토큰 가지고 me 호출 방법으로 변경 */
//    @Transactional
//    public LoginResponse login(LoginRequest req) {
//        User user = userRepository.getUser(req.getUserId())
//                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "존재하지 않은 아이디입니다."));
//
//        // 비밀번호가 틀렸을 경우
//        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
//            throw new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "비밀번호를 다시 입력해주세요.");
//        }
//
//
//        // 인증서 생성 타입은 Authentication
//        // principal, credntials, authent~~list
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                user.getId(),
//                null,
//                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
//        );
//
//        // 토큰 생성 ( grantType, accessToken, RefreshToken )
//        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
//
//        // last login 갱신
//        LocalDateTime now = LocalDateTime.now();
//        return new LoginResponse(jwtToken, user);
//    }

    /** 토큰 재발급 (리프레시) */
//    public LoginResponse refresh(RefreshReqeust req) {
//
//        String refreshToken = req.getRefreshToken();
//
//        // 리프레시 토큰 검증 (오류발생 -> validateToken 에서 터짐() )
//        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
//            throw new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "만료된 토큰 입니다.");
//        }
//
//        // 사용자 정보 꺼내기 (subject)
//        String userId = jwtTokenProvider.getUserNameFromToken(refreshToken);
//
//        // 리프레시 토큰 (Redis에서 삭제)
//        jwtTokenProvider.deleteRefreshToken(refreshToken);
//
//        // 유저 정보 db에서 조회
//        User user = userRepository.getUser(userId)
//                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "존재하지 않은 아이디입니다."));
//
//        // 새로운 인증서 생성
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                user.getUserId(),
//                null,
//                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
//        );
//
//        // 토큰 재발급
//        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
//
//        return new LoginResponse(jwtToken, user);
//    }

}
