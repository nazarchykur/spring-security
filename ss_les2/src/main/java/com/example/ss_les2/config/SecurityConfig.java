package com.example.ss_les2.config;

import com.example.ss_les2.repositories.UserRepository;
import com.example.ss_les2.services.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
/*
     краще просто додати анотацію @Service над JpaUserDetailsService
     
     можна тут створити бін UserDetailsService, але він зобов'язує мати депенденсі,
     що трохи ускладнить цей клас
     
         private final UserRepository userRepository;
     
            Bean
            public UserDetailsService userDetailsService() {
                return new JpaUserDetailsService(userRepository); 
            }
            
 */
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // !!! не використовувати на прод, тільки для розробки і демонстрації
    }
}

/*
    https://howtodoinjava.com/spring-security/inmemory-jdbc-userdetails-service/
    
    !!! Зауважте, що InMemoryUserDetailsManager не призначено для готових додатків . 
        Використовуйте цей клас лише для прикладів.
    
    UserDetailsService — це основний інтерфейс у Spring Security framework, який використовується для отримання 
        інформації про автентифікацію та авторизацію користувача.
    
    Цей інтерфейс має лише один названий метод, loadUserByUsername() який ми можемо реалізувати для передачі інформації 
    про клієнта API безпеки Spring. Ця DaoAuthenticationProvider інформація використовуватиметься для завантаження 
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