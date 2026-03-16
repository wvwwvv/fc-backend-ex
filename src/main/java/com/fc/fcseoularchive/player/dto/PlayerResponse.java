package com.fc.fcseoularchive.player.dto;

import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import lombok.Getter;

@Getter
public class PlayerResponse {

    private final Integer id;

    private final String name;

    private final Integer backNumber;

    private final PlayerPosition position;

    private final String status;

    private final String image;



    public PlayerResponse(Player player) {
        this.id = player.getId();
        this.name = player.getName();
        this.backNumber = player.getBackNumber();
        this.position = player.getPosition();
        this.status = player.getStatus().toString();
        this.image = player.getImage(); // 상대 경로 그대로 줌 (프론트에서 앞에 서버 주소 붙여줘야해!)
    }
}
