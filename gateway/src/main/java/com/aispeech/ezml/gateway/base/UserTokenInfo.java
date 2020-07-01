package com.aispeech.ezml.gateway.base;

import com.aispeech.ezml.gateway.auth.PermissionVO;
import com.aispeech.ezml.gateway.auth.RoleVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserTokenInfo {

    String userId;
    String userName;
    String accessToken;
    String refreshToken;
    Date issuedAt;
    Date expiresAt;

    boolean isValid;

    /**
     * 用户的角色
     */
    RoleVO role;

    /**
     * 权限列表
     */
    List<PermissionVO> permissionList;
}
