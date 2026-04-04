package com.fc.fcseoularchive.domain.game;

import com.fc.fcseoularchive.domain.game.querydsl.GameRepositoryQueryDsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryQueryDsl {

    // 오름차순 날짜로 모든 경기 조회
    List<Game> findAllByOrderByDateAsc();

    // 오름차순 날짜로 모든 경기 조회 - 특정 년/월
    // date 중 연도/월만 추출 후 사용 : JPA 메서드 네이밍 만으로 불가
    @Query("SELECT g FROM Game g WHERE YEAR(g.date) = :year AND MONTH(g.date) = :month ORDER BY g.date ASC")
    List<Game> findByYearOrderByDateAsc(@Param("year") int year, @Param("month") int month);

}
