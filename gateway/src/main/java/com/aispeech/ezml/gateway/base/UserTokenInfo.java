package com.aispeech.ezml.gateway.base;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTokenInfo {
    String userId;
    String userName;
    boolean isValid;
    LocalDateTime createTime;
    LocalDateTime expireTime;

}
