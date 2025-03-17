package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainerRequest {
  private String traineeUsername;
  @NotEmpty(message = "trainerlist should be provided") private List<String> trainersList;

    public <T> UpdateTraineeTrainerRequest(String s, List<String> list) {
      this.traineeUsername = s;
      this.trainersList = list;
    }

    public String getTraineeUsername() { return traineeUsername; }

  public void setTraineeUsername(String traineeUsername) {
    this.traineeUsername = traineeUsername;
  }

  public List<String> getTrainersList() { return trainersList; }

  public void setTrainersList(List<String> trainersList) {
    this.trainersList = trainersList;
  }
}
