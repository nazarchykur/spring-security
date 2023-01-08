package com.example.ss_les6.config;

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
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();

        UserDetails u1 = User.withUsername("bill")
                .password(passwordEncoder().encode("pass"))
                .authorities("read")
                .build();

        UserDetails u2 = User.withUsername("john")
                .password(passwordEncoder().encode("pass"))
                .authorities("write")
                .build();

        inMemoryUserDetailsManager.createUser(u1);
        inMemoryUserDetailsManager.createUser(u2);

        return inMemoryUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                    .httpBasic()
                .and()
                    .authorizeHttpRequests()
//                        .anyRequest().authenticated()
                        .requestMatchers("/test1").authenticated()
                        .requestMatchers("/test2").hasAuthority("read")
//                        .requestMatchers("/test2").permitAll()
                .and()
                .build();
    }
}

/*

    !!! нагадування 
        401 Unauthorized - означає, що користувач (юзер) НЕ автентифікований
        403 Forbidden    - означає, що користувач автентифікований, але НЕ пройшов авторизацію і НЕМАЄ прав доступу на такий-то запит


 є 2 підходи:
    1)
        .authorizeHttpRequests()
                        .anyRequest().authenticated()
                        
    2)  .authorizeHttpRequests(
            auth -> auth.anyRequest().authenticated()
         )                 
   
 ----------------------------------------------------------------------------------------------------------------------
 
    > .anyRequest().authenticated()  - будь-який запит повинен бути аутентифікований
    > .requestMatchers("/test1").authenticated() - запит "/test1" має бути аутентифікований
    > .requestMatchers("/test2").permitAll() - на запит "/test2" є доступ для всіх
    > .requestMatchers("/test2").hasAuthority("read") - на запит "/test2" має право доступу тільки юзер з Authority("read")
       
       
       
   !!! раніше використовували 
            .mvcMatchers()   - цей варіант пріоритетніший
            .antMatchers()                     
 */