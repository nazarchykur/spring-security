package com.example.ss_les4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsLes4Application {

    public static void main(String[] args) {
        SpringApplication.run(SsLes4Application.class, args);
    }

}

/*
маємо наш фільтр (CustomFilter) ApiKeyFilter, і наш AuthenticationManager, який делегує AuthenticationProvider
цей Authentication

як ми вже знаємо цепочка фільтрів йде одне за одним
у даному випадку у нас є ApiKeyFilter і HTTP Basic фільтри, 
    тобто перше буде відпрацьовувати наш кастомний через x-api-key у хедері,
    якщо немає, то тоді звичайний HTTP Basic фільтр через логін і пароль
    
        * для AuthenticationProvider цього разу ми використали дефолтну реалізацію, 
           але там має бути UserDetailsService + PasswordEncoder
    
    

        CustomFilter                              DefaultFilter
        
            +--------------+                       +--------------+
     -->    | ApiKeyFilter |   ---------------->   |  HTTP Basic  |  -------->  ...   
            +--------------+                       +--------------+
                   |                                      |
        +-----------------------+                +-----------------------+
        | AuthenticationManager |                | AuthenticationManager |        
        +-----------------------+                +-----------------------+         +--------------------+
                   |                                       |                  |--- | UserDetailsService |
        +------------------------+              +------------------------+    |    +--------------------+
        | AuthenticationProvider |              | AuthenticationProvider | ---|       
        +------------------------+              +------------------------+    |    +-------------------+
             /                                                                |--- |  PasswordEncoder  |                  
            /                                                                      +-------------------+
       secret Key      
 */