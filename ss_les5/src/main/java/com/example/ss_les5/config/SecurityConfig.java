package com.example.ss_les5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic()
                .and()
                    .authorizeHttpRequests().anyRequest().authenticated()
                .and()
                .build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        
        UserDetails userDetails = User.withUsername("john")
                .password(passwordEncoder().encode("pass"))
                .authorities("read")
                .build();

        inMemoryUserDetailsManager.createUser(userDetails);
        
        return inMemoryUserDetailsManager;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

/*
    для WebApp
    
    
        .authorizeHttpRequests().anyRequest().authenticated()  => endpoint level authorization
        
        коли ми викликаємо потрібний endpoint, то залежно від того, як ми тут описали правила,
        запит буде дозволений або взагалі заборонений, навіть ще до того як запит буде надісланий 
        до цього контролера
        
            .anyRequest().authenticated() - означає, що будь-який запит повинен бути аутентифікований
                matcher method + authorization rule
                1) який matcher method ми повинні використовувати і як саме
                2) як додати різні authorization rules
 */