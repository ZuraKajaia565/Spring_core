package com.zura.gymCRM.controller;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/training")
public class TrainingController {

  private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);

  private final GymFacade gymFacade;
  private final TrainingService trainingService;

  @Autowired
  public TrainingController(GymFacade gymFacade, TrainingService trainingService) {
    this.gymFacade = gymFacade;
    this.trainingService = trainingService;
  }

  @PostMapping(value = "", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method = "Add Training",
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

    logger.info("Adding training: trainee={}, trainer={}, name={}",
            traineeUsername, trainerUsername, trainingName);

    try {
      Optional<Trainee> trainee = gymFacade.selectTraineeByusername(traineeUsername);
      if (trainee.isEmpty()) {
        return ResponseEntity.badRequest().body("Trainee not found");
      }

      Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(trainerUsername);
      if (trainer.isEmpty()) {
        return ResponseEntity.badRequest().body("Trainer not found");
      }

      Training newTraining = gymFacade.addTraining(
              trainee.get(),
              trainer.get(),
              trainingName,
              null,
              trainingDate,
              trainingDuration
      );

      logger.info("Training added successfully: ID={}", newTraining.getId());

      return ResponseEntity.ok("Training added successfully");
    } catch (Exception e) {
      logger.error("Error occurred while adding training: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(
              "Error occurred while adding training: " + e.getMessage());
    }
  }

  @PutMapping(value = "/{id}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method = "Update Training",
          summary = "Update Training",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  updateTraining(@PathVariable Long id,
                 @RequestParam String traineeUsername,
                 @RequestParam String trainerUsername,
                 @RequestParam String trainingName,
                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date trainingDate,
                 @RequestParam int trainingDuration) {

    logger.info("Updating training: ID={}", id);

    try {
      Optional<Training> existingTraining = trainingService.getTraining(id);
      if (existingTraining.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      Optional<Trainee> trainee = gymFacade.selectTraineeByusername(traineeUsername);
      if (trainee.isEmpty()) {
        return ResponseEntity.badRequest().body("Trainee not found");
      }

      Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(trainerUsername);
      if (trainer.isEmpty()) {
        return ResponseEntity.badRequest().body("Trainer not found");
      }

      Training training = existingTraining.get();
      training.setTrainee(trainee.get());
      training.setTrainer(trainer.get());
      training.setTrainingName(trainingName);
      training.setTrainingDate(trainingDate);
      training.setTrainingDuration(trainingDuration);

      trainingService.updateTraining(training);

      logger.info("Training updated successfully: ID={}", id);

      return ResponseEntity.ok("Training updated successfully");
    } catch (NotFoundException e) {
      logger.error("Training not found: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      logger.error("Error occurred while updating training: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(
              "Error occurred while updating training: " + e.getMessage());
    }
  }

  @DeleteMapping(value = "/{id}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method = "Delete Training",
          summary = "Delete Training",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  deleteTraining(@PathVariable Long id) {

    logger.info("Deleting training: ID={}", id);

    try {
      trainingService.deleteTraining(id);

      logger.info("Training deleted successfully: ID={}", id);

      return ResponseEntity.ok("Training deleted successfully");
    } catch (NotFoundException e) {
      logger.error("Training not found: {}", e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      logger.error("Error occurred while deleting training: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(
              "Error occurred while deleting training: " + e.getMessage());
    }
  }
}