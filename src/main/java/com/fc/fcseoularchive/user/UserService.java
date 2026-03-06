package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;












    /**
     * 개발 임시 테스트용
     */
    // 유저 생성 로직
    public void createUser(UserCreateRequest req) {
        User user = new User(req.getUserId(), req.getPassword(), req.getNickname());
        userRepository.save(user);
    }

    // 유저 id 로 조회
    public User getUser(String id) {
        User byUserId = userRepository.findByUserId(id);


        return byUserId;
    }


}
