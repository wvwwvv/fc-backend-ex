package com.fc.fcseoularchive.domain.post;

import com.fc.fcseoularchive.domain.post.querydsl.PostRepositoryQueryDsl;
import com.fc.fcseoularchive.domain.rank.AttendanceRankRow;
import com.fc.fcseoularchive.domain.rank.WinRateRankRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryQueryDsl {

    // 게시물 id 와 유저 id로 post 찾기
    @Query("""
        SELECT p
        FROM Post p
        JOIN FETCH p.game g
        JOIN FETCH p.user u
        WHERE p.id = :postId AND u.id = :loginId
""")
    Optional<Post> findByIdAndUserIdWithGame(@Param("postId") Long postId, @Param("loginId") String loginId);


    // 직관왕 랭킹에 사용
    // Post 에서 join 하므로, 게시글 0개인 유저는 처음부터 집계 안됨
    // 동점 처리는 user 의 id 로 오름차순
    @Query("""
        SELECT u.nickname as nickname, COUNT(p.id) as count
        FROM Post p
        JOIN p.user u
        jOIN p.game g
        WHERE FUNCTION('YEAR', g.date) = :year
        GROUP BY u.id, u.nickname
        ORDER BY COUNT(p.id) DESC, u.id ASC
""")
    // JPQL 에는 LIMIT 이 없다. LIMIT 을 사용하려면 nativeQuery 를 사용해야 한다.
    // Pageable 을 파라미터로 넣으면 DB 에서 n 개만 가져온다
    List<AttendanceRankRow> findAttendanceTopKByYear(@Param("year") int year, Pageable pageable);


    // 승률왕 랭킹에 사용
    // SELECT 하는 별칭과 WinRateRankRow interface get 메서드명 일치(자동 Projection)
    // game 의 result 가 null 이 아닌 것만 집계
    // (이긴 경기 / 전체 경기) 우선 내림차순, 그 다음으로 직관 경기 수가 많은 사람에게 내림차순
    @Query("""
        SELECT u.nickname as nickname,
                  SUM(CASE WHEN g.result = 'W' THEN 1 ELSE 0 END) as winCount,
                  COUNT(p.id) as totalCount
        FROM Post p
        JOIN p.user u
        JOIN p.game g
        WHERE FUNCTION('YEAR', g.date) = :year AND g.result IS NOT NULL
        GROUP BY u.id, u.nickname
        ORDER BY (SUM(CASE WHEN g.result = 'W' THEN 1.0 ELSE 0.0 END) / COUNT(p.id)) DESC,
                       COUNT(p.id) DESC,
                       u.id ASC
""")
    List<WinRateRankRow> findWinRateTopKByYear(@Param("year") int year, Pageable pageable);
}
