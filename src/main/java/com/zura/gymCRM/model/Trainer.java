package com.zura.gymCRM.model;

public class Trainer extends User {
  private int userId;
  private String specialization;
  private TrainingType trainingType;
  private Training training;

  public Trainer(int userId, String firstName, String lastName, String username,
                 String password, boolean isActive, String specialization,
                 TrainingType trainingType, Training training) {
    super(firstName, lastName, username, password, isActive);
    this.specialization = specialization;
    this.userId = userId;
    this.trainingType = trainingType;
    this.training = training;
  }

  // Getter and Setter for userId
  public int getUserId() { return userId; }

  public void setUserId(int userId) { this.userId = userId; }

  // Getter and Setter for specialization
  public String getSpecialization() { return specialization; }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public TrainingType getTrainingType() { return trainingType; }

  public void setTrainingType(TrainingType trainingType) {
    this.trainingType = trainingType;
  }

  public String getFirstName() { return super.getFirstName(); }

  public void setFirstName(String firstName) { super.setFirstName(firstName); }

  public String getLastName() { return super.getLastName(); }

  public void setLastName(String lastName) { super.setLastName(lastName); }

  public String getUserName() { return super.getUserName(); }

  public void setUserName(String username) { super.setUserName(username); }

  public String getPassword() { return super.getPassword(); }

  public void setPassword(String password) { super.setPassword(password); }

  public boolean isActive() { return super.isActive(); }

  public void setActive(boolean isActive) { super.setActive(isActive); }

  public Training getTRaining() { return training; }

  public void setTraining(Training training) { this.training = training; }
}
