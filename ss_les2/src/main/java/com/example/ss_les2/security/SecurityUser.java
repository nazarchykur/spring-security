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
    
    // поки пропустимо це до наступного уроку
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
        return List.of(() -> "read"); // поки додамо права для читання
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
