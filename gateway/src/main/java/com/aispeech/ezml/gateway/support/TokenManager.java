package com.aispeech.ezml.gateway.support;

import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenManager {

    public static boolean useRedis = true;
    public static final String REDIS_KEY_PREFIX = "TOKEN_";
    private static ConcurrentHashMap<String, UserTokenInfo> userTokenInfoHashMap = new ConcurrentHashMap<>();

    @Resource
    private RedisUtil redisUtil;

    public UserTokenInfo getUserTokenInfo(String userId) {
        if (useRedis) {
            String key = REDIS_KEY_PREFIX + userId;
            if (redisUtil.hasKey(key)) {
                return JSON.parseObject((String) redisUtil.get(key), UserTokenInfo.class);
            }

        } else {
            if (userTokenInfoHashMap.containsKey(userId)) {
                return userTokenInfoHashMap.get(userId);
            }
        }


        return null;
    }

    public void updateTokenCache(UserTokenInfo userTokenInfo) {
        if (useRedis) {

            String key = REDIS_KEY_PREFIX + userTokenInfo.getUserId();
            redisUtil.set(key, JSON.toJSONString(userTokenInfo));

        } else {
            if (userTokenInfoHashMap.containsKey(userTokenInfo.getUserId())) {
                userTokenInfoHashMap.replace(userTokenInfo.getUserId(), userTokenInfo);
            } else {
                userTokenInfoHashMap.put(userTokenInfo.getUserId(), userTokenInfo);
            }
        }

    }

    public void removeTokenCache(String userId) {
        if (useRedis) {
            String key = REDIS_KEY_PREFIX + userId;
            redisUtil.del(key);
        } else {
            userTokenInfoHashMap.remove(userId);
        }
    }

    public boolean checkAccessToken(String userId, String accessToken) {
        if (useRedis) {
            String key = REDIS_KEY_PREFIX + userId;
            if (redisUtil.hasKey(key)) {
                UserTokenInfo info = JSON.parseObject((String) redisUtil.get(key), UserTokenInfo.class);
                if (info != null) {
                    return info.getAccessToken().equals(accessToken);
                }
            }
        } else {
            if (userTokenInfoHashMap.containsKey(userId)) {
                return userTokenInfoHashMap.get(userId).getAccessToken().equals(accessToken);
            }
        }
        return false;
    }

    public boolean checkRefreshToken(String userId, String refreshToken) {
        if (useRedis) {
            String key = REDIS_KEY_PREFIX + userId;
            if (redisUtil.hasKey(key)) {
                UserTokenInfo info = JSON.parseObject((String) redisUtil.get(key), UserTokenInfo.class);
                if (info != null) {
                    return info.getRefreshToken().equals(refreshToken);
                }
            }
        } else {
            if (userTokenInfoHashMap.containsKey(userId)) {
                return userTokenInfoHashMap.get(userId).getRefreshToken().equals(refreshToken);
            }
        }

        return false;
    }


}
