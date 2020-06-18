package com.aispeech.ezml.demoservice.auth;

import lombok.Data;

@Data
public class LoginReq {
    String userName;
    String password;
}
