package com.example.ss_les7.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/demo1")
    @PreAuthorize("hasAuthority('read')") // hasAuthority() hasAnyAuthority() hasRole() hasAnyRole()
    public String demo() {
        return "demo1";
    }

    @GetMapping("/demo2")
    @PreAuthorize("hasAnyAuthority('read', 'write')")
    public String demo2() {
        return "demo2";
    }

    @GetMapping("/demo3/{smth}")
    @PreAuthorize("#something == authentication.name") // authentication from SecurityContext
    public String demo3(@PathVariable("smth") String something) {
        return "demo3";
    }
    
}

/*

    через SpEl ми маємо доступ до параметрів і можемо брати цю змінну для перевірки  
    authentication - це об'єкт який зберігається у SecurityContext 
 */


/*
     @PreAuthorize("hasAuthority('read')") - означає, що юзер може викликати цей ендпоінт тільки тоді якщо має права 'read'
 */