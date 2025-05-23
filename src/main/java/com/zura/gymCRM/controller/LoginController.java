package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.AuthenticationRequest;
import com.zura.gymCRM.dto.AuthenticationResponse;
import com.zura.gymCRM.dto.LoginResponse;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.security.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

  @Autowired
  JwtService jwtService;
  private final AuthenticationService authenticationService;
  private final LoginAttemptService loginAttemptService;
  private final GymFacade gymFacade;
  private final PasswordUtil passwordUtil;

  private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

  private final TokenBlacklistService tokenBlacklistService;

  @Autowired
  public LoginController(
          AuthenticationService authenticationService,
          LoginAttemptService loginAttemptService,
          GymFacade gymFacade,
          PasswordUtil passwordUtil,
          TokenBlacklistService tokenBlacklistService) {
    this.authenticationService = authenticationService;
    this.loginAttemptService = loginAttemptService;
    this.gymFacade = gymFacade;
    this.passwordUtil = passwordUtil;
    this.tokenBlacklistService = tokenBlacklistService;
  }

  @PostMapping("/login")
  @Operation(summary = "User Login", description = "Authenticate a user with username and password in JSON body")
  public ResponseEntity<AuthenticationResponse> login(
          @RequestBody @Valid AuthenticationRequest request,
          HttpServletRequest httpRequest) {
    String clientIp = getClientIP(httpRequest);

    logger.info("Login attempt for user: {} from IP: {}", request.getUsername(), clientIp);

    if (loginAttemptService.isBlocked(clientIp)) {
      logger.warn("IP is blocked due to too many failed attempts: {}", clientIp);
      return ResponseEntity.status(429)
              .body(new AuthenticationResponse(null, "Account locked due to too many failed attempts. Try again in 5 minutes."));
    }

    try {
      // Authenticate and get token
      AuthenticationResponse response = authenticationService.authenticate(request);

      // Log success and return the response with token
      loginAttemptService.loginSucceeded(clientIp);
      logger.info("Login successful for user: {}, token generated", request.getUsername());

      // Return the response which contains the token
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      loginAttemptService.loginFailed(clientIp);
      logger.warn("Login failed for user: {} - {}", request.getUsername(), e.getMessage());
      return ResponseEntity.status(401)
              .body(new AuthenticationResponse(null, "Authentication failed: " + e.getMessage()));
    }
  }

  @GetMapping("/login")
  @Operation(summary = "User Login with URL Parameters", description = "Authenticate a user with username and password as URL parameters")
  public ResponseEntity<AuthenticationResponse> loginWithParams(
          @RequestParam String username,
          @RequestParam String password,
          HttpServletRequest request) {

    String clientIp = getClientIP(request);
    logger.info("Login attempt for user: {} from IP: {}", username, clientIp);

    if (loginAttemptService.isBlocked(clientIp)) {
      logger.warn("IP is blocked due to too many failed attempts: {}", clientIp);
      return ResponseEntity.status(429)
              .body(new AuthenticationResponse(null, "Account locked due to too many failed attempts. Try again in 5 minutes."));
    }

    try {
      // Create authentication request and authenticate
      AuthenticationRequest authRequest = new AuthenticationRequest(username, password);
      AuthenticationResponse response = authenticationService.authenticate(authRequest);

      loginAttemptService.loginSucceeded(clientIp);
      logger.info("Login successful for user: {}", username);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      loginAttemptService.loginFailed(clientIp);
      logger.warn("Login failed for user: {} - {}", username, e.getMessage());
      return ResponseEntity.status(401)
              .body(new AuthenticationResponse(null, "Authentication failed: " + e.getMessage()));
    }
  }

  @PostMapping("/logout")
  @Operation(summary = "User Logout", description = "Invalidate the user's session and JWT token")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
    // Get the JWT token from the Authorization header

    logger.info("zura");
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null) {
      String token = authHeader.substring(7);
      logger.info("Processing logout request for token: {}...", token.substring(0, Math.min(10, token.length())));

      // Blacklist the JWT token2
      tokenBlacklistService.blacklistToken(token);
      logger.info("Token blacklisted successfully. Blacklist size: {}", tokenBlacklistService.getBlacklistSize());
    } else {
      logger.warn("Logout request without valid Authorization header");
    }

    // Invalidate HTTP session
    if (request.getSession(false) != null) {
      request.getSession().invalidate();
      logger.debug("HTTP session invalidated");
    }

    // Clear Spring Security context
    SecurityContextHolder.clearContext();
    logger.debug("Security context cleared");

    // Clear any auth cookies
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("JSESSIONID".equals(cookie.getName()) || "remember-me".equals(cookie.getName())) {
          Cookie newCookie = new Cookie(cookie.getName(), null);
          newCookie.setMaxAge(0);
          newCookie.setPath("/");
          response.addCookie(newCookie);
          logger.debug("Cleared cookie: {}", cookie.getName());
        }
      }
    }

    Map<String, String> responseBody = Map.of(
            "message", "Successfully logged out",
            "status", "success",
            "timestamp", String.valueOf(System.currentTimeMillis())
    );

    logger.info("Logout completed successfully");
    return ResponseEntity.ok(responseBody);
  }

  @PutMapping("/{username}/password")
  @Operation(summary = "Change Password", description = "Change user password with proper authorization")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<LoginResponse> changePassword(
          @PathVariable String username,
          @RequestParam String oldPassword,
          @RequestParam String newPassword,
          HttpServletRequest request) {

    String clientIp = getClientIP(request);
    logger.debug("Password change request for user: {} from IP: {}", username, clientIp);

    // Check if the IP is blocked
    if (loginAttemptService.isBlocked(clientIp)) {
      logger.warn("IP blocked for too many failed attempts: {}", clientIp);
      return ResponseEntity.status(429).body(
              new LoginResponse("Unknown", "Account locked due to too many failed attempts. Try again in 5 minutes."));
    }

    // Get the authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().equals(username)) {
      logger.warn("User {} attempted to change password for {}", authentication.getName(), username);
      return ResponseEntity.status(403).body(
              new LoginResponse("Unknown", "You can only change your own password"));
    }

    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);

    if (trainee.isPresent()) {
      // Use password util to check if the raw oldPassword matches the stored hash
      boolean passwordMatches = passwordUtil.matches(oldPassword, trainee.get().getUser().getPassword());
      logger.debug("Trainee found, password match result: {}", passwordMatches);

      if (passwordMatches) {
        gymFacade.changeTraineePassword(username, newPassword);
        loginAttemptService.loginSucceeded(clientIp);
        logger.info("Password changed successfully for trainee: {}", username);
        return ResponseEntity.ok(
                new LoginResponse("Trainee", "Password changed successfully!"));
      } else {
        loginAttemptService.loginFailed(clientIp);
        logger.warn("Old password verification failed for trainee: {}", username);
        return ResponseEntity.status(401).body(
                new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }
    else if (trainer.isPresent()) {
      // Use password util to check if the raw oldPassword matches the stored hash
      boolean passwordMatches = passwordUtil.matches(oldPassword, trainer.get().getUser().getPassword());
      logger.debug("Trainer found, password match result: {}", passwordMatches);

      if (passwordMatches) {
        gymFacade.changeTrainerPassword(username, newPassword);
        loginAttemptService.loginSucceeded(clientIp);
        logger.info("Password changed successfully for trainer: {}", username);
        return ResponseEntity.ok(
                new LoginResponse("Trainer", "Password changed successfully!"));
      } else {
        loginAttemptService.loginFailed(clientIp);
        logger.warn("Old password verification failed for trainer: {}", username);
        return ResponseEntity.status(401).body(
                new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }
    else {
      logger.warn("User not found: {}", username);
      return ResponseEntity.status(404).body(
              new LoginResponse("Unknown", "User not found."));
    }
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}