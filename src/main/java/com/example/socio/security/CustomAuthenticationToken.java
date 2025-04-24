package com.example.socio.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Long userId;
    private final String email;

    public CustomAuthenticationToken(UserDetails principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Long userId, String email) {
        super(principal, credentials, authorities);
        this.userId = userId;
        this.email = email;
    }

}