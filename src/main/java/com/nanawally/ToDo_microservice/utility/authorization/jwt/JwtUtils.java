package com.nanawally.ToDo_microservice.utility.authorization.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    private final String base64EncodedSecretKey = "U2VjdXJlQXBpX1NlY3JldEtleV9mb3JfSFMyNTYwX3NlY3JldF9wcm9qZWN0X2tleV9leGFtcGxl";
    private final byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
    private final SecretKey key = Keys.hmacShaKeyFor(keyBytes); // HMAC algorithm

    // JWT expiration time (1 hour)
    private final int jwtExpirationMs = (int) TimeUnit.HOURS.toMillis(1);   // TODO - Check Expiration

    public String getUsernameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.getSubject();
            log.debug("Extracted username '{}' from JWT token", username);
            return username;

        } catch (Exception e) {
            log.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public Set<String> getAuthoritiesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> authorityList = claims.get("authorities", List.class);
        return new HashSet<>(authorityList);
    }

    public String getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.get("user_id", String.class);
        return userId;
    }

    /*public Set<UserRole> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<?> authoritiesClaim = claims.get("authorities", List.class);

        if (authoritiesClaim == null || authoritiesClaim.isEmpty()) {
            log.warn("No authorities found in JWT token");
            return Set.of();
        }

        // Convert each string like "ROLE_USER" -> UserRole.USER
        Set<UserRole> roles = authoritiesClaim.stream()
                .filter(String.class::isInstance) // keep only strings
                .map(String.class::cast)
                .map(role -> role.replace("ROLE_", "")) // remove prefix if necessary
                .map(String::toUpperCase)
                .map(UserRole::valueOf) // map to your enum
                .collect(Collectors.toSet());

        log.debug("Extracted roles from JWT token: {}", roles);
        return roles;
    }*/

    // Used to pass in JWT token for Validation
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);

            log.debug("JWT validation succeeded");
            return true;

        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
        }

        return false;
    }

    // Helper: Extract JWT from cookie
    public String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("authToken".equals(cookie.getName())) {     // Cookie should be named authToken
                return cookie.getValue();
            }
        }
        return null;
    }

    // Helper: Extract JWT from Authorization header
    public String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
