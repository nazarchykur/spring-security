package com.example.ss_les1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig {

    /*
    https://howtodoinjava.com/spring-security/inmemory-jdbc-userdetails-service/
    
    !!! Зауважте, що InMemoryUserDetailsManager не призначено для готових додатків . 
        Використовуйте цей клас лише для прикладів.
    
    UserDetailsService — це основний інтерфейс у Spring Security framework, який використовується для отримання 
        інформації про автентифікацію та авторизацію користувача.
    
    Цей інтерфейс має лише один названий метод, loadUserByUsername()який ми можемо реалізувати для передачі інформації 
    про клієнта API безпеки Spring. Ця DaoAuthenticationProviderінформація використовуватиметься для завантаження 
    інформації про користувача під час процесу автентифікації.
    
    
    
  Стандартна реалізація AuthenticationProvider використовує стандартні реалізації, надані для UserDetailsService та PasswordEncoder .
    
        Стандартна реалізація UserDetailsService лише реєструє облікові дані за замовчуванням у пам’яті програми.
            login: user
            password:  випадково згенерованим,  який записується на консоль програми під час завантаження контексту spring.
                    Using generated security password: 78nh23h-sd56-4b98-86ef-dfas8f8asf8
        
        Зауважте, що служба UserDetailsService завжди пов’язана з PasswordEncoder об’єктом, який кодує наданий пароль 
        і перевіряє, чи відповідає пароль існуючому кодуванню. Коли ми замінюємо стандартну реалізацію UserDetailsService,
        ми також повинні вказати PasswordEncoder .
        
        
  UserDetailsService в пам’яті
        Першим простим прикладом перевизначення UserDetailsService є InMemoryUserDetailsManager . 
        Цей клас зберігає облікові дані в пам’яті , які потім можуть використовуватися Spring Security для автентифікації 
        вхідного запиту.
    
    
    Наведена нижче конфігурація реєструє bean-компоненти типу UserDetailsService та PasswordEncoder у контексті spring, 
    і постачальник автентифікації використовує їх автоматично. Spring дозволяє нам налаштувати службу користувача та 
    кодувальник паролів безпосередньо для менеджера автентифікації, якщо ми бажаємо це зробити.
    
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var userDetailsService = new InMemoryUserDetailsManager();
        var user = User.withUsername("user")
                .password("pass")
                .authorities("USER_ROLE")
                .build();

        userDetailsService.createUser(user);

        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

/*
    !!! Зауважте, що InMemoryUserDetailsManager не призначено для готових додатків . 
        Використовуйте цей клас лише для прикладів.
        
Попередню конфігурацію можна переписати наступним чином:

@Configuration
class AppConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws  Exception {

    var userDetailsService = new InMemoryUserDetailsManager();

    var user = User.withUsername("user")
        .password("pass")
        .authorities("USER_ROLE")
        .build();

    userDetailsService.createUser(user);

    auth.userDetailsService(userDetailsService)
        .passwordEncoder(NoOpPasswordEncoder.getInstance());
  }
}
 */

/*
База даних із підтримкою UserDetailsService

    Щоб зберігати та отримувати ім’я користувача та паролі з бази даних SQL, ми використовуємо JdbcUserDetailsManager клас. 
    Він підключається до бази даних безпосередньо через JDBC .
    
    За замовчуванням він створює дві таблиці в базі даних:
        USERS
        AUTHORITIES
        
        
        
   Файл схеми за замовчуванням знаходиться у файлі users.ddl. Розташування файлу вказано в константі 
   JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION.

    Щоб налаштувати схему за замовчуванням і створити початкових користувачів, ми можемо написати власні файли та 
    розмістити їх у папці програми schema.sql/ src /main/resources . Spring Boot автоматично запускає ці файли, коли 
    ми запускаємо програму.data.sql
    
    Зверніть увагу, що JdbcUserDetailsManager потребує DataSource для підключення до бази даних, тому нам також потрібно 
    визначити його.
    
    
            @EnableWebSecurity
            public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
            
              @Bean
              public DataSource dataSource() {
                return new EmbeddedDatabaseBuilder()
                  .setType(EmbeddedDatabaseType.H2)
                  .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                  .build();
              }
            
              @Bean
              public UserDetailsService jdbcUserDetailsService(DataSource dataSource) {
            
                UserDetails user = User
                  .withUsername("user")
                  .password("password")
                  .roles("USER_ROLE")
                  .build();
            
                JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
                users.createUser(user);
                return users;
              }
            
              @Bean
              public PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
              }
            }
 */