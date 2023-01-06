package com.example.ss_les2.services;

import com.example.ss_les2.repositories.UserRepository;
import com.example.ss_les2.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    UserDetailsService має тільки один метод
        public UserDetails loadUserByUsername(String username)
        
    і цей контракт ми реалізуємо за допомогою нашого UserRepository, звідки з БД дістанемо юзера
         
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);

        /*
            userRepository.findByUsername повертає Optional
            так як нам потрібно повернути UserDetails, то перемаплюємо до нашого декоратора SecurityUser,
            і якщо немає юзера, то UsernameNotFoundException
         */
        return user.map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found " + username));
    }
}
