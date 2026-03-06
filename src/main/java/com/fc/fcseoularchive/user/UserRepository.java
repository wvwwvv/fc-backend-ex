package com.fc.fcseoularchive.user;


import com.fc.fcseoularchive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    // 회원 id 로 찾기
    User findByUserId(String id);


}
