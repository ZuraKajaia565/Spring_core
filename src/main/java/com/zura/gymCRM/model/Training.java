package com.zura.gymCRM.model;

import java.time.LocalDate;

public class Training {
  private Integer traineeId;
  private Integer trainerId;
  private String trainingName;
  private String trainingType;
  private LocalDate trainingDate;
  private Integer trainingDuration;
  private TrainingType trainingTypeName;

  public Training(Integer traineeId, Integer trainerId, String trainingName,
                  String trainingType, LocalDate trainingDate,
                  Integer trainingDuration, TrainingType trainingTypeName) {
    this.traineeId = traineeId;
    this.trainerId = trainerId;
    this.trainingName = trainingName;
    this.trainingType = trainingType;
    this.trainingDate = trainingDate;
    this.trainingDuration = trainingDuration;
    this.trainingTypeName = trainingTypeName;
  }

  public Integer getTraineeId() { return traineeId; }

  public void setTraineeId(Integer traineeId) { this.traineeId = traineeId; }

  public Integer getTrainerId() { return trainerId; }

  public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }

  public String getTrainingName() { return trainingName; }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }

  public String getTrainingType() { return trainingType; }

  public void setTrainingType(String trainingType) {
    this.trainingType = trainingType;
  }

  public LocalDate getTrainingDate() { return trainingDate; }

  public void setTrainingDate(LocalDate trainingDate) {
    this.trainingDate = trainingDate;
  }

  public Integer getTrainingDuration() { return trainingDuration; }

  public void setTrainingDuration(Integer trainingDuration) {
    this.trainingDuration = trainingDuration;
  }

  public TrainingType getTrainingTypeName() { return trainingTypeName; }

  public void setTrainingTypeName(TrainingType trainingTypeName) {
    this.trainingTypeName = trainingTypeName;
  }
}
