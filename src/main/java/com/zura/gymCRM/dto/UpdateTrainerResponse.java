package com.zura.gymCRM.dto;

import java.util.List;

public class UpdateTrainerResponse {
  private String username;
  private String firstName;
  private String lastName;
  private String specialization;
  private Boolean isActive;
  private List<TraineeInfo> trainees;

  public UpdateTrainerResponse(String username, String firstName,
                               String lastName, String specialization,
                               Boolean isActive, List<TraineeInfo> trainees) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
    this.isActive = isActive;
    this.trainees = trainees;
  }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getSpecialization() { return specialization; }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public List<TraineeInfo> getTrainees() { return trainees; }

  public void setTrainees(List<TraineeInfo> trainees) {
    this.trainees = trainees;
  }
}
