package com.example.ss_les2.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        /*
            можемо отримати з SecurityContextHolder в потрібному місці аутентифікацію і перевірити її
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(System.out::println);
        
        return "Hello!";
    }
}
/*

Basic Auth
        Username: john
        Password: 12345
 */