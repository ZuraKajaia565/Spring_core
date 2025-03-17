package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

public class TraineeRegistrationRequest {

  @NotBlank(message = "First name is required") private String firstName;

  @NotBlank(message = "Last name is required") private String lastName;

  private Date dateOfBirth;
  private String address;

  public TraineeRegistrationRequest(String firstName, String lastName,
                                    Date dateOfBirth, String address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
  }

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
}
