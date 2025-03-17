package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class TraineeUpdateRequest {

  @NotBlank(message="username should be provided") private String username;

  @NotBlank(message = "firstname should be provided") private String firstName;

  @NotBlank(message = "lastname should be provided") private String lastName;

  private Date dateOfBirth;

  private String address;

  @NotNull(message = "activity status should be provided") private Boolean isActive;

  public TraineeUpdateRequest() {}

  public TraineeUpdateRequest(String username, String firstName,
                              String lastName, Date dateOfBirth, String address,
                              Boolean isActive) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.isActive = isActive;
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
}
