package com.zura.gymCRM.dto;

public class TrainerInfo {
  private String username;
  private String firstName;
  private String lastName;
  private String specialization;

  public TrainerInfo(String username, String firstName, String lastName,
                     String specialization) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialization = specialization;
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
}
