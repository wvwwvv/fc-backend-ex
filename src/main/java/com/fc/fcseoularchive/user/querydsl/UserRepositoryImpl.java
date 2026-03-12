package com.fc.fcseoularchive.user.querydsl;


import com.fc.fcseoularchive.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.fc.fcseoularchive.domain.entity.QUser.user;


/**
 * QueryDSL을 사용한 User Repository 구현체
 */

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> getUserAll() {
        return jpaQueryFactory
                .select(user)
                .from(user)
                .fetch();
    }


    @Override
    public Optional<User> getUser(String userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(user)
                        .from(user)
                        .where(user.userId.eq(userId))
                        .fetchOne()
        );
    }
}