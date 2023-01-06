package com.example.ss_les2.security;

import com.example.ss_les2.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
є кілька способів щодо імплементації UserDetails
    1) імплементувати у самому Ентіті = що є не добре згідно 1 принципу SOLID, 
        бо буде у цього ентіні вже 2 відповідальності
        
    2) використовувати мапер, що є прийнятним , але менш зручним
    
    3) використати патерн декоратор
        для цього створюємо SecurityUser, який буде імплементувати UserDetails
        і як депенденсі добавимо наш User
        
        тепер у потрібних методах просто делегуємо методи з User
    
        
 */
@RequiredArgsConstructor
public class SecurityUser implements UserDetails {

    private final User user;

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map(SecurityAuthority::new)
                .toList();
    }
    
    /*
        наступні методи спрінг використовує для додаткових перевірок, таких як:
            - чи Expired / Locked / Enabled
            
        на даному етапі поки поставимо TRUE    
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

/*

Granted Authority vs Role in Spring Security


1. Granted Authority

    Простіше кажучи, надані повноваження в безпеці Spring — це « дозвіл » або « право », надане ролі. 
    Деякі приклади наданих повноважень можуть бути
            READ_AUTHORITY
            WRITE_AUTHORITY
            UPDATE_AUTHORITY
            DELETE_AUTHORITY


    Spring security надає можливість використовувати ці повноваження за допомогою таких виразів, 
    як hasAuthority("DELETE_AUTHORITY").
    Найпоширеніший спосіб надання наданих повноважень користувачеві шляхом впровадження користувацьких 
    UserDetailsService , які створюють і повертають GrantedAuthorities для нашої програми. Ось стандартний 
    User об’єкт, який повертає Spring безпеки, включаючи список GrantedAuthorities.

        public User(String username, 
                    String password, 
                    boolean enabled, 
                    boolean accountNonExpired,
                    boolean credentialsNonExpired, 
                    boolean accountNonLocked, 
                    Collection<? extends GrantedAuthority> authorities)
                    
                    
                    
     Об’єкти GrantedAutority – це дозволи на рівні програми, а не обмеження для об’єктів домену. 
     Тому ми не можемо використовувати GrantedAuthority для представлення дозволів працівнику чи клієнту. 
     Для таких ситуацій ми будемо використовувати Ролі, які більш узгоджені для визначення таких випадків використання.
 
 
 
2. Roles in Spring Security

    Ролі можна розглядати як GrantedAuthorities, представлені у вигляді рядка з префіксом « ROLE» . 
    Ми можемо використовувати роль у Spring за допомогою hasRole("CUSTOMER"). 
    Для кількох простих програм ви можете розглядати ролі як надані повноваження. 
            ROLE_ADMIN
            ROLE_MANAGER
            ROLE_USER



3. Spring Security Roles as Container

    Ми також можемо використовувати ролі як контейнер для повноважень або привілеїв. 
    Цей підхід забезпечує гнучкість зіставлення ролей на основі бізнес-правил. 
            
            Користувач із ROLE_ADMINроллю має повноваження на READ, DELETE, WRITE, UPDATE.
            Користувач із роллю ROLE_USERмає повноваження READлише на.
            Користувач ROLE_MANAGERможе виконувати операції READ, WRITEі UPDATE.


4. Using Granted Authority vs Role in Spring Security

    Spring security використовує hasRole()і hasAuthority()взаємозамінно. 
        > Always add the ROLE_ while using the hasAuthority() method (e.g hasAuthority("ROLE_CUSTOMER")).
        
        > While using hasRole(), do not add the ROLE_ prefix as it will be added automatically by Spring security 
            (hasRole("CUSTOMER")).
                            
         */