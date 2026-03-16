package com.fc.fcseoularchive.player.dto;


import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.fc.fcseoularchive.domain.enums.PlayerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NotNull
public class CreatePlayerRequest {

    @NotNull
    private String name;

    @NotNull
    private int backNumber;

    @NotNull
    private PlayerPosition position;

    @NotNull
    private MultipartFile image;

    @NotNull
    private PlayerStatus status;

}
