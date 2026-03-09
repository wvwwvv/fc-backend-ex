package com.fc.fcseoularchive.season_auth.querydsl;


import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.domain.enums.SeasonStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fc.fcseoularchive.domain.entity.QSeasonauth.*;
import static com.fc.fcseoularchive.domain.entity.QUser.*;

@RequiredArgsConstructor
public class SeasonauthRepositoryImpl implements SeasonauthRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Seasonauth> getSeasonList() {
        return jpaQueryFactory
                .select(seasonauth)
                .from(seasonauth)
                .join(user, user).fetchJoin()
                .where(seasonauth.seasonStatus.eq(SeasonStatus.valueOf("PENDING")))
                .fetch();
    }

}
