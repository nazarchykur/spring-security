package com.example.ss_les6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .authorities("write", "delete")
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
//                        .requestMatchers("/test1").authenticated()
//                        .requestMatchers("/test2").hasAuthority("read")
//                        .requestMatchers("/test2").permitAll()
//                        .requestMatchers("/demo/**").hasAuthority("read")
                        .requestMatchers(HttpMethod.GET, "/demo/**").hasAuthority("read")
                .anyRequest().authenticated()
                .and().csrf().disable() // DON'T DO THAT IN REAL APPS
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
            
            
    можна використовувати деякий перелік запитів   .requestMatchers("/test1", "/test3")        
    але зручніше і практичніше описати певну групу ендпоінтів, тобто певні запити, які будуть об'єднані
    спільним префіксом, наприклад, 
            .requestMatchers("/demo/**")
                і будемо знати, що /demo/test1  i /demo/test2  підпадають під цей патерн
                
                
    Різниця між "/*" і "/**" полягає в тому, що другий відповідає всьому дереву каталогів, включаючи підкаталоги, 
    a "/*" відповідає лише на рівні, на якому він указаний.
    
        Отже, усі ці URL-адреси відповідають тексту з шаблоном "/**".
            /book
            /book/20
            /book/20/author
            
        URL-адреси збігаються з "/*"
            /book
            /magazine
            
----------------------------------------------------------------------------------------------------------------------

    .requestMatchers(HttpMethod.GET, "/demo/**").hasAuthority("read") - означає, що по патерну "/demo/**" можна 
        заходити всім користувачам, але GET метод по цій урлі можна лише тим, хто має "read" права




----------------------------------------------------------------------------------------------------------------------
        Cross-Site Request Forgery (CSRF) 
        
        Міжсайтова підробка запитів (CSRF, іноді також називається XSRF) - це атака, яка може змусити кінцевого 
        користувача за допомогою веб-програми несвідомо виконати дії, які можуть поставити під загрозу безпеку.
        
        
        !!! починаючи з Spring Security 4.0, захист CSRF увімкнено за замовчуванням .
        
        
        Як працює стандартний захист Spring CSRF?
        
        Spring Security використовує шаблон маркера синхронізатора для генерації маркера CSRF, який захищає від атак CSRF.

        Особливості токена CSRF:
            > Маркер CSRF за замовчуванням генерується на сервері за допомогою Spring framework.
            
            > Цей маркер CSRF має бути частиною кожного запиту HTTP. Це не є частиною файлу cookie, оскільки браузер 
                автоматично включає файли cookie з кожним запитом HTTP .
                
            > Коли надсилається HTTP-запит, Spring Security порівнює очікуваний CSRF-токен із тим, що надіслано в HTTP-запиті . 
                Запит буде оброблено, лише якщо значення маркера збігаються, інакше запит буде розглядатися як 
                підроблений і буде відхилено зі статусом 403 (Заборонено) .
                
            > Маркер CSRF зазвичай додається до запитів, які змінюють стан, наприклад POST, PUT, DELETE, PATCH .
            
            > Ідемпотентні методи, такі як GET, не вразливі до атак CSRF, оскільки вони не змінюють стан на стороні 
                сервера та захищені тією самою політикою походження .
                
                
                
----------------------------------------------------------------------------------------------------------------------
https://stackoverflow.com/questions/28907030/spring-security-authorize-request-for-certain-url-http-method-using-httpsecu/74633151#74633151

https://stackoverflow.com/questions/74683225/updating-to-spring-security-6-0-replacing-removed-and-deprecated-functionality

У Spring Security 6.0 antMatchers(), а також інші методи конфігурації для захисту запитів 
    ( а саме mvcMatchers() та regexMatchers() ) були видалені з API.

    Перевантажений метод requestMatchers() був введений як уніфікований засіб для захисту запитів. 
     requestMatchers() полегшують усі способи обмеження запитів, які підтримувалися вилученими методами.
     
     Крім того, метод authorizeRequests() застарів (deprecated), і його більше не слід використовувати. 
     Рекомендована заміна - authorizeHttpRequests()
     
     
     Ось як ваш SecurityFilterChain може бути визначений у Spring Security 6.0:
     
     public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/token/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .httpBasic(Customizer.withDefaults())
            .build();
     }
     
     Щодо застарілої анотації@EnableGlobalMethodSecurityйого було замінено на @EnableMethodSecurity. 
     Обґрунтування цієї зміни полягає в тому, що з @EnableMethodSecurity властивістю prePostEnabled, необхідною 
     для ввімкнення використання, @PreAuthorize/@PostAuthorizeі @PreFilter/@PostFilterза замовчуванням встановлено на true.
     
     Тож вам більше не потрібно писати prePostEnabled = true, достатньо буде лише анотувати ваш 
     клас конфігурації @EnableMethodSecurity.
     
         
                  
 */