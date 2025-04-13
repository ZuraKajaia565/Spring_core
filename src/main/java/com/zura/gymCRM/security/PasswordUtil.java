package com.zura.gymCRM.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Verify PasswordUtil implementation

@Component
public class PasswordUtil {
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String encodePassword(String rawPassword) {
        logger.debug("Encoding password");
        String encoded = passwordEncoder.encode(rawPassword);
        logger.debug("Password encoded successfully");
        return encoded;
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        logger.debug("Checking if passwords match");

        // Check if the encodedPassword looks like BCrypt format
        if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$") && !encodedPassword.startsWith("$2y$")) {
            logger.warn("Encoded password does not look like BCrypt format - trying direct comparison");
            // For legacy passwords or during development/testing
            return rawPassword.equals(encodedPassword);
        }

        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}