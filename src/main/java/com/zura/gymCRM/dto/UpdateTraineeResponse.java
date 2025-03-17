package com.zura.gymCRM.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;

public class UpdateTraineeResponse {
  private String username;
  private String firstName;
  private String lastName;
  private Date dateOfBirth;
  private String address;
  private Boolean isActive;
  private List<TrainerInfo> trainers; // List of trainers

  public UpdateTraineeResponse(String username, String firstName,
                               String lastName, Date dateOfBirth,
                               String address, Boolean isActive,
                               List<TrainerInfo> trainers) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.isActive = isActive;
    this.trainers = trainers;
  }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public Date getDateOfBirth() { return dateOfBirth; }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getAddress() { return address; }

  public void setAddress(String address) { this.address = address; }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public List<TrainerInfo> getTrainers() { return trainers; }

  public void setTrainers(List<TrainerInfo> trainers) {
    this.trainers = trainers;
  }
}
