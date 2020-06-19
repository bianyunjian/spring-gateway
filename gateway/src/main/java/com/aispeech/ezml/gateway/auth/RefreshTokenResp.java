package com.aispeech.ezml.gateway.auth;

import lombok.Data;

@Data
public class RefreshTokenResp {

    String accessToken;
    String refreshToken;
}
