package com.fc.fcseoularchive.security.web;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {

    private String accessToken;

    /**
     * refreshToken 은 아직 사용 x
     * 확장성만 고려
     */
    private String refreshToken;

}
