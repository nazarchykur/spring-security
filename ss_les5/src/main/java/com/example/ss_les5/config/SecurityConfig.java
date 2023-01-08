package com.example.ss_les5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
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
//                    .authorizeHttpRequests(authorize -> authorize
//                            .requestMatchers("/hello").hasAuthority("read")
//                            .anyRequest().authenticated())
                .authorizeHttpRequests()
                    .requestMatchers("/hello").hasAuthority("read")
                    .anyRequest().authenticated()
//                        .anyRequest().authenticated()
//                        .anyRequest().permitAll()
//                        .anyRequest().denyAll()
//                .anyRequest().hasAuthority("read")
//                .anyRequest().hasAnyAuthority("read", "write")
//                .anyRequest().hasRole("ADMIN")
//                .anyRequest().hasAnyRole("ADMIN", "MANAGER")
//                .mvcMatchers("/hello").hasAuthority("read")
//                .anyRequest().authenticated()
                .and()
                .build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        
        var u1 = User.withUsername("john")
                .password(passwordEncoder().encode("pass"))
//                .authorities("read") // if we use .hasAuthority()
//                .authorities("ROLE_ADMIN") // if we use .hasRole()
                .roles("ADMIN")
                .build();

        var u2 = User.withUsername("bill")
                .password(passwordEncoder().encode("pass"))
//                .authorities("write")
                .roles("MANAGER")
                .build();

        inMemoryUserDetailsManager.createUser(u1);
        inMemoryUserDetailsManager.createUser(u2);
        
        return inMemoryUserDetailsManager;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

/*
    для WebApp
    
    1) який matcher method ми повинні використовувати і як саме (anyRequest(), mvcMatchers(), antMatchers(), regexMatchers())
    2) як додати різні authorization rules
    
        !!! 
            використовувати mvcMatchers() (more secured) і краще НЕ використовувати antMatchers()
            
        
    
        .authorizeHttpRequests().anyRequest().authenticated()  => endpoint level authorization
        
        коли ми викликаємо потрібний endpoint, то залежно від того, як ми тут описали правила,
        запит буде дозволений або взагалі заборонений, навіть ще до того як запит буде надісланий 
        до цього контролера
        
            .anyRequest().authenticated() - означає, що будь-який запит повинен бути аутентифікований
                matcher method + authorization rule
                1) який matcher method ми повинні використовувати і як саме
                2) як додати різні authorization rules
                
                
      ------------------------------------------------------------------------------------------------------------------          
            (див картинку postman 200 and 401)
            .anyRequest().permitAll() - означає, що для буль-якого запиту не потрібна аутентифікація
                + все рівно це дозволить зайти з нашим логіном і паролем 
                
                !!!  але якщо логін або пароль НЕ правильний, то все рівно отримаємо 401 Unauthorized  
                
                так як перше йде authentication, а потім authorization, і два працюють з SecurityContext,
                то якщо немає аутентифікації при запиті (No Auth), то у SecurityContext нічого не було добавлено
                і тому всі запити проходять без всіляких прав і обмежень
                
                якщо ж ми хочемо зайти через Auth Basic, то під час аутентифікація зміниться authentication
                і він подаде далі до SecurityContext, а так як після аутентифікації йде авторизація, то 
                цей authentication вже буде перевірятися і якщо комбінація логіну і паролю не вірні, то 
                буде 401 Unauthorized
        ------------------------------------------------------------------------------------------------------------------        
                
             .denyAll()  
                 застосовується для відхилення всіх запитів, навіть якщо вони надходять із надійного джерела з автентифікованими користувачами.
                    Це метод, необхідний для відхилення запитів.
             
                    .anyRequest().denyAll()        - всі запити відхилені
                    .antMatchers("/*").denyAll()   - всі запити по цьому патерну відхилені
                    ...
                    
        ------------------------------------------------------------------------------------------------------------------            
             (дивись картинки 401 / 403 / hasAuthority)
             
             .hasAuthority()       
                    .hasAuthority("read") - можна передати один параметр, який надасть право, наприклад, тільки читати
                        і в залежності від того які права є у юзера, давати чи не давати йому доступ до цього ендроінту
               
             .hasAnyAuthority()
                    .hasAnyAuthority("read", "write")  - можна передати кілька параметрів, і якщо юзер має хоча б якейсь
                        з цих перераховних прав, то доступ буде дозволено, в іншому випадку заборонено  403 Forbidden      
                        
        ------------------------------------------------------------------------------------------------------------------            
            .hasRole()
                    .hasRole("ADMIN") / .hasRole("MANAGER") / ...
                
                .hasRole() = використовуємо коли потрібно надати доступ до певних ендпоінтів тільки тим користувачам, 
                            які мають відповідні ролі (ADMIN / MANAGER / CALLCENTER / USER / ... )
                
                .authorities("ROLE_ADMIN")  => якщо використовуємо .authorities(), то ролі перераховуємо з приставною ROLE_
                .roles("ADMIN")    =>  якщо використовуємо .roles(), то просто перераховуємо потрібні ролі
                        
                         *- .authorities("ROLE_ADMIN") == .roles("ADMIN")  то саме
                         

            .hasAnyRole() - перевірить чи є хоча б одна із ролей
                    .hasAnyRole("ADMIN", "MANAGER")



!!! 
    загалом можна використовувати і Role і Authority разом, але краще дотримуватися чогось одного для простоти
    і надалі буде легше дебажити
             
                        
        
        ------------------------------------------------------------------------------------------------------------------            
        
        Різниця між authorizeRequests і authorizeHttpRequests
        
        authorizeHttpRequests використовує  новий спрощений AuthorizationManagerAPI та AuthorizationFilter, 
        тоді як authorizeRequests використовує AccessDecisionManager та FilterSecurityInterceptor. 
    !!! Останній буде застарілим у майбутній версії Spring Security.
        
        
        
        
   When authorizeHttpRequests is used instead of authorizeRequests, 
   then AuthorizationFilter is used instead of FilterSecurityInterceptor.  
   
    1 First, the AuthorizationFilter obtains an Authentication from the SecurityContextHolder. 
        It wraps this in an Supplier in order to delay lookup.
    2 Second, it passes the Supplier<Authentication> and the HttpServletRequest to the AuthorizationManager.
    3 If authorization is denied, an AccessDeniedException is thrown. In this case the ExceptionTranslationFilter 
        handles the AccessDeniedException.
    4 If access is granted, AuthorizationFilter continues with the FilterChain which allows the application 
        to process normally.
    We can configure Spring Security to have different rules by adding more rules in order of precedence.      
    
    
                 
        @Bean
        SecurityFilterChain web(HttpSecurity http) throws Exception {
            http
                // ...
                .authorizeHttpRequests(authorize -> authorize                                                1)
                    .requestMatchers("/resources/**", "/signup", "/about").permitAll()                       2)
                    .requestMatchers("/admin/**").hasRole("ADMIN")                                           3)
                    .requestMatchers("/db/**").access(new WebExpressionAuthorizationManager("hasRole('ADMIN') and hasRole('DBA')"))  4)   
                    .anyRequest().denyAll()                                                                  5)
                );
        
            return http.build();
        }   
        
    1) Вказано кілька правил авторизації. Кожне правило розглядається в порядку їх оголошення.
    2) Ми вказали кілька шаблонів URL-адрес, до яких може отримати доступ будь-який користувач. Зокрема, будь-який 
        користувач може отримати доступ до запиту, якщо URL-адреса починається з "/resources/", або "/signup", або "/about".    
    3) Будь-яка URL-адреса, яка починається з "/admin/", буде доступна лише користувачам із роллю "ROLE_ADMIN". 
        Зауважте, оскільки ми викликаємо метод hasRole, то не потрібно вказувати префікс "ROLE_".    
    4) Будь-яка URL-адреса, яка починається з "/db/", вимагає від користувача одночасно "ROLE_ADMIN" і "ROLE_DBA". 
        Зауважте, оскільки ми використовуємо hasRole вираз, нам не потрібно вказувати префікс "ROLE_".    
    5) Доступ до будь-якої іншої URL-адреси, заборонено. 
        Це хороша стратегія, якщо ви не хочете випадково забути оновити свої правила авторизації.     
        
        
        
        
      
      
      2 варіанти:
     
     1) використовуємо 
      .authorizeHttpRequests(authorize -> authorize
         .requestMatchers("/hello").hasAuthority("read")
         .anyRequest().authenticated())
             
      2)       
        .authorizeHttpRequests()
            .requestMatchers("/hello").hasAuthority("read")
            .anyRequest().authenticated()  
        
        
        
                        
 */