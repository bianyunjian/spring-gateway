package com.aispeech.ezml.demoservice.controller;

import com.aispeech.ezml.demoservice.base.BaseResponse;
import com.aispeech.ezml.demoservice.pojo.UserInfoReq;
import com.aispeech.ezml.demoservice.pojo.UserInfoResp;
import com.aispeech.ezml.demoservice.support.RequestUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/hello")
    public String hello() {

        String userId = RequestUtil.getRequestUserId();
        String userName = RequestUtil.getRequestUserName();
        return "hello  " + userId + " " + userName;
    }

    @PostMapping("/getMessage")
    public String getMessage() {
        return "welcome";
    }

    @PostMapping(value = "/getUserInfo")
    public BaseResponse<UserInfoResp> login(@RequestBody @Validated UserInfoReq param) {

//        String userId = RequestUtil.getRequestUserId();
        BaseResponse<UserInfoResp> obj = new BaseResponse<UserInfoResp>();
        UserInfoResp resp = new UserInfoResp();
        resp.setUserId("10001");
        resp.setUserName(param.getUserName());
        resp.setPassword("MTIzNDU2"); //123456 ==>base64
        obj.success("success", resp);
        return obj;
    }
}
