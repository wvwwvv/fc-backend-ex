package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.config.jwt.JwtToken;
import com.fc.fcseoularchive.config.jwt.JwtTokenProvider;
import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.user.dto.LoginRequest;
import com.fc.fcseoularchive.user.dto.LoginResponse;
import com.fc.fcseoularchive.user.dto.RefreshReqeust;
import com.fc.fcseoularchive.user.dto.UserCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원 가입
    public void createUser(UserCreateRequest req) {
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


    // Id 로 회원 조회
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));
    }

    //  userId 로 회원 조회
    public User getUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));
    }

    // 닉네임으로 회원 조회
    public User getNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));
    }

    // 로그인
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "존재하지 않은 아이디입니다."));

        // 비밀번호가 틀렸을 경우
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "비밀번호를 다시 입력해주세요.");
        }

        // 인증서 생성 타입은 Authentication
        // principal, credntials, authent~~list
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );

        // 토큰 생성 ( grantType, accessToken, RefreshToken )
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        return new LoginResponse(jwtToken, user);
    }

    // 토큰 재발급 (리프레시)
    public LoginResponse refresh(RefreshReqeust req) {

        String refreshToken = req.getRefreshToken();

        // 리프레시 토큰 검증 (오류발생 -> validateToken 에서 터짐() )
        if(!jwtTokenProvider.validateRefreshToken(refreshToken)){
            throw new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "만료된 토큰 입니다.");
        }

        // 사용자 정보 꺼내기 (subject)
        String userId = jwtTokenProvider.getUserNameFromToken(refreshToken);

        // 리프레시 토큰 (Redis에서 삭제)
        jwtTokenProvider.deleteRefreshToken(refreshToken);

        // 유저 정보 db에서 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));

        // 새로운 인증서 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );

        // 토큰 재발급
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(jwtToken, user);
    }


    /**
     * 관리자용 AP
     */
    // 관리자용 전체 회원 조회
    public List<User> getAll() {
        return userRepository.findAll();
    }


}
