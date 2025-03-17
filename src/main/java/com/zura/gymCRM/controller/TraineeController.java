package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.ActivateDeActivateRequest;
import com.zura.gymCRM.dto.TraineeProfileResponse;
import com.zura.gymCRM.dto.TraineeRegistrationRequest;
import com.zura.gymCRM.dto.TraineeRegistrationResponse;
import com.zura.gymCRM.dto.TraineeTrainingInfo;
import com.zura.gymCRM.dto.TrainerInfo;
import com.zura.gymCRM.dto.UpdateTraineeRequest;
import com.zura.gymCRM.dto.UpdateTraineeResponse;
import com.zura.gymCRM.dto.UpdateTraineeTrainerRequest;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainees")
@Validated
public class TraineeController {

  private final GymFacade gymFacade;

  public TraineeController(GymFacade gymFacade) { this.gymFacade = gymFacade; }

  @PostMapping("/register")
  @Operation(summary = "Trainee Registration", description = "Trainee Registration")
  public ResponseEntity<?>
  registerTrainee(@RequestBody @Valid TraineeRegistrationRequest traineeDTO) {
    try {
      Trainee createdTrainee = gymFacade.addTrainee(
          traineeDTO.getFirstName(), traineeDTO.getLastName(), true,
          traineeDTO.getDateOfBirth(), traineeDTO.getAddress());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new TraineeRegistrationResponse(
              createdTrainee.getUser().getUsername(),
              createdTrainee.getUser().getPassword()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  @GetMapping(value = "/profile", produces = org.springframework.http.MediaType
                                                 .APPLICATION_JSON_VALUE)
  @Operation(method = "Get Trainee Profile", summary = "Get Trainee Profile")
  public ResponseEntity<?>
  getTraineeProfile(@RequestParam String username) {
    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);

    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    List<TrainerInfo> trainers =
        gymFacade.getTrainersListofTrainee(trainee.get())
            .stream()
            .map(trainer
                 -> new TrainerInfo(
                     trainer.getUser().getUsername(),
                     trainer.getUser().getFirstName(),
                     trainer.getUser().getLastName(),
                     trainer.getSpecialization().getTrainingTypeName()))
            .collect(Collectors.toList());

    TraineeProfileResponse response = new TraineeProfileResponse(
        trainee.get().getUser().getUsername(),
        trainee.get().getUser().getFirstName(),
        trainee.get().getUser().getLastName(), trainee.get().getDateOfBirth(),
        trainee.get().getAddress(), trainee.get().getUser().getIsActive(),
        trainers);

    return ResponseEntity.ok(response);
  }

  @PutMapping(value = "/profile", produces = org.springframework.http.MediaType
                                                 .APPLICATION_JSON_VALUE)
  @Operation(method = "Update Trainee Profile", summary = "Update Trainee Profile")
  public ResponseEntity<?>
  updateTraineeProfile(@RequestBody UpdateTraineeRequest request) {
    Optional<Trainee> trainee =
        gymFacade.selectTraineeByusername(request.getUsername());

    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    Trainee updatedTrainee = trainee.get();
    updatedTrainee.getUser().setFirstName(request.getFirstName());
    updatedTrainee.getUser().setLastName(request.getLastName());
    if (request.getDateOfBirth() != null) {
      updatedTrainee.setDateOfBirth(request.getDateOfBirth());
    }
    if (request.getAddress() != null) {
      updatedTrainee.setAddress(request.getAddress());
    }
    updatedTrainee.getUser().setIsActive(request.getIsActive());

    gymFacade.updateTrainee(updatedTrainee);

    List<TrainerInfo> trainers =
        gymFacade.getTrainersListofTrainee(updatedTrainee)
            .stream()
            .map(trainer
                 -> new TrainerInfo(
                     trainer.getUser().getUsername(),
                     trainer.getUser().getFirstName(),
                     trainer.getUser().getLastName(),
                     trainer.getSpecialization().getTrainingTypeName()))
            .collect(Collectors.toList());

    UpdateTraineeResponse response = new UpdateTraineeResponse(
        updatedTrainee.getUser().getUsername(),
        updatedTrainee.getUser().getFirstName(),
        updatedTrainee.getUser().getLastName(), updatedTrainee.getDateOfBirth(),
        updatedTrainee.getAddress(), updatedTrainee.getUser().getIsActive(),
        trainers);

    return ResponseEntity.ok(response);
  }

  @GetMapping(
      value = "/unassigned-trainers",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(method="Get not assigned on trainee active trainers", summary = "Get not assigned on trainee active trainers")
  public ResponseEntity<?>
  getUnassignedActiveTrainers(@RequestParam String username) {
    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);

    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    List<Trainer> unassignedTrainers =
        gymFacade.getUnassignedTrainersForTrainee(username);

    System.out.println(unassignedTrainers.size());
    unassignedTrainers =
        unassignedTrainers.stream()
            .filter(trainer -> trainer.getUser().getIsActive())
            .toList();

    List<TrainerInfo> trainerInfos =
        unassignedTrainers.stream()
            .map(trainer
                 -> new TrainerInfo(
                     trainer.getUser().getUsername(),
                     trainer.getUser().getFirstName(),
                     trainer.getUser().getLastName(),
                     trainer.getSpecialization().getTrainingTypeName()))
            .collect(Collectors.toList());


    return ResponseEntity.ok(trainerInfos);
  }

  @PutMapping(
      value = "/update-trainers",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(method="Update Trainee's Trainer List", summary = "Update Trainee's Trainer List")
  public ResponseEntity<?>
  updateTraineeTrainerList(
      @RequestBody UpdateTraineeTrainerRequest updateRequest) {
    Optional<Trainee> trainee =
        gymFacade.selectTraineeByusername(updateRequest.getTraineeUsername());

    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    boolean insert = true;
    updateRequest.getTrainersList().forEach(username -> gymFacade.updateTraineeTrainerRelationship(updateRequest.getTraineeUsername(), username, insert));

    List<TrainerInfo> trainerInfos =
        gymFacade.getTrainersListofTrainee(trainee.get()).stream()
            .map(trainer
                 -> new TrainerInfo(
                     trainer.getUser().getUsername(),
                     trainer.getUser().getFirstName(),
                     trainer.getUser().getLastName(),
                     trainer.getSpecialization().getTrainingTypeName()))
            .collect(Collectors.toList());

    return ResponseEntity.ok(trainerInfos);
  }

  @GetMapping(
      value = "/trainings",
      produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(method="Get Trainee Trainings List", summary = "Get Trainee Trainings List")
  public ResponseEntity<?>
  getTraineeTrainingsList(@RequestParam String username,
                          @RequestParam(required = false) Date periodFrom,
                          @RequestParam(required = false) Date periodTo,
                          @RequestParam(required = false) String trainerName,
                          @RequestParam(required = false) String trainingType) {

    Optional<Trainee> trainee = gymFacade.selectTraineeByusername(username);
    if (trainee.isEmpty()) {
      return ResponseEntity.badRequest().body("Trainee not found");
    }

    List<Training> trainings = gymFacade.getTraineeTrainingsByCriteria(
        trainee.get().getUser().getUsername(), periodFrom, periodTo,
        trainerName, trainingType);

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

  @PatchMapping("/activate")
  @Operation(method="Activate Trainee", summary = "Activate Trainee")
  public ResponseEntity<?>
  activateTrainee(@RequestBody ActivateDeActivateRequest request) {
    try {
      Trainee trainee = gymFacade.activateTrainee(request.getUsername());
      return ResponseEntity.ok(trainee);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PatchMapping("/deactivate")
  @Operation(method="De-Activate Trainee", summary = "De-Activate Trainee")
  public ResponseEntity<?>
  deactivateTrainee(@RequestBody ActivateDeActivateRequest request) {
    try {
      Trainee trainee = gymFacade.deactivateTrainee(request.getUsername());
      return ResponseEntity.ok(trainee);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("/{username}")
  @Operation(method="Delete Trainee Profile", summary = "Delete Trainee Profile")
  public ResponseEntity<Map<String, String>>
  deleteTrainee(@PathVariable String username) {
    gymFacade.deleteTraineeByUsername(username);
    return ResponseEntity.ok(Map.of("message", "Trainee deleted successfully"));
  }
}
