package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.AuthenticationRequest;
import com.zura.gymCRM.dto.AuthenticationResponse;
import com.zura.gymCRM.security.AuthenticationService;
import com.zura.gymCRM.security.LoginAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(AuthenticationService authenticationService, LoginAttemptService loginAttemptService) {
        this.authenticationService = authenticationService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate a user and receive a JWT token")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIP(httpRequest);

        if (loginAttemptService.isBlocked(clientIp)) {
            return ResponseEntity.status(429)
                    .body(new AuthenticationResponse(null, "Account locked due to too many failed attempts. Try again in 5 minutes."));
        }

        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            loginAttemptService.loginSucceeded(clientIp);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            loginAttemptService.loginFailed(clientIp);
            return ResponseEntity.status(401)
                    .body(new AuthenticationResponse(null, "Authentication failed: " + e.getMessage()));
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }


    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Invalidate the user's session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate session if it exists
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }

        // Perform logout
        SecurityContextHolder.clearContext();

        // Return success message
        return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    }

}