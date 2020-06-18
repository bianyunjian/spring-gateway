package com.aispeech.ezml.demoservice.auth;

import lombok.Data;

@Data
public class LoginResp {
    String access_token;
    String refresh_token;
}
