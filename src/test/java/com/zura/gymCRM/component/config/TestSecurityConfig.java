package com.zura.gymCRM.component.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    /**
     * Custom security filter chain for testing that permits all requests
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF protection and permit all requests for testing
        http
                .authorizeRequests()
                .anyRequest().permitAll(); // Allow all requests without authentication for testing

        return http.build();
    }

    /**
     * Configure web security to ignore all requests
     * This is helpful for testing to bypass security completely
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().anyRequest();
    }

    
}