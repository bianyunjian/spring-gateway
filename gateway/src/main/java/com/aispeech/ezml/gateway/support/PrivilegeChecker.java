package com.aispeech.ezml.gateway.support;

import com.aispeech.ezml.gateway.auth.PermissionVO;
import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PrivilegeChecker {

    public static boolean useRedis = true;
    public static final String REDIS_KEY_PREFIX = "PERMISSION_";

    @Resource
    private RedisUtil redisUtil;

    /**
     * check if current user has the specified privilege for the request path
     * Lookup the predefined privilege mapping:
     * --if match the request path ,go check the user privilege
     * --if not found the request path , just pass through
     *
     * @param tokenInfo
     * @param requestPath
     * @return
     */
    public boolean checkUserPrivilegeForRequestPath(UserTokenInfo tokenInfo, String requestPath) {

        String key = REDIS_KEY_PREFIX + requestPath;
        if (redisUtil.hasKey(key)) {
            //有权限限制
            String jsonString = (String) redisUtil.get(key);
            PermissionVO permissionVO = JSON.parseObject(jsonString, PermissionVO.class);
            // 查找是否有指定的权限编号
            int requiredPermissionId = permissionVO.getId();
            if (tokenInfo != null && tokenInfo.getPermissionList() != null && tokenInfo.getPermissionList().size() > 0) {
                for (PermissionVO p :
                        tokenInfo.getPermissionList()) {
                    if (p.getId() == requiredPermissionId) {
                        //匹配到指定的权限编号
                        return true;
                    }
                }
            }

            return false;
        } else {

            //未做权限限制
            return true;
        }
    }

}
