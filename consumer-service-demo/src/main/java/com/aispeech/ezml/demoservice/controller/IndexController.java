package com.aispeech.ezml.demoservice.controller;

import com.aispeech.ezml.demoservice.base.BaseResponse;
import com.aispeech.ezml.demoservice.pojo.UserInfoReq;
import com.aispeech.ezml.demoservice.pojo.UserInfoResp;
import com.aispeech.ezml.demoservice.support.RequestUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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

    @PostMapping(value = "/upload")
    public String upload(@RequestParam("name") String name,
                         @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("name") String name) {

        try {
            File file = new File(name);
            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream stream =
                    new BufferedInputStream(new FileInputStream(file));
            stream.read(bytes);
            return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
