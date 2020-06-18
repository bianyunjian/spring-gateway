package com.aispeech.ezml.demoservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

}
