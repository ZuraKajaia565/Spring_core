package com.zura.gymCRM.controller;

import com.zura.gymCRM.dto.TrainingTypeResponse;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainingTypeController {

  private final GymFacade gymFacade;

  @Autowired
  public TrainingTypeController(GymFacade gymFacade) {
    this.gymFacade = gymFacade;
  }

  @Operation(summary = "Get all training types",
          description = "Fetch all available training types.")
  @ApiResponses(value =
          {
                  @ApiResponse(responseCode = "200",
                          description =
                                  "Successfully fetched training types.")
                  ,
                  @ApiResponse(responseCode = "500",
                          description = "Internal server error")
          })
  @GetMapping("/training-types")
  public ResponseEntity<?>
  getTrainingTypes() {
    List<TrainingType> trainingTypes = gymFacade.selectAllTrainings();

    List<TrainingTypeResponse> response =
            trainingTypes.stream()
                    .map(trainingType
                            -> new TrainingTypeResponse(
                            trainingType.getId(), trainingType.getTrainingTypeName()))
                    .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }
}


