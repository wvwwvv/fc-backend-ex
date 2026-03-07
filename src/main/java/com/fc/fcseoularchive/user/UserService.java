package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.Role;
import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "잘못된 요청입니다.");

    // 유저 생성
    public void createUser(UserCreateRequest req) {

        // 유저 아이디 중복 검사
        if (userRepository.findByUserId(req.getUserId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 존재하는 아이디입니다.");
        }

        // 닉네임 중복 검사
        if (userRepository.findByNickname(req.getUserId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "409", "CONFLICT", "이미 존재하는 닉네임입니다.");
        }

        String password = passwordEncoder.encode(req.getPassword()); // 비밀번호 암호화

        // 회원 생성
        User user = new User(req.getUserId(), password, req.getNickname());
        userRepository.save(user);
    }


}
