package com.zura.gymCRM.component.helper;

import com.zura.gymCRM.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper class for setting up authentication in MockMvc tests
 */
@Component
public class MockMvcAuthHelper {

    @Autowired(required = false)
    private JwtService jwtService;

    /**
     * Sets up the security context with a mock user for testing
     * @param username The username
     * @param roles The roles to assign (e.g. "USER", "ADMIN")
     * @return The authentication token
     */
    public Authentication setUpSecurityContext(String username, String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // Create user details
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(authorities)
                .build();

        // Create and set authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    /**
     * Creates a JWT token for the given username and roles
     * @param username The username
     * @param roles The roles to assign
     * @return The JWT token or a mock token if JwtService is not available
     */
    public String createJwtToken(String username, String... roles) {
        if (jwtService == null) {
            // If JwtService is not available, return a mock token
            return "mock-jwt-token-for-" + username;
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // Create user details
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(authorities)
                .build();

        // Generate token
        return jwtService.generateToken(userDetails);
    }

    /**
     * Creates a RequestPostProcessor to add authentication to a MockMvc request
     * @param token The JWT token
     * @return The RequestPostProcessor
     */
    public RequestPostProcessor bearerToken(String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    /**
     * Adds authentication to a MockHttpServletRequestBuilder
     * @param builder The request builder
     * @param username The username
     * @param roles The roles
     * @return The updated builder with authentication
     */
    public MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder, String username, String... roles) {
        String token = createJwtToken(username, roles);
        return builder.header("Authorization", "Bearer " + token);
    }

    /**
     * Clears the security context
     */
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}