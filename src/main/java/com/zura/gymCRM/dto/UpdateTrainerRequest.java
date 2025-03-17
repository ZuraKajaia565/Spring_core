package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTrainerRequest {
  @NotBlank(message = "username should be provided") private String username;
  @NotBlank(message = "firstname should be provided") private String firstName;
  @NotBlank(message = "lastname should be provided") private String lastName;
  @NotBlank(message = "specializzation should be provided") private String specialization;
  @NotNull(message = "active status should be provided") private Boolean isActive;

  public UpdateTrainerRequest(String username, String firstName,
                              String lastName, String specialization,
                              Boolean isActive) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
    this.isActive = isActive;
  }

  public UpdateTrainerRequest() {}

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
}
