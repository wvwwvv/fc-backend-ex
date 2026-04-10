package com.fc.fcseoularchive.domain.bet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetHistoryRepository extends JpaRepository<BetHistory, Long> {
    List<BetHistory> findAllByUser_Id(String loginId);

    Optional<BetHistory> findByUser_IdAndGame_Id(String loginId, Long gameId);
}
