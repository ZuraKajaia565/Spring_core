package com.zura.gymCRM.controller;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/training")
public class TrainingController {

  private final GymFacade gymFacade;

  public TrainingController(GymFacade gymFacade) { this.gymFacade = gymFacade; }

  @PostMapping(value = "", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method="Add Training",
          summary = "Add Training",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  addTraining(@RequestParam String traineeUsername,
              @RequestParam String trainerUsername,
              @RequestParam String trainingName,
              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date trainingDate,
              @RequestParam int trainingDuration) {

    Optional<Trainee> trainee =
            gymFacade.selectTraineeByusername(traineeUsername);
    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    Optional<Trainer> trainer =
            gymFacade.selectTrainerByUsername(trainerUsername);
    if (trainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainer not found");
    }

    try {
      Training newTraining = new Training();
      newTraining.setTrainee(trainee.get());
      newTraining.setTrainer(trainer.get());
      newTraining.setTrainingName(trainingName);
      newTraining.setTrainingDate(trainingDate);
      newTraining.setTrainingDuration(trainingDuration);

      gymFacade.addTraining(trainee.get(), trainer.get(), trainingName, null,
              trainingDate, trainingDuration);

      return ResponseEntity.ok("Training added successfully");

    } catch (Exception e) {
      return ResponseEntity.status(500).body(
              "Error occurred while adding training: " + e.getMessage());
    }
  }
}