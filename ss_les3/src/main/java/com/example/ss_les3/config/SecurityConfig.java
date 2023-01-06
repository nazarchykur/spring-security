package com.example.ss_les3.config;

import com.example.ss_les3.config.security.filters.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    
    /*
        cannot use anymore, because WebSecurityConfigurerAdapter is deprecated 
            public class SecurityConfig extends WebSecurityConfigurerAdapter {
     */

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .build();
    }
}
/*
    !!! на перших уроках ми не додавали ніяких фільтрів, і знали, що по замовчуванню спрінг додасть конфігурацію,
            і у нас кожен ендпоіт буде перевірятися 
 */

/*
    використовуємо свій фільтр CustomAuthenticationFilter
        
     > .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) - означає, що коли буде 
                відпрацьовувати UsernamePasswordAuthenticationFilter, то у цей момент добавити наш фільтр
 
 
    > .authorizeHttpRequests().anyRequest().authenticated() - пізніше повернемося до цього, але поки що потрібно знати, 
            що кожний авторизаційний ріквест повинен бути аутентифікований 
 */