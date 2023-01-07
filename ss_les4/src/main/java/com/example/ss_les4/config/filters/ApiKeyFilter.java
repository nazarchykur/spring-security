package com.example.ss_les4.config.filters;

import com.example.ss_les4.config.authentications.ApiKeyAuthentication;
import com.example.ss_les4.config.managers.CustomAuthenticationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String key;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        CustomAuthenticationManager manager = new CustomAuthenticationManager(key);

        String headerKey = request.getHeader("x-api-key");

        if ("null".equals(headerKey) || headerKey == null) {
            filterChain.doFilter(request, response);
        }
        
        ApiKeyAuthentication auth = new ApiKeyAuthentication(headerKey);
        
        try {

            Authentication authentication = manager.authenticate(auth);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);
    }
}

/*
    OncePerRequestFilter
    
        Filter base class that aims to guarantee a single execution per request dispatch, 
        on any servlet container. 

 */