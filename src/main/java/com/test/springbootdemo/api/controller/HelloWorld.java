package com.test.springbootdemo.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {

    @RequestMapping("/hello")
    public String hello01() {
        return "hello SpringBoot";
    }

    @RequestMapping("/hello1")
    public String hello02() {
        String s = "hhh";
        return "hahah";
    }
}
