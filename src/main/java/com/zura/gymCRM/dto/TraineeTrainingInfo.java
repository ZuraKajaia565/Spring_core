package com.zura.gymCRM.dto;

import java.time.LocalDate;
import java.util.Date;

public class TraineeTrainingInfo {
  private String trainingName;
  private Date trainingDate;
  private String trainingType;
  private int trainingDuration;
  private String trainerName;

  public TraineeTrainingInfo(String trainingName, Date trainingDate,
                             String trainingType, int trainingDuration,
                             String trainerName) {
    this.trainingName = trainingName;
    this.trainingDate = trainingDate;
    this.trainingType = trainingType;
    this.trainingDuration = trainingDuration;
    this.trainerName = trainerName;
  }

  public String getTrainingName() { return trainingName; }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }

  public Date getTrainingDate() { return trainingDate; }

  public void setTrainingDate(Date trainingDate) {
    this.trainingDate = trainingDate;
  }

  public String getTrainingType() { return trainingType; }

  public void setTrainingType(String trainingType) {
    this.trainingType = trainingType;
  }

  public int getTrainingDuration() { return trainingDuration; }

  public void setTrainingDuration(int trainingDuration) {
    this.trainingDuration = trainingDuration;
  }

  public String getTrainerName() { return trainerName; }

  public void setTrainerName(String trainerName) {
    this.trainerName = trainerName;
  }
}
