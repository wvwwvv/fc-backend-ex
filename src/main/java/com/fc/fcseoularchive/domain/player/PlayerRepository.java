package com.fc.fcseoularchive.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {

    List<Player> findByStatus(PlayerStatus status);

    List<Player> findByStatusAndPosition(PlayerStatus status, PlayerPosition position);

}
