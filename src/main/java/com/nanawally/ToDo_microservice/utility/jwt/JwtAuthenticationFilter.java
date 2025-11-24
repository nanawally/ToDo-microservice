package com.nanawally.ToDo_microservice.utility.jwt;

/*import com.nanawally.ToDo_microservice.user.CustomUserDetailsService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
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
        log.debug(token);
        System.out.println(token + " DEBUGGING");

        if (token == null) {
            token = jwtUtils.extractJwtFromRequest(request);
        }

        if (token == null) {
            log.debug("No JWT token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("JWT token found: {}", token);

        // Validate token
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUsernameFromJwtToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Live DB lookup (ensures user still exists / is enabled)
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username); // ENTITY

                // Possibility to check for other userDetails booleans
                if (userDetails != null && userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Update Spring with possible new change
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Authenticated (DB verified) user '{}'", username);
                } else {
                    log.warn("User '{}' not found or disabled", username);
                }
            }
        } else {
            log.warn("Invalid JWT token");
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
        log.debug("---- JwtAuthenticationFilter END ----");
    }

}*/
