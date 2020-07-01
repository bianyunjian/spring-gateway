package com.aispeech.ezml.demoservice.support;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class RequestUtil {
    public static String getRequestUserId() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = req.getHeader("userId");
        return userId;
    }

    public static String getRequestUserName() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userNameBase64Str = req.getHeader("userNameBase64");
        //header中有中文必须编码
        byte[] bytes = Base64.getDecoder().decode(userNameBase64Str);
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return userNameBase64Str;
    }
}
