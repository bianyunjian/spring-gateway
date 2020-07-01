package com.aispeech.ezml.gateway.auth;

import lombok.Data;

@Data
public class PermissionVO {

    /**
     * 权限编号
     */
    private Integer id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限是否可配置：0-不可配置,1-可配置
     */
    private Integer isAssigned;

    /**
     * 权限类型：1-页面,2-接口
     */
    private Integer type;
}
