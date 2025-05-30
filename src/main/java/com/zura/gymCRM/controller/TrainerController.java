package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.TraineeInfo;
import com.zura.gymCRM.dto.TraineeTrainingInfo;
import com.zura.gymCRM.dto.TrainerProfileResponse;
import com.zura.gymCRM.dto.TrainerRegistrationRequest;
import com.zura.gymCRM.dto.TrainerRegistrationResponse;
import com.zura.gymCRM.dto.UpdateTrainerRequest;
import com.zura.gymCRM.dto.UpdateTrainerResponse;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@Validated
public class TrainerController {

  private final GymFacade gymFacade;

  public TrainerController(GymFacade gymFacade) { this.gymFacade = gymFacade; }

  @PostMapping("")
  @Operation(summary = "Trainer Registration", description = "Trainer Registration - Public endpoint, no authentication required")
  public ResponseEntity<?> registerTrainer(@RequestBody @Valid TrainerRegistrationRequest trainerDTO) {
    try {
      // Validate specialization exists FIRST before creating any user
      if (trainerDTO.getSpecialization() == null || trainerDTO.getSpecialization().getId() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid specialization: Specialization is required");
      }

      // Look up the specialization by ID - do this before any user creation
      Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(
              trainerDTO.getSpecialization().getId());

      if (trainingType.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid specialization: TrainingType not found");
      }

      // Verify the name matches what was provided
      if (!trainingType.get().getTrainingTypeName().equals(
              trainerDTO.getSpecialization().getTrainingTypeName())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid specialization: TrainingType name mismatch");
      }

      // Now that we've validated the specialization, proceed with trainer creation
      Trainer createdTrainer = gymFacade.addTrainer(
              trainerDTO.getFirstName(), trainerDTO.getLastName(), true,
              trainingType.get());  // Use the validated training type

      return ResponseEntity.status(HttpStatus.CREATED)
              .body(new TrainerRegistrationResponse(
                      createdTrainer.getUser().getUsername(),
                      createdTrainer.getUser().getPassword()));
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  @GetMapping(value = "/{username}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method="Get Trainer Profile",
          summary = "Get Trainer Profile",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  getTrainerProfile(@PathVariable String username) {
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);

    if (trainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainer not found");
    }

    List<TraineeInfo> trainees =
            gymFacade.getTraineesListofTrainer(trainer.get())
                    .stream()
                    .map(trainee
                            -> new TraineeInfo(trainee.getUser().getUsername(),
                            trainee.getUser().getFirstName(),
                            trainee.getUser().getLastName()))
                    .collect(Collectors.toList());

    TrainerProfileResponse response = new TrainerProfileResponse(
            trainer.get().getUser().getFirstName(),
            trainer.get().getUser().getLastName(),
            trainer.get().getSpecialization().getTrainingTypeName(),
            trainer.get().getUser().getIsActive(), trainees);

    return ResponseEntity.ok(response);
  }

  @PutMapping(
          value = "/{username}",
          consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
          produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method="Update Trainer Profile",
          summary = "Update Trainer Profile",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  updateTrainerProfile(@PathVariable String username, @Valid @RequestBody UpdateTrainerRequest updateTrainerRequest) {
    Optional<Trainer> existingTrainer =
            gymFacade.selectTrainerByUsername(username);

    if (existingTrainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainer not found");
    }

    Trainer trainerToUpdate = existingTrainer.get();
    trainerToUpdate.getUser().setFirstName(updateTrainerRequest.getFirstName());
    trainerToUpdate.getUser().setLastName(updateTrainerRequest.getLastName());
    trainerToUpdate.getUser().setIsActive(updateTrainerRequest.getIsActive());

    gymFacade.updateTrainer(trainerToUpdate);

    List<TraineeInfo> trainees =
            gymFacade.getTraineesListofTrainer(trainerToUpdate)
                    .stream()
                    .map(trainee
                            -> new TraineeInfo(trainee.getUser().getUsername(),
                            trainee.getUser().getFirstName(),
                            trainee.getUser().getLastName()))
                    .collect(Collectors.toList());

    UpdateTrainerResponse response = new UpdateTrainerResponse(
            trainerToUpdate.getUser().getUsername(),
            trainerToUpdate.getUser().getFirstName(),
            trainerToUpdate.getUser().getLastName(),
            trainerToUpdate.getSpecialization().getTrainingTypeName(),
            trainerToUpdate.getUser().getIsActive(), trainees);

    return ResponseEntity.ok(response);
  }

  @GetMapping(
          value = "/{username}/trainings",
          produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(
          method="Get Trainer Trainings List",
          summary = "Get Trainer Trainings List",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?>
  getTrainerTrainingsList(@PathVariable String username,
                          @RequestParam(required = false) Date periodFrom,
                          @RequestParam(required = false) Date periodTo,
                          @RequestParam(required = false) String traineeName) {

    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);
    if (trainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainer not found");
    }

    List<Training> trainings = gymFacade.getTrainerTrainingsByCriteria(
            trainer.get().getUser().getUsername(), periodFrom, periodTo,
            traineeName);

    List<TraineeTrainingInfo> trainingInfos =
            trainings.stream()
                    .map(training
                            -> new TraineeTrainingInfo(
                            training.getTrainingName(), training.getTrainingDate(),
                            training.getTrainingType().getTrainingTypeName(),
                            training.getTrainingDuration(),
                            training.getTrainer().getUser().getUsername()))
                    .collect(Collectors.toList());

    return ResponseEntity.ok(trainingInfos);
  }

  @PatchMapping("/{username}/status")
  @Operation(
          summary = "Update trainer active status",
          security = @SecurityRequirement(name = "Bearer Authentication")
  )
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> updateTrainerStatus(
          @PathVariable String username,
          @Valid @RequestBody Map<String, Boolean> statusUpdate) {

    try {
      boolean isActive = statusUpdate.get("active");
      Trainer trainer = isActive
              ? gymFacade.activateTrainer(username)
              : gymFacade.deactivateTrainer(username);

      return ResponseEntity.ok(
              Map.of(
                      "username", trainer.getUser().getUsername(),
                      "active", trainer.getUser().getIsActive()
              )
      );
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(Map.of("error", e.getMessage()));
    }
  }
}