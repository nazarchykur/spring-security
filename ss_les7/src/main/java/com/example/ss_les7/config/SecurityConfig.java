package com.example.ss_les7.config;

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
        return http.httpBasic()
                .and()
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and().build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails u1 = User.withUsername("john")
                .password(passwordEncoder().encode("pass"))
                .authorities("read")
                .build();

        UserDetails u2 = User.withUsername("bill")
                .password(passwordEncoder().encode("pass"))
                .authorities("write")
                .build();

        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
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
            
            
 ----------------------------------------------------------------------------------------------------------------------
 https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
            
https://stackoverflow.com/questions/74683225/updating-to-spring-security-6-0-replacing-removed-and-deprecated-functionality

У Spring Security 6.0 antMatchers(), а також інші методи конфігурації для захисту запитів 
    ( а саме mvcMatchers() та regexMatchers() ) були видалені з API.

    Перевантажений метод requestMatchers() був введений як уніфікований засіб для захисту запитів. 
     requestMatchers() полегшують усі способи обмеження запитів, які підтримувалися вилученими методами.
     
     Крім того, метод authorizeRequests() застарів (deprecated), і його більше не слід використовувати. 
     Рекомендована заміна - authorizeHttpRequests() 
     
     
     Щодо застарілої анотації @EnableGlobalMethodSecurity його було замінено на @EnableMethodSecurity. 
     Обґрунтування цієї зміни полягає в тому, що з @EnableMethodSecurity властивістю prePostEnabled, необхідною 
     для ввімкнення використання, @PreAuthorize/@PostAuthorize і @PreFilter/@PostFilter за замовчуванням встановлено на true.
     
     Тож вам більше не потрібно писати prePostEnabled = true, достатньо буде лише анотувати ваш 
     клас конфігурації @EnableMethodSecurity.     
     
     > old version:
      
            @EnableWebSecurity
            @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
            public class SecurityConfig {
                // config
            }
            
      > new version Spring Security 6.0... 
      
            @Configuration 
            @EnableMethodSecurity
            public class SecurityConfig {
                // config
            }

                    boolean prePostEnabled() default true;
                    boolean jsr250Enabled() default false;
                    boolean proxyTargetClass() default false;                     
 */


// ====================================================================================================================
/*
https://javatechonline.com/spring-security-annotations/

Spring Security Annotations With Examples   (August 4, 2022)

    @EnableWebSecurity
    Spring Boot already provides some basic security configurations. In contrast, while doing customizations on 
    Web Security, we generally create a custom configuration class that extends WebSecurityConfigurerAdapter. 
    That custom configuration class becomes the candidate for applying @EnableWebSecurity annotation.


     Note: Please note that the WebSecurityConfigurerAdapter is deprecated from Spring Security 5.7.0-M2. 
            Hence, we will not be using WebSecurityConfigurerAdapter in our custom configuration class. 
            Instead, there will be a new way to implement the methods in our custom configuration class. 
            Please refer a separate article ‘Spring Security Without WebSecurityConfigurerAdapter‘ on this. 
            However, we will use @EnableWebSecurity as it is.
     

            @Configuration
            @EnableWebSecurity
            public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
            
                @Override
                protected void configure(AuthenticationManagerBuilder auth) {
                                 // enable in memory based authentication with a user named "user" and "admin"
                    auth.inMemoryAuthentication()
                           .withUser("user")
                           .password("password")
                           .roles("USER")
                           .and()
                           .withUser("admin")
                           .password("password")
                           .roles("USER", "ADMIN");
                 }
            
                @Override
                protected void configure(HttpSecurity http) throws Exception {
                   http.authorizeRequests().antMatchers("/public/**").permitAll().anyRequest()
                          .hasRole("USER").and()
                                // Possibly more configuration ...
                         .formLogin() // enable form based log in
                               // set permitAll for all URLs associated with Form Login
                         .permitAll();
                 }
                              // Possibly more overridden methods ...
            }

    The above example demonstrates the concept of in-Memory database authentication using Spring Security.

@EnableGlobalMethodSecurity

        We can enable annotation-based security using the @EnableGlobalMethodSecurity annotation on any @Configuration 
        annotated class. By default, global method security is disabled, so if you want to use this functionality, 
        you first need to enable it. Hence, in order to get access of annotations such as 
        @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed, you first need to enable Global Method Security by 
        applying @EnableGlobalMethodSecurity annotation to any @Configuration annotated java class. 

                @EnableWebSecurity
                @EnableGlobalMethodSecurity(
                        prePostEnabled = true,  // Enables @PreAuthorize and @PostAuthorize
                        securedEnabled = true, // Enables @Secured 
                        jsr250Enabled = true    // Enables @RolesAllowed (Ensures JSR-250 annotations are enabled)
                 )
                @Configuration 
                 public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
                      @Override 
                      protected void configure(HttpSecurity http) throws Exception {
                              ....
                      }
                       // some other overriden methods
                }

     Note: If you are using Spring 3.0.0 or a later version, you will get @EnableGlobalMethodSecurity as deprecated. 
            You need to use @EnableMethodSecurity in place of @EnableGlobalMethodSecurity.



@Secured  vs  @RolesAllowed
        
        We use @Secured on a method to specify a list of roles who can access the method. If there are multiple roles, 
        user can access that method if the user has at least one of the specified roles.
         
                @Secured("ROLE_MANAGER","ROLE_ADMIN")
                public String getUserDetails() {
                           SecurityContext securityContext = SecurityContextHolder.getContext();
                           return securityContext.getAuthentication().getName();
                }

        In the example above, If a user has either Admin or Manager role, that user can access the getUserDetails() method.
        
        
        We use @RolesAllowed in a similar way as @Secured. The @RolesAllowed annotation is the JSR-250’s equivalent 
        annotation of the @Secured annotation. However, @Secured annotation doesn’t support SpEL(Spring Expression Language).
        
        
@PreAuthorize and  @PostAuthorize
    
    If we have a requirement where we want to apply a conditional or expression based access restriction to a method, 
    then we can use @PreAuthorize and @PostAuthorize annotations. Moreover, we can write expressions using SpEL 
    (Spring Expression Language). The @PreAuthorize annotation validates the provided expression before entering into 
    the method. In contrast, the @PostAuthorize annotation checks it after the execution of the method and could modify the result.
    
    
    @PreAuthorize

        @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        public String getUserDetails() {
                   SecurityContext securityContext = SecurityContextHolder.getContext();
                   return securityContext.getAuthentication().getName();
        }
        
        
        If you compare the example from @Secured annotation, the 
            @PreAuthorize(“hasRole(‘ROLE_MANAGER’) or hasRole(‘ROLE_ADMIN’)”) 
        is another way of writing 
            @Secured(“ROLE_MANAGER”,”ROLE_ADMIN”). 
        Consequently, The @PreAuthorize(“hasRole(‘ROLE_ADMIN’)”) is equivalent to @Secured(“ROLE_ADMIN”) and both have the same meaning.
        

    @PostAuthorize
    
        As aforementioned, @PostAuthorize annotation allows the business logic of a method to execute first and only 
        then, the security expression it contains will be evaluated. So, be careful while applying this annotation. 
        It is recommended that do not use this annotation with methods that perform modifying queries like for example 
        Delete User or Update User. The @PostAuthorize annotation has access to an object that the method is going 
        to return. We can access the object that the method is going to return in a security expression via the 
        ‘returnObject’. Lets write an example of @PostAuthorize using this concept.
    
            @PostAuthorize("returnObject.userId == authentication.principal.userId")
            public User getUserDetail(String username) {
                  return userRepository.getUserByUserName(username);
            }
        
        
        In this example, the getUserDetail( ) method would only execute successfully if the userId of the returned 
        User is equal to the current authentication principal’s userId.


@EnableMethodSecurity

    In Spring Security 5.6, we can enable annotation-based security using the @EnableMethodSecurity annotation in place 
    of @EnableGlobalMethodSecurity on any @Configuration annotated class. 
    It enables @PreAuthorize, @PostAuthorize, @PreFilter, and @PostFilter by default and also complies with JSR-250. 
    For example, below code snippets demonstrate the concept of using this annotation.


    Example#1: To enable Spring Security’s @PreAuthorize annotation
    
            @EnableMethodSecurity
            @Configuration
            public class MySecurityConfig {
                 // ... 
            }
            
        We can add an annotation to a method (on a class or interface). It will then limit the access to that method accordingly. 
    
            public interface PaymentService {
               @PreAuthorize("hasRole('USER')") 
               Double readAmount(Long id);
            
               @PreAuthorize("hasRole('USER')") 
               List<Account> findAccount(); 
             }

    
    
    Example#2: To enable support for Spring Security’s @Secured annotation 
    
            @EnableMethodSecurity(securedEnabled = true) 
            @Configuration
            public class MySecurityConfig { 
                // ...
            }



    Example#3: To enable support for JSR-250 annotations
    
            @EnableMethodSecurity(jsr250Enabled = true) 
            @Configuration
            public class MySecurityConfig {
                // ... 
            }



        
 */

// ====================================================================================================================
/*

https://javatechonline.com/spring-security-without-websecurityconfigureradapter/

    Spring Security Without WebSecurityConfigurerAdapter
    
    WebSecurityConfigurerAdapter is an abstract class which has been deprecated from Spring Security 5.7.0-M2 as per 
    an announcement posted in the Spring Official website, on 21st Feb, 2022. It was generally used to extend configure() 
    methods by a custom configuration subclass. As a result, it encourages users to move towards a component-based 
    security configuration. To support with the change to this new design of configuration, we will discuss a list of 
    common use-cases and the proposed alternatives going forward. Therefore, we will discuss about the implementation 
    of use cases of Spring Security Without WebSecurityConfigurerAdapter.
    
    
    What is WebSecurityConfigurerAdapter?
    
    WebSecurityConfigurerAdapter is an abstract class provided by the Spring Security module. Generally, we use it to 
    override its configure() methods in order to define our security configuration class. Typically, we use two 
    configure() methods with different parameters while implementing Spring Security in our application. One is used 
    to declare authentication related configurations whereas the other one is to declare authorization related configurations. 
    The code looks like below code snippet.
                    @Configuration
                    @EnableWebSecurity
                    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {    
                        
                        @Override
                        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                             // configure Authentication ......
                        }
                        @Override
                        protected void configure(HttpSecurity http) throws Exception {
                            // configure Authorization ......           
                        }       
                    }
    
        If you work with Spring Boot 2.7.0 & maven, it will automatically download Spring Security 5.7.0 or higher version. 
        In that case, you will find WebSecurityConfigurerAdapter deprecated. If you still want to use this class without 
        deprecation, you can change Spring Boot version to lower version(e.g. 2.6.6 ) in your pom.xml
        
    
    Where do we need to implement this change?
            
            1) If you are working on Spring Boot 2.7.0 or higher versions
        
            2) If you are working on Spring Security 5.7.0 or higher versions
            
            3) If your project is getting upgraded or migrated to higher versions as described above
            
            4) If you want to customize your Spring Security Configuration using the latest version of Spring Boot
            
            5) If you want to remove annoying warnings of  WebSecurityConfigurerAdapter Deprecated
            
            6) If you want to implement Spring Security Without WebSecurityConfigurerAdapter.
    
------------------------------------------------------------------------------------------------------------------------    
    
    Example#1: With WebSecurityConfigurerAdapter
    
        This example demonstrates the HttpSecurity configuration. Typically, we write it to declare the authorization artifacts.
        
                @Configuration 
                @EnableWebSecurity
                public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
                
                   @Override 
                   protected void configure(HttpSecurity http) throws Exception { 
                     http.cors().and().csrf().disable()
                         .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                         .and().authorizeRequests() 
                         .antMatchers("/home").permitAll()
                         .antMatchers("/welcome").authenticated()
                         .antMatchers("/admin").hasAuthority("ADMIN")
                         .antMatchers("/emp").hasAuthority("EMPLOYEE")
                         .antMatchers("/mgr").hasAuthority("MANAGER")
                         .anyRequest().authenticated()
                     ; 
                   }
                }
    
    
    Example#1: Without WebSecurityConfigurerAdapter
        
        The below code demonstrates the possible solution of implementing Spring Security Without WebSecurityConfigurerAdapter.
    
                @Configuration 
                @EnableWebSecurity
                public class WebSecurityConfig {
                
                   @Bean
                   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
                      http.cors().and().csrf().disable()
                        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and().authorizeRequests() 
                        .antMatchers("/home").permitAll()
                        .antMatchers("/welcome").authenticated()
                        .antMatchers("/admin").hasAuthority("ADMIN")
                        .antMatchers("/emp").hasAuthority("EMPLOYEE")
                        .antMatchers("/mgr").hasAuthority("MANAGER")
                        .anyRequest().authenticated();
                      return http.build();
                   }
                }
    
------------------------------------------------------------------------------------------------------------------------
    
    Example#2: With WebSecurityConfigurerAdapter
        In the example below, we have used both methods.
        

                @Configuration
                @EnableWebSecurity
                public class SecurityConfig extends WebSecurityConfigurerAdapter {
                
                   @Override
                   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                
                      // {noop} => No operation for password encoder (no password encoding needed)
                      auth.inMemoryAuthentication()
                          .withUser("devs")
                          .password ("{noop} devs") //no password encoding needed
                          .authorities("ADMIN");
                
                      auth.inMemoryAuthentication().withUser("ns").password("{noop}ns").authorities("EMPLOYEE");
                      auth.inMemoryAuthentication().withUser("vs").password("{noop}vs").authorities("MANAGER");
                   }
                
                   @Override
                   protected void configure(HttpSecurity http) throws Exception {
                
                      //declares which Page(URL) will have What access type
                      http.authorizeRequests()
                          .antMatchers("/home").permitAll()
                          .antMatchers("/welcome").authenticated()
                          .antMatchers("/admin").hasAuthority("ADMIN")
                          .antMatchers("/emp").hasAuthority("EMPLOYEE")
                          .antMatchers("/mgr").hasAuthority("MANAGER")
                          .antMatchers("/common").hasAnyAuthority("EMPLOYEE","MANAGER")
                
                      // Any other URLs which are not configured in above antMatchers
                      // generally declared authenticated() in real time
                          .anyRequest().authenticated()
                
                      // Login Form Details
                         .and()
                         .formLogin()
                         .defaultSuccessUrl("/welcome", true)
                
                      // Logout Form Details
                        .and()
                        .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                
                      // Exception Details 
                        .and() 
                        .exceptionHandling()
                        .accessDeniedPage("/accessDenied")
                      ;
                   }
                }
                
                
                
    Example#2: Without WebSecurityConfigurerAdapter
    
    
                @Configuration
                @EnableWebSecurity
                public class SecurityConfigNew {
                
                    @Bean
                    protected InMemoryUserDetailsManager configAuthentication() {
                
                       List<UserDetails> users = new ArrayList<>();
                       List<GrantedAuthority> adminAuthority = new ArrayList<>();
                       adminAuthority.add(new SimpleGrantedAuthority("ADMIN"));
                       UserDetails admin= new User("devs", "{noop}devs", adminAuthority);
                       users.add(admin);
                
                       List<GrantedAuthority> employeeAuthority = new ArrayList<>();
                       adminAuthority.add(new SimpleGrantedAuthority("EMPLOYEE"));
                       UserDetails employee= new User("ns", "{noop}ns", employeeAuthority);
                       users.add(employee);
                
                       List<GrantedAuthority> managerAuthority = new ArrayList<>();
                       adminAuthority.add(new SimpleGrantedAuthority("MANAGER"));
                       UserDetails manager= new User("vs", "{noop}vs", managerAuthority);
                       users.add(manager);
                
                       return new InMemoryUserDetailsManager(users);
                    }
                
                    @Bean
                    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                
                        //declares which Page(URL) will have What access type
                        http.authorizeRequests()
                            .antMatchers("/home").permitAll()
                            .antMatchers("/welcome").authenticated()
                            .antMatchers("/admin").hasAuthority("ADMIN")
                            .antMatchers("/emp").hasAuthority("EMPLOYEE")
                            .antMatchers("/mgr").hasAuthority("MANAGER")
                            .antMatchers("/common").hasAnyAuthority("EMPLOYEE","MANAGER")
                
                        // Any other URLs which are not configured in above antMatchers
                        // generally declared aunthenticated() in real time
                           .anyRequest().authenticated()
                
                        // Login Form Details
                           .and()
                           .formLogin()
                           .defaultSuccessUrl("/welcome", true)
                
                        // Logout Form Details
                          .and()
                          .logout()
                         .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                
                        // Exception Details 
                         .and() 
                         .exceptionHandling()
                        .accessDeniedPage("/accessDenied")
                        ;
                    return http.build();
                    }
                }   
                
                
------------------------------------------------------------------------------------------------------------------------

    Example#3: With WebSecurityConfigurerAdapter
    
            @Configuration 
            @EnableWebSecurity
            public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
            
                @Autowire
                UserDetailsService userDetailsService;
            
                @Override
                public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
                    authenticationManagerBuilder.userDetailsService(userDetailsService)
                                                .passwordEncoder(passwordEncoder());
                }
            
                @Bean
                public PasswordEncoder passwordEncoder() {
                    return new BCryptPasswordEncoder();
                }
            }
            
            
            

    Example#3: Without WebSecurityConfigurerAdapter
    
        @Configuration 
        @EnableWebSecurity
        public class WebSecurityConfig {
        
            @Bean
            AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { 
                return authenticationConfiguration.getAuthenticationManager();
            }
        }
        
        
        Here, in the old version we inject AuthenticationManagerBuilder, set userDetailsService, passwordEncoder and build it. 
        But AuthenticationManager is already created in this step. It is created the way we wanted 
        (with userDetailsService and the passwordEncoder).
        
 
 ------------------------------------------------------------------------------------------------------------------------
 
     Example#4: With WebSecurityConfigurerAdapter
     
            While implementing configuration for web security, the WebSecurityCustomizer is a callback interface 
            that can be used to customize WebSecurity.
            
                    @Configuration  
                    @EnableWebSecurity
                    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
                    
                        @Override
                        public void configure(WebSecurity web) {
                            web.ignoring().antMatchers("/ignore1", "/ignore2");
                        }
                    }


    Example#4: Without WebSecurityConfigurerAdapter
        Spring Security 5.7.0-M2 onward, the recommended way of doing this is by registering a WebSecurityCustomizer 
        bean. The below code demonstrates the possible solution of implementing Spring Security 
        Without WebSecurityConfigurerAdapter.:
    
                @Configuration  
                @EnableWebSecurity
                public class SecurityConfiguration {
                
                    @Bean
                    public WebSecurityCustomizer webSecurityCustomizer() {
                        return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
                    }
                }




------------------------------------------------------------------------------------------------------------------------


    Example#5: With WebSecurityConfigurerAdapter
        Below code demonstrates the changes in JDBC authentication in the context of Spring Security.
        
            @Configuration
            @EnableWebSecurity
            public class SecurityConfig extends WebSecurityConfigurerAdapter {
            
                @Autowired
                private DataSource dataSource;
            
                @Autowired
                private BCryptPasswordEncoder passwordEncoder;
                        
                @Override
                protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            
                   auth.jdbcAuthentication()
                       .dataSource(dataSource) //creates database connection
                       .usersByUsernameQuery("select user_name,user_pwd,user_enabled from user where user_name=?")
                       .authoritiesByUsernameQuery("select user_name,user_role from user where user_name=?")
                       .passwordEncoder(passwordEncoder);
            
                }
            }



    Example#5: Without WebSecurityConfigurerAdapter
    
        Going forward, the new code will look like below, if we want to implement Spring Security Without WebSecurityConfigurerAdapter.
        
            @Configuration
            @EnableWebSecurity
            public class SecurityConfig {
            
               @Autowired 
               private DataSource dataSource;
            
               @Bean
               public UserDetailsManager authenticateUsers() {
            
                  UserDetails user = User.withUsername("username")
                    .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("password"))
                    .build();
                    
                  JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
                  users.setAuthoritiesByUsernameQuery("select user_name,user_pwd,user_enabled from user where user_name=?");
                  users.setUsersByUsernameQuery("select user_name,user_role from user where user_name=?");
                  users.createUser(user);
                  
                  return users;
               }
            }


 */