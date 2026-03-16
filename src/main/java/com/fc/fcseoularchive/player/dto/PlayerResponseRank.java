package com.fc.fcseoularchive.player.dto;


import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.fc.fcseoularchive.domain.enums.PlayerStatus;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class PlayerResponseRank {

    private final Integer id;

    private final String name;

    private final Integer backNumber;

    private final PlayerPosition position;

    private final PlayerStatus status;

    private final String image;

    private final HashMap<Integer,String> userRank;

    public PlayerResponseRank(Player player, HashMap<Integer,String> userRank) {
        this.id = player.getId();
        this.name = player.getName();
        this.backNumber = player.getBackNumber();
        this.position = player.getPosition();
        this.status = player.getStatus();
        this.image = player.getImage();
        this.userRank = userRank;
    }

}
