package com.example.ss_les1.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }
}

/*
 за замовчуванням працює Basic   (всі картинки 2 ...)
 
 https://www.base64decode.org/
     headers from postman
 
        Authorization       Basic dXNlcjpkMjJhMzcwMC0zOWI5LTQ1MzYtOTRmZS0xZjA4NWE2MzdmM2I=
 */