package com.fc.fcseoularchive.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String userId;

    private String password;

    private String nickname;

}
