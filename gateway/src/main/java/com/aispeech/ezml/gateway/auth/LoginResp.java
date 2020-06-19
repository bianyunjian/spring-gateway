package com.aispeech.ezml.gateway.auth;

import lombok.Data;

@Data
public class LoginResp {
    String userId;
    String userName;
    String displayName;
    String accessToken;
    String refreshToken;

}
