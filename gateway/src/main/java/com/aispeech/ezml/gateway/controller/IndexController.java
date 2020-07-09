package com.aispeech.ezml.gateway.controller;


import com.aispeech.ezml.gateway.auth.*;
import com.aispeech.ezml.gateway.base.BaseResponse;
import com.aispeech.ezml.gateway.base.UserTokenInfo;
import com.aispeech.ezml.gateway.service.UserService;
import com.aispeech.ezml.gateway.support.TokenManager;
import com.aispeech.ezml.gateway.support.TokenUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.Calendar;
import java.util.Date;

@RestController
public class IndexController {
    @Autowired
    private TokenManager TokenManager;
    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String hello() {
        return "hello " + Clock.systemDefaultZone().instant().toString();
    }

    @GetMapping("/validate")
    public String validate() {
        return "validate passed at " + Clock.systemDefaultZone().instant().toString();
    }


    @PostMapping(value = "/login")
    public BaseResponse<LoginResp> login(@RequestBody @Validated LoginReq param) {

        BaseResponse<LoginResp> obj = new BaseResponse<LoginResp>();
        LoginResp resp = new LoginResp();

        UserInfoReq userServiceReq = new UserInfoReq();
        userServiceReq.setUserName(param.getUserName());
        BaseResponse<UserInfoResp> userServiceResp = userService.getUserInfo(userServiceReq);
        if (userServiceResp != null && userServiceResp.isSuccess() && userServiceResp.getData() != null) {
            UserInfoResp userData = userServiceResp.getData();
            String passwordBase64 = userData.getPassword();
            String userName = userData.getUserName();
            String userId = userData.getUserId();

            if (userData.getStatus() != 0) {
                obj.fail("user has been disabled, cannot login");
                return obj;
            }

            if (TokenUtil.getInstance().verifyPassword(param.getPassword(), passwordBase64) == false) {
                obj.fail("username or password incorrect");
                return obj;
            }
            resp.setUserId(userId);
            resp.setUserName(userName);
            resp.setLoginName(param.getUserName());

            Date issueAt = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, TokenUtil.getInstance().TOKEN_EXPIRE_MINUTES);
            Date expireAt = calendar.getTime();
            resp.setIssuedAt(issueAt);
            resp.setExpiresAt(expireAt);

            String roleJsonString = JSON.toJSONString(userData.getRole());
//            String permissionJsonString = JSON.toJSONString(userData.getPermissionList().stream().map(t -> t.getId()).distinct().collect(Collectors.toList()));
            String permissionJsonString = JSON.toJSONString(userData.getPermissionList());
            String[] audienceArray = {
                    userId,
                    userName,
                    roleJsonString,
                    permissionJsonString,
            };
            String accessToken = TokenUtil.getInstance().generateAccessToken(issueAt, expireAt, audienceArray);
            resp.setAccessToken(accessToken);


            Calendar calendar4RefreshToken = Calendar.getInstance();
            calendar4RefreshToken.add(Calendar.MINUTE, TokenUtil.getInstance().TOKEN_EXPIRE_MINUTES * 10);
            Date expireAt4RefreshToken = calendar4RefreshToken.getTime();
            String[] audienceArray4RefreshToken = {
                    userId, userName,
            };
            String refreshToken = TokenUtil.getInstance().generateRefreshToken(issueAt, expireAt4RefreshToken, audienceArray4RefreshToken);
            resp.setRefreshToken(refreshToken);
            resp.setValid(true);
            obj.success("success", resp);

            //TokenManager加入到缓存中
            TokenManager.updateTokenCache(resp);

        } else {
            obj.fail("user not exist");
        }
        return obj;
    }


    @PostMapping(value = "/loginOut")
    public BaseResponse<LoginOutResp> loginOut(@RequestBody @Validated LoginOutReq param) {

        BaseResponse<LoginOutResp> obj = new BaseResponse<>();
        LoginOutResp resp = new LoginOutResp();

        if (param != null && StringUtils.isEmpty(param.getAccessToken()) == false) {
            UserTokenInfo userTokenInfo = TokenUtil.getInstance().decodeToken(param.getAccessToken());
            if (userTokenInfo != null
                    && StringUtils.isEmpty(userTokenInfo.getUserId()) == false) {
                TokenManager.removeTokenCache(userTokenInfo.getUserId());
                obj.success("success login out");
                return obj;
            }
        }
        obj.fail("fail to login out");
        return obj;
    }

    @PostMapping(value = "/refreshToken")
    public BaseResponse<RefreshTokenResp> refreshToken(@RequestBody @Validated RefreshTokenReq param) {

        BaseResponse<RefreshTokenResp> obj = new BaseResponse<RefreshTokenResp>();
        RefreshTokenResp resp = new RefreshTokenResp();

        String requestRefreshToken = param.getRefreshToken();
        UserTokenInfo userTokenInfo = TokenUtil.getInstance().decodeToken(requestRefreshToken);
        if (userTokenInfo != null
                && StringUtils.isEmpty(userTokenInfo.getUserId()) == false) {

            userTokenInfo = TokenManager.getUserTokenInfo(userTokenInfo.getUserId());
            boolean checkValid = userTokenInfo != null && userTokenInfo.getRefreshToken().equals(requestRefreshToken);
            if (checkValid) {

                Date issueAt = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, TokenUtil.getInstance().TOKEN_EXPIRE_MINUTES);
                Date expireAt = calendar.getTime();
                userTokenInfo.setIssuedAt(issueAt);
                userTokenInfo.setExpiresAt(expireAt);

                String[] audienceArray = {
                        userTokenInfo.getUserId(),
                        userTokenInfo.getUserName()
                };
                String accessToken = TokenUtil.getInstance().generateAccessToken(issueAt, expireAt, audienceArray);
                resp.setAccessToken(accessToken);
                userTokenInfo.setAccessToken(accessToken);

                Calendar calendar4RefreshToken = Calendar.getInstance();
                calendar4RefreshToken.add(Calendar.MINUTE, TokenUtil.getInstance().TOKEN_EXPIRE_MINUTES * 10);
                Date expireAt4RefreshToken = calendar4RefreshToken.getTime();
                String newRefreshToken = TokenUtil.getInstance().generateRefreshToken(issueAt, expireAt4RefreshToken, audienceArray);
                resp.setRefreshToken(newRefreshToken);
                userTokenInfo.setRefreshToken(newRefreshToken);
                TokenManager.updateTokenCache(userTokenInfo);

                obj.success("success refresh token", resp);
                return obj;
            }
        }

        obj.fail("fail to refresh token, refresh token is invalid");
        return obj;
    }
}
