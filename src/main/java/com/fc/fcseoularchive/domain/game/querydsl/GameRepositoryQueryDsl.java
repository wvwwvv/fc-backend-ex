package com.fc.fcseoularchive.domain.game.querydsl;

import com.fc.fcseoularchive.domain.game.Game;

import java.util.List;

public interface GameRepositoryQueryDsl {

    List<Game> getAll(Integer year, Integer month);

}
