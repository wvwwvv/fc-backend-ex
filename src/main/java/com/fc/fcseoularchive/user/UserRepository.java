package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // 회원 id 로 찾기
    Optional<User> findByUserId(String id);

    // 닉네임으로 id 찾기
    Optional<User> findByNickname(String nickname);


}
