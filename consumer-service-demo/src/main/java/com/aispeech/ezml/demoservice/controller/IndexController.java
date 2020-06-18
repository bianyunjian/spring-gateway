package com.aispeech.ezml.demoservice.controller;

import com.aispeech.ezml.demoservice.auth.LoginReq;
import com.aispeech.ezml.demoservice.auth.LoginResp;
import com.aispeech.ezml.demoservice.base.BaseResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello  " + name;
    }

    @PostMapping("/getMessage")
    public String getMessage() {
        return "welcome";
    }

    @PostMapping(value = "/login")
    public BaseResponse<LoginResp> login(@RequestBody @Validated LoginReq param) {

        String userId = RequestUtil.getRequestUserId();
        BaseResponse<LoginResp> obj = new BaseResponse<LoginResp>();
        LoginResp resp = new LoginResp();
        resp.setUserId(userId);
        resp.setAccess_token(param.getPassword());
        resp.setRefresh_token(param.getUserName());
        obj.success("success", resp);
        return obj;
    }
}
