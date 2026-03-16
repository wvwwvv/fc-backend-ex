package com.fc.fcseoularchive.player;

import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.fc.fcseoularchive.domain.enums.PlayerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {

    List<Player> findByStatus(PlayerStatus status);

    List<Player> findByStatusAndPosition(PlayerStatus status, PlayerPosition position);

}
