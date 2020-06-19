package com.aispeech.ezml.gateway.auth;

import lombok.Data;

@Data
public class LoginReq {
    String userName;
    String password;
}
