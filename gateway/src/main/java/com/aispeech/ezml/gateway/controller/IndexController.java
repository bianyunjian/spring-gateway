package com.aispeech.ezml.gateway.controller;


import com.aispeech.ezml.gateway.auth.*;
import com.aispeech.ezml.gateway.base.BaseResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;

@RestController
public class IndexController {

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

        obj.success("success", resp);
        return obj;
    }

    @PostMapping(value = "/loginOut")
    public BaseResponse<LoginOutResp> loginOut(@RequestBody @Validated LoginOutReq param) {

        BaseResponse<LoginOutResp> obj = new BaseResponse<LoginOutResp>();
        LoginOutResp resp = new LoginOutResp();

        obj.success("success", resp);
        return obj;
    }

    @PostMapping(value = "/refreshToken")
    public BaseResponse<RefreshTokenResp> refreshToken(@RequestBody @Validated RefreshTokenReq param) {

        BaseResponse<RefreshTokenResp> obj = new BaseResponse<RefreshTokenResp>();
        RefreshTokenResp resp = new RefreshTokenResp();

        obj.success("success", resp);
        return obj;
    }
}
