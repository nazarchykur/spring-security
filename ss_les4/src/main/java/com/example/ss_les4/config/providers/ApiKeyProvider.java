package com.example.ss_les4.config.providers;

import com.example.ss_les4.config.authentications.ApiKeyAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class ApiKeyProvider implements AuthenticationProvider {
    private final String key;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiKeyAuthentication auth = (ApiKeyAuthentication) authentication;

        if (key.equals(auth.getKey())) {
            auth.setAuthenticated(true);
            return auth;
        }
        throw new BadCredentialsException("Bad Credentials Exception");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.equals(authentication);
    }
}
