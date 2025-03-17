package com.zura.gymCRM.dto;

import com.zura.gymCRM.entities.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.aspectj.bridge.IMessage;

public class TrainerRegistrationRequest {

  @NotBlank(message = "firstname should be provided") private String firstName;
  @NotBlank(message = "lastname should be provided") private String lastName;
  @NotNull(message ="specialization should be provided") private TrainingType specialization;

  public TrainerRegistrationRequest() {}

  public TrainerRegistrationRequest(String firstName, String lastName,
                                    TrainingType specialization) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
  }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public TrainingType getSpecialization() { return specialization; }

  public void setSpecialization(TrainingType specialization) {
    this.specialization = specialization;
  }
}
