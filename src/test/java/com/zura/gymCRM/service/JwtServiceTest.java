package com.zura.gymCRM.service;

import com.zura.gymCRM.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = "7dJpxYxnzOdZsrDZrGhK3uQtyGdOJWvdMLXF8YRpfM3g6s9olJ";
        jwtService.jwtExpiration = 86400000; // 24 hours in milliseconds
    }

    @Test
    void testGenerateToken() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 10); // Simplistic check for token length
    }

    @Test
    void testExtractUsername() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testUser", username);
    }

    @Test
    void testIsTokenValid() {
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
}