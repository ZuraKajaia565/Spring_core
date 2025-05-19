package com.zura.gymCRM;

import com.zura.gymCRM.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void testBlacklistToken() {
        String token = "testToken";
        tokenBlacklistService.blacklistToken(token);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void testIsBlacklistedForNonExistentToken() {
        String token = "nonExistentToken";

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void testClearBlacklist() {
        String token = "testToken";
        tokenBlacklistService.blacklistToken(token);

        tokenBlacklistService.clearBlacklist();

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }
}