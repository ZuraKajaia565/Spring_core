package com.zura.gymCRM.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TraineeProfileResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private String username;
  private String firstName;
  private String lastName;
  private Date dateOfBirth;
  private String address;
  private Boolean isActive;
  private List<TrainerInfo> trainerslist;

  public TraineeProfileResponse(String username, String firstName,
                                String lastName, Date dateOfBirth,
                                String address, Boolean isActive,
                                List<TrainerInfo> trainerslist) {
    this.firstName = firstName;
    this.username = username;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.isActive = isActive;
    this.trainerslist = trainerslist;
  }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public Date getDateOfBirth() { return dateOfBirth; }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getAddress() { return address; }

  public void setAddress(String address) { this.address = address; }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public List<TrainerInfo> getTrainerslist() { return trainerslist; }

  public void setTrainerslist(List<TrainerInfo> trainerslist) {
    this.trainerslist = trainerslist;
  }
}
