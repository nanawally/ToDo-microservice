package com.nanawally.ToDo_microservice.utility.authorization.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
public class JwtUserDetails implements UserDetails {

    private final String username;
    private final Set<? extends GrantedAuthority> authorities;
    private final UUID userId;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Use UUID instead of String (-> JwtAuthFilter)
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
