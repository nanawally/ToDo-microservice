package com.nanawally.ToDo_microservice.utility.authorization;

import com.nanawally.ToDo_microservice.utility.authorization.jwt.JwtUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Helper class to get the userId
@Component
public class CurrentUser {

    public UUID getUserId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userDetails.getUserId();
    }
}
