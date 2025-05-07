package com.zura.gymCRM.security;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    public static final int MAX_ATTEMPT = 3;
    private final Map<String, LoginAttempt> attemptsMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void initialize() {
        // Run cleanup every minute to remove expired entries
        scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    public void loginSucceeded(String key) {
        attemptsMap.remove(key);
    }

    public void loginFailed(String key) {
        LoginAttempt attempt = attemptsMap.getOrDefault(key, new LoginAttempt());
        attempt.incrementCount();
        attempt.setTimestamp(LocalDateTime.now());
        attemptsMap.put(key, attempt);
    }

    public boolean isBlocked(String key) {
        LoginAttempt attempt = attemptsMap.get(key);
        if (attempt == null) {
            return false;
        }

        // Check if the block has expired (5 minutes)
        if (attempt.getCount() >= MAX_ATTEMPT) {
            LocalDateTime blockTime = attempt.getTimestamp();
            LocalDateTime now = LocalDateTime.now();
            if (blockTime.plusMinutes(5).isAfter(now)) {
                return true;
            } else {
                // If block expired, remove the entry
                attemptsMap.remove(key);
                return false;
            }
        }

        return false;
    }

    private void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        attemptsMap.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            // Remove entries older than 5 minutes
            return attempt.getTimestamp().plusMinutes(5).isBefore(now);
        });
    }

    // Helper class to store attempt count and timestamp
    private static class LoginAttempt {
        private int count;
        @Setter
        private LocalDateTime timestamp;

        public LoginAttempt() {
            this.count = 0;
            this.timestamp = LocalDateTime.now();
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            this.count++;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }


    }
}