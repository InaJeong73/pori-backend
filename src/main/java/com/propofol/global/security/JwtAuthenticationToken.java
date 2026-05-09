package com.propofol.global.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthPrincipal principal;

    public JwtAuthenticationToken(AuthPrincipal principal) {
        super(List.of(new SimpleGrantedAuthority(principal.role().name())));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public AuthPrincipal getPrincipal() {
        return principal;
    }
}
