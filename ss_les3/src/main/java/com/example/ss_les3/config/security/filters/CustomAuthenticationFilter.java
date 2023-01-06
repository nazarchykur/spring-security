package com.example.ss_les3.config.security.filters;

import com.example.ss_les3.config.security.authentication.CustomAuthentication;
import com.example.ss_les3.config.security.managers.CustomAuthenticationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final CustomAuthenticationManager customAuthenticationManager;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        /*
            1) create an authentication object which is not yet authenticated
            2) delegate the authentication object to the manager
            3) get back the authentication from the manager
            4) if the object is authenticated then send request to the next filter in the chain
            
         */

        // 1.
        String key = String.valueOf(request.getHeader("key"));
        CustomAuthentication customAuthentication = new CustomAuthentication(false, key);

        // 2. call Manager and 3. get Authentication 
        Authentication authentication = customAuthenticationManager.authenticate(customAuthentication);
        
        // 4. 
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response); // only when authentication worked
        }

    }
}

/*
    SecurityContextHolder.getContext().setAuthentication(authentication);  - важливий код
    коли ми зберігаємо десь  Authentication 
    і пізніше Authorization механізм зможе перевірити ХТО аутентифікований 
    
    і на основі, хто аутентифікований і їх привілеї (privileges), буде надано права до доступу до АП
 */