package com.aispeech.ezml.gateway.support;

import com.aispeech.ezml.gateway.base.UserTokenInfo;

import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {

    //TODO  use Redis to store token
    private static ConcurrentHashMap<String, UserTokenInfo> userTokenInfoHashMap = new ConcurrentHashMap<>();

    public static UserTokenInfo getUserTokenInfo(String userId) {
        if (userTokenInfoHashMap.containsKey(userId)) {
            return userTokenInfoHashMap.get(userId);
        }
        return null;
    }

    public static void updateTokenCache(UserTokenInfo userTokenInfo) {

        if (userTokenInfoHashMap.containsKey(userTokenInfo.getUserId())) {
            userTokenInfoHashMap.replace(userTokenInfo.getUserId(), userTokenInfo);
        } else {
            userTokenInfoHashMap.put(userTokenInfo.getUserId(), userTokenInfo);
        }
    }

    public static void removeTokenCache(String userId) {
        userTokenInfoHashMap.remove(userId);
    }

    public static boolean checkAccessToken(String userId, String accessToken) {
        if (userTokenInfoHashMap.containsKey(userId)) {
            return userTokenInfoHashMap.get(userId).getAccessToken().equals(accessToken);
        }
        return false;
    }

    public static boolean checkRefreshToken(String userId, String refreshToken) {
        if (userTokenInfoHashMap.containsKey(userId)) {
            return userTokenInfoHashMap.get(userId).getRefreshToken().equals(refreshToken);
        }
        return false;
    }


}
