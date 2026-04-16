package com.fc.fcseoularchive.domain.user;


import com.fc.fcseoularchive.domain.user.querydsl.UserRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String>, UserRepositoryQuerydsl {

    // 회원 id 로 찾기
    Optional<User> findById(String id);

    // 닉네임으로 id 찾기
    Optional<User> findByNickname(String nickname);




}
