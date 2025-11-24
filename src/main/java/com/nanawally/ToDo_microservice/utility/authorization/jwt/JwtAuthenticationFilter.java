package com.nanawally.ToDo_microservice.utility.authorization.jwt;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.debug("---- JwtAuthenticationFilter START ----");

        // Extract token
        String token = jwtUtils.extractJwtFromCookie(request);

        if (token == null) {
            token = jwtUtils.extractJwtFromRequest(request);
        }

        if (token == null) {
            log.debug("No JWT token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtils.validateJwtToken(token)) {
            log.debug("Invalid JWT token");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JWT token found: {}", token);

        String username = jwtUtils.getUsernameFromJwtToken(token);

        Set<String> roles =  jwtUtils.getAuthoritiesFromJwtToken(token);

        if (username == null){
            log.warn("No username found in request");
            filterChain.doFilter(request, response);
            return;
        }

        var grantedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        JwtUserDetails userDetails = new JwtUserDetails(
                username,
                grantedAuthorities
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Authenticated user '{}' with roles '{}'", username, roles);

        filterChain.doFilter(request, response);
        log.debug("---- JwtAuthenticationFilter END ----");
    }
}
