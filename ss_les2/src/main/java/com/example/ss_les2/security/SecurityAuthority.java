package com.example.ss_les2.security;

import com.example.ss_les2.entities.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class SecurityAuthority implements GrantedAuthority {

    private final Authority authority;
    
    @Override
    public String getAuthority() {
        return authority.getName();
    }
}
