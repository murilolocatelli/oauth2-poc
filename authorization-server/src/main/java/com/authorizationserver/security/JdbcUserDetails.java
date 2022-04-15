package com.authorizationserver.security;

import com.authorizationserver.model.Credential;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JdbcUserDetails implements UserDetails {

    private static final long serialVersionUID = -2438970599055109523L;

    private Credential credential;

    public JdbcUserDetails(final Credential credential) {
        this.credential = credential;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();

        if (this.credential.getAuthorities() == null) {
            return null;
        }

        this.credential.getAuthorities().forEach(t ->
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(t.getDescription())));

        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return credential.getPassword();
    }

    @Override
    public String getUsername() {
        return credential.getEmail();
    }

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
