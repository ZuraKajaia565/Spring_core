package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainerRequest {

  @NotEmpty(message = "trainerlist should be provided") private List<String> trainersList;

    public UpdateTraineeTrainerRequest(List<String> list) {
      this.trainersList = list;
    }

  public UpdateTraineeTrainerRequest() {}


  public List<String> getTrainersList() { return trainersList; }

  public void setTrainersList(List<String> trainersList) {
    this.trainersList = trainersList;
  }
}
