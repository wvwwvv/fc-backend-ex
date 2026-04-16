package com.fc.fcseoularchive.domain.post.querydsl;

import com.fc.fcseoularchive.domain.game.QGame;
import com.fc.fcseoularchive.domain.post.Post;
import com.fc.fcseoularchive.domain.post.QPost;
import com.fc.fcseoularchive.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fc.fcseoularchive.domain.game.QGame.*;
import static com.fc.fcseoularchive.domain.post.QPost.*;
import static com.fc.fcseoularchive.domain.user.QUser.*;


@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryQueryDsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByUserIdAndGameId(String userId, Long gameId) {
        return jpaQueryFactory
                .selectOne()
                .from(post)
                .join(post.user, user) // exist 는 fetch join 이 필요없다
                .join(post.game, game)
                .where(
                        post.user.id.eq(userId),
                        post.game.id.eq(gameId)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<Post> findByUser_Id(String userId) {
        return jpaQueryFactory
                .selectFrom(post)
                .distinct()
                .join(post.user, user).fetchJoin()
                .join(post.game, game).fetchJoin()
                .where(post.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<Post> getPostAll(String loginId) {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .leftJoin(post.user, user).fetchJoin()
                .leftJoin(post.game, game).fetchJoin()
                .where(post.user.id.eq(loginId))
                .fetch();
    }
}
