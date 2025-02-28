package com.zura.gymCRM.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "training")
public class Training {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "trainee_id", nullable = false)
  private Trainee trainee;

  @ManyToOne
  @JoinColumn(name = "trainer_id", nullable = false)
  private Trainer trainer;

  @Column(nullable = false) private String trainingName;

  @ManyToOne
  @JoinColumn(name = "trainingtype_id", nullable = false)
  private TrainingType trainingType;

  @Column private Date trainingDate;

  @Column(nullable = false) private int trainingDuration;

  public Training() {}

  // Parameterized constructor
  public Training(Trainee trainee, Trainer trainer, String trainingName,
                  TrainingType trainingType, Date trainingDate,
                  int trainingDuration) {
    this.trainee = trainee;
    this.trainer = trainer;
    this.trainingName = trainingName;
    this.trainingType = trainingType;
    this.trainingDate = trainingDate;
    this.trainingDuration = trainingDuration;
  }

  // Getters and Setters
  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public Trainee getTrainee() { return trainee; }

  public void setTrainee(Trainee trainee) { this.trainee = trainee; }

  public Trainer getTrainer() { return trainer; }

  public void setTrainer(Trainer trainer) { this.trainer = trainer; }

  public String getTrainingName() { return trainingName; }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }

  public TrainingType getTrainingType() { return trainingType; }

  public void setTrainingType(TrainingType trainingType) {
    this.trainingType = trainingType;
  }

  public Date getTrainingDate() { return trainingDate; }

  public void setTrainingDate(Date trainingDate) {
    this.trainingDate = trainingDate;
  }

  public int getTrainingDuration() { return trainingDuration; }

  public void setTrainingDuration(int trainingDuration) {
    this.trainingDuration = trainingDuration;
  }
}
