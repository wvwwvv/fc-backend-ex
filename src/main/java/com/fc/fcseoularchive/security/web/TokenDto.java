package com.fc.fcseoularchive.security.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenDto {
    
    private String grantType;
    
    private String accessToken;
    
    private String refreshToken;
    
    private Long accessTokenExpiresIn;
    
}
