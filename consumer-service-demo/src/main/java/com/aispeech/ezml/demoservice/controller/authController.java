package com.aispeech.ezml.demoservice.controller;


import com.aispeech.ezml.demoservice.auth.LoginReq;
import com.aispeech.ezml.demoservice.auth.LoginResp;
import com.aispeech.ezml.demoservice.base.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Slf4j
@RestController

public class authController {

////    private final OCRService OCRService;
//
//    @Autowired
//    public authController(OCRService OCRService) {
//        this.OCRService = OCRService;
//    }

    @PostMapping(value = "/login")
    public BaseResponse<LoginResp> login(@RequestBody @Validated LoginReq param) {
        BaseResponse<LoginResp> obj = new BaseResponse<LoginResp>();
        return obj;
    }


}
