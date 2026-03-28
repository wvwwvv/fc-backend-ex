package com.fc.fcseoularchive.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CallbackRequest {

    private String code;
    private String codeVerifier;

}
