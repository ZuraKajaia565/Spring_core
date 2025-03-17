package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Date;

public class AddTrainingRequest {
  @NotBlank(message = "Trainee username is required") private String traineeUsername;
  @NotBlank(message = "Trainer username is required") private String trainerUsername;
  @NotBlank(message = "TrainingName is required") private String trainingName;
  @NotBlank(message = "Date is required") private Date trainingDate;
  private String trainingDuration;

  public AddTrainingRequest(String traineeUsername, String trainerUsername,
                            String trainingName, Date trainingDate,
                            String trainingDuration) {
    this.traineeUsername = traineeUsername;
    this.trainerUsername = trainerUsername;
    this.trainingName = trainingName;
    this.trainingDate = trainingDate;
    this.trainingDuration = trainingDuration;
  }

  public String getTraineeUsername() { return traineeUsername; }

  public void setTraineeUsername(String traineeUsername) {
    this.traineeUsername = traineeUsername;
  }

  public String getTrainerUsername() { return trainerUsername; }

  public void setTrainerUsername(String trainerUsername) {
    this.trainerUsername = trainerUsername;
  }

  public String getTrainingName() { return trainingName; }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }

  public Date getTrainingDate() { return trainingDate; }

  public void setTrainingDate(Date trainingDate) {
    this.trainingDate = trainingDate;
  }

  public String getTrainingDuration() { return trainingDuration; }

  public void setTrainingDuration(String trainingDuration) {
    this.trainingDuration = trainingDuration;
  }
}
