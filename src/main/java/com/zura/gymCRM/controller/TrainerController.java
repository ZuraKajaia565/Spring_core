package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.ActivateDeActivateRequest;
import com.zura.gymCRM.dto.TraineeInfo;
import com.zura.gymCRM.dto.TraineeProfileResponse;
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
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@Validated
public class TrainerController {

  private final GymFacade gymFacade;

  public TrainerController(GymFacade gymFacade) { this.gymFacade = gymFacade; }

  @PostMapping("/register")
  @Operation(summary = "Trainer Registration")
  public ResponseEntity<?>
  registerTrainee(@RequestBody @Valid TrainerRegistrationRequest trainerDTO) {
    try {
      Optional<TrainingType> trainingtype = gymFacade.selectTrainingTypeByID(
          trainerDTO.getSpecialization().getId());
      if (trainingtype.isEmpty() ||
          !trainingtype.get().getTrainingTypeName().equals(
              trainerDTO.getSpecialization().getTrainingTypeName())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Invalid specialization: TrainingType not found");
      }
      Trainer createdTrainer = gymFacade.addTrainer(
          trainerDTO.getFirstName(), trainerDTO.getLastName(), true,
          trainerDTO.getSpecialization());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new TrainerRegistrationResponse(
              createdTrainer.getUser().getUsername(),
              createdTrainer.getUser().getPassword()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  @GetMapping(value = "/{username}/account", produces = org.springframework.http.MediaType
                                                 .APPLICATION_JSON_VALUE)
  @Operation(method="Get Trainer Profile", summary = "Get Trainer Profile")
  public ResponseEntity<?>
  getTraineeProfile(@PathVariable String username) {
    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);

    if (trainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    List<TraineeInfo> trainers =
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
        trainer.get().getUser().getIsActive(), trainers);

    return ResponseEntity.ok(response);
  }

  @PutMapping(
      value = "/{username}/account-update",
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(method="Update Trainer Profile", summary = "Update Trainer Profile")
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
  @Operation(method="Get Trainer Trainings List", summary = "Get Trainer Trainings List")
  public ResponseEntity<?>
  getTraineeTrainingsList(@PathVariable String username,
                          @RequestParam(required = false) Date periodFrom,
                          @RequestParam(required = false) Date periodTo,
                          @RequestParam(required = false) String traineeName) {

    Optional<Trainer> trainer = gymFacade.selectTrainerByUsername(username);
    if (trainer.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
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

  @PatchMapping("/{username}/activate")
  @Operation(method="Activate Trainer", summary = "Activate Trainer")
  public ResponseEntity<?>
  activateTrainee(@PathVariable String username, @RequestBody ActivateDeActivateRequest request) {
    try {
      Trainer trainer = gymFacade.activateTrainer(username);
      return ResponseEntity.ok(trainer);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PatchMapping("/{username}/deactivate")
  @Operation(method="De-activate Trainer", summary = "De-activate Trainer")
  public ResponseEntity<?>
  deactivateTrainee(@PathVariable String username, @RequestBody ActivateDeActivateRequest request) {
    try {
      Trainer trainer = gymFacade.deactivateTrainer(username);
      return ResponseEntity.ok(trainer);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
