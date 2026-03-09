package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.user.querydsl.UserRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryQuerydsl, QuerydslPredicateExecutor<Seasonauth> {

    // 회원 id 로 찾기
    Optional<User> findByUserId(String id);

    // 닉네임으로 id 찾기
    Optional<User> findByNickname(String nickname);




}
