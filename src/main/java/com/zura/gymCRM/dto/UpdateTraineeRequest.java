package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class UpdateTraineeRequest {

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  private Date dateOfBirth;

  private String address;

  @NotNull(message = "Active status is required")
  private Boolean isActive;

  public UpdateTraineeRequest(String username, String firstName, String lastName,
                              Date dateOfBirth, String address, boolean isActive) {
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

  public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

  public String getAddress() { return address; }

  public void setAddress(String address) { this.address = address; }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
