package com.example.ss_les4.config;

import com.example.ss_les4.config.filters.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {
    
    @Value("${the.secret.key}")
    private String key;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
//                .and().authenticationManager() // or by adding a bean of type AuthenticationManager
//                .and().authenticationProvider() // it doesn`t override the AuthProvider, it adds one more to the collection
                .and()
                    .addFilterBefore(new ApiKeyFilter(key), BasicAuthenticationFilter.class)
                    .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .build();
    }
}

/*
    .httpBasic()

        Виклик цього методу HttpSecurity ввімкне Http Basic Authentication для вашої програми з деякими 
        «розумними» значеннями за замовчуванням.
        
        Він поверне HttpBasicConfigurer для подальшого налаштування.
        
        BasicAuthenticationFilter обробляє запит і перевіряє, чи містить запит заголовок автентифікації чи ні
        
        Коли httpBasic() викликається, ми повідомляємо Spring автентифікувати запит за допомогою значень, 
        переданих Authorization заголовком запиту. Якщо запит не автентифікований, 
        ви отримаєте статус 401 і повідомлення про помилкуUnauthorized
        
 */