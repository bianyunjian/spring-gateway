package com.aispeech.ezml.gateway.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfoResp {
    @JsonProperty("userId")
    String userId;
    @JsonProperty("userName")
    String userName;
    @JsonProperty("password")
    String password;
}
