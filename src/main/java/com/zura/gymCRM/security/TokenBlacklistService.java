package com.zura.gymCRM.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    // Use a Set for more efficient lookups
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Add a token to the blacklist
     */
    public void blacklistToken(String token) {
        logger.info("Blacklisting token: {}...", getTokenPreview(token));
        blacklistedTokens.add(token);
        logger.info("Token blacklisted. Current blacklist size: {}", blacklistedTokens.size());
    }

    /**
     * Check if a token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        boolean isBlacklisted = blacklistedTokens.contains(token);
        logger.debug("Token {} is{} blacklisted", getTokenPreview(token), isBlacklisted ? "" : " not");
        return isBlacklisted;
    }

    /**
     * Get a safe preview of the token for logging
     */
    private String getTokenPreview(String token) {
        if (token == null) return "null";
        if (token.length() <= 10) return token;
        return token.substring(0, 10) + "...";
    }

    /**
     * Get current blacklist size (for diagnostics)
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }

    /**
     * Clear all blacklisted tokens (for testing/admin purposes)
     */
    public void clearBlacklist() {
        logger.warn("Clearing entire token blacklist with {} entries", blacklistedTokens.size());
        blacklistedTokens.clear();
    }
}