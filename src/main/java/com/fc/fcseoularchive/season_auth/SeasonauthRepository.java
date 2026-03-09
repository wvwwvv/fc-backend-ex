package com.fc.fcseoularchive.season_auth;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.season_auth.querydsl.SeasonauthRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;



// No property 'getAllSeasons' found for type 'Seasonauth 라는 에러는 뭐임?
@Repository
public interface SeasonauthRepository extends JpaRepository<Seasonauth,Long> , SeasonauthRepositoryQuerydsl, QuerydslPredicateExecutor<Seasonauth> {



}
