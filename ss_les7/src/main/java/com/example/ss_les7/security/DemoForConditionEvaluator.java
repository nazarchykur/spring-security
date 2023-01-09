package com.example.ss_les7.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DemoForConditionEvaluator {

    // choose the appropriate method name
    public boolean condition() {
        /*
            тут можна з контексту взяти Authentication для перевірки / для отримання інформації з Authentication / і т.д
         */
        var a = SecurityContextHolder.getContext().getAuthentication();


        // наша складна логіка щодо authorization condition
        return true;
    }

}
