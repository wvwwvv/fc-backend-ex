package com.fc.fcseoularchive.user.querydsl;


import com.fc.fcseoularchive.domain.entity.QSeasonauth;
import com.fc.fcseoularchive.domain.entity.QUser;
import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.user.dto.UserResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fc.fcseoularchive.domain.entity.QSeasonauth.seasonauth;
import static com.fc.fcseoularchive.domain.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Seasonauth> getUserAll() {
        return jpaQueryFactory
                .select(seasonauth)
                .from(seasonauth)
                .join(user, user)
                .fetch();

    }
}
