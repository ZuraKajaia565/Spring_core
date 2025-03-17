package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.LoginResponse;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.facade.GymFacade;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

  @Autowired GymFacade gymFacade;

  @Operation(summary="Login", method = "Login", description = "Login")
  @GetMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestParam String username,
                                             @RequestParam String password) {

    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);
    if (trainee.isPresent() && trainer.isPresent()) {
      return ResponseEntity.status(401).body(
              new LoginResponse("Unknown", "Invalid username or password."));
    }
    if (trainee.isPresent() &&
        trainee.get().getUser().getPassword().equals(password)) {
      return ResponseEntity.ok(
          new LoginResponse("Trainee", "Login successful!"));
    }

    else if (trainer.isPresent() &&
             trainer.get().getUser().getPassword().equals(password)) {
      return ResponseEntity.ok(
          new LoginResponse("Trainer", "Login successful!"));
    }

    else {
      return ResponseEntity.status(401).body(
          new LoginResponse("Unknown", "Invalid username or password."));
    }
  }

  @Operation(summary = "change password", method = "Change Login", description = "Change Login credentials")
  @PutMapping("/changePassword")
  public ResponseEntity<LoginResponse>
  changePassword(@RequestParam String username,
                 @RequestParam String oldPassword,
                 @RequestParam String newPassword) {

    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);

    if (trainee.isPresent()) {
      if (trainee.get().getUser().getPassword().equals(oldPassword)) {
        gymFacade.changeTraineePassword(username, newPassword);
        return ResponseEntity.ok(
            new LoginResponse("Trainee", "Password changed successfully!"));
      } else {
        return ResponseEntity.status(401).body(
            new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }

    else if (trainer.isPresent()) {
      if (trainer.get().getUser().getPassword().equals(oldPassword)) {
        gymFacade.changeTrainerPassword(username, newPassword);
        return ResponseEntity.ok(
            new LoginResponse("Trainer", "Password changed successfully!"));
      } else {
        return ResponseEntity.status(401).body(
            new LoginResponse("Unknown", "Old password is incorrect."));
      }
    }

    else {
      return ResponseEntity.status(404).body(
          new LoginResponse("Unknown", "User not found."));
    }
  }
}
