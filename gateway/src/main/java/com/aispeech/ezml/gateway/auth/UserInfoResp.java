package com.aispeech.ezml.gateway.auth;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoResp {
    /**
     * 用户ID
     */
    String userId;

    /**
     * 用户名称
     */

    String userName;

    /**
     * 登录密码
     */

    String password;

    /**
     * 登录名，默认邮箱
     */

    String loginName;

    /**
     * 用户邮箱
     */

    String email;

    /**
     * 用户所属部门
     */

    String department;

    /**
     * 用户职位
     */

    String position;

    /**
     * 用户状态，0-正常,1-禁用
     */

    Integer status;

    /**
     * 最近一次登录时间
     */

    LocalDateTime lastLoginTime;

    /**
     * 用户的角色
     */
    RoleVO role;

    /**
     * 权限列表
     */
    List<PermissionVO> permissionList;
}
