package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.AuthenticationRequest;
import com.zura.gymCRM.dto.AuthenticationResponse;
import com.zura.gymCRM.dto.LoginResponse;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.security.AuthenticationService;
import com.zura.gymCRM.security.LoginAttemptService;
import com.zura.gymCRM.security.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

  @Autowired
  GymFacade gymFacade;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private LoginAttemptService loginAttemptService;

  @Autowired
  private PasswordUtil passwordUtil;

  @Operation(summary="Login", method = "Login", description = "Login")
  @GetMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
          @RequestParam String username,
          @RequestParam String password,
          HttpServletRequest request) {

    String clientIp = getClientIP(request);

    // Check if the IP is blocked due to too many failed attempts
    if (loginAttemptService.isBlocked(clientIp)) {
      return ResponseEntity.status(429)
              .body(new AuthenticationResponse(null, "Account locked due to too many failed attempts. Try again in 5 minutes."));
    }

    try {
      // Use our authentication service to handle login
      AuthenticationRequest authRequest = new AuthenticationRequest(username, password);
      AuthenticationResponse response = authenticationService.authenticate(authRequest);

      // If authentication successful, record success and return token
      loginAttemptService.loginSucceeded(clientIp);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      // If authentication fails, record failure and return error
      loginAttemptService.loginFailed(clientIp);
      return ResponseEntity.status(401)
              .body(new AuthenticationResponse(null, "Invalid username or password."));
    }
  }

  @Operation(summary = "change password", method = "Change Login", description = "Change Login credentials")
  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{username}/password")
  public ResponseEntity<LoginResponse>
  changePassword(@PathVariable String username,
                 @RequestParam String oldPassword,
                 @RequestParam String newPassword,
                 HttpServletRequest request) {

    String clientIp = getClientIP(request);

    // Check if the IP is blocked
    if (loginAttemptService.isBlocked(clientIp)) {
      return ResponseEntity.status(429).body(
              new LoginResponse("Unknown", "Account locked due to too many failed attempts. Try again in 5 minutes."));
    }

    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);

    if (trainee.isPresent()) {
      // Use password util to check if the raw oldPassword matches the stored hash
      if (passwordUtil.matches(oldPassword, trainee.get().getUser().getPassword())) {
        gymFacade.changeTraineePassword(username, newPassword);
        loginAttemptService.loginSucceeded(clientIp);
        return ResponseEntity.ok(
                new LoginResponse("Trainee", "Password changed successfully!"));
      } else {
        loginAttemptService.loginFailed(clientIp);
        return ResponseEntity.status(401).body(
                new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }

    else if (trainer.isPresent()) {
      // Use password util to check if the raw oldPassword matches the stored hash
      if (passwordUtil.matches(oldPassword, trainer.get().getUser().getPassword())) {
        gymFacade.changeTrainerPassword(username, newPassword);
        loginAttemptService.loginSucceeded(clientIp);
        return ResponseEntity.ok(
                new LoginResponse("Trainer", "Password changed successfully!"));
      } else {
        loginAttemptService.loginFailed(clientIp);
        return ResponseEntity.status(401).body(
                new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }

    else {
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