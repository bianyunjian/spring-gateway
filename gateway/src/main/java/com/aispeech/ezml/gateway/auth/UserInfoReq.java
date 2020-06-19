package com.aispeech.ezml.gateway.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfoReq {
    @JsonProperty("userName")
    String userName;
}
