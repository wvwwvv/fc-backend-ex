package com.fc.fcseoularchive.domain.player.dto;


import com.fc.fcseoularchive.domain.player.Player;
import com.fc.fcseoularchive.domain.player.PlayerPosition;
import com.fc.fcseoularchive.domain.player.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseRank {

    private Integer id;

    private String name;

    private Integer backNumber;

    private PlayerPosition position;

    private PlayerStatus status;

    private String image;

    private HashMap<Integer,String> userRank;

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
