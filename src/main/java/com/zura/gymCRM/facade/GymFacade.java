package com.zura.gymCRM.facade;

import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.model.Trainer;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.service.TraineeService;
import com.zura.gymCRM.service.TrainerService;
import com.zura.gymCRM.service.TrainingService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
  private TraineeService traineeService;
  private TrainerService trainerService;
  private TrainingService trainingService;

  @Autowired
  public GymFacade(TraineeService traineeService, TrainerService trainerService,
                   TrainingService trainingService) {
    this.traineeService = traineeService;
    this.trainerService = trainerService;
    this.trainingService = trainingService;
  }

  public Trainee AddTrainee(int userId, String firstName, String lastName,
                            boolean isActive, LocalDate dateOfBirth,
                            String address, Training training) {
    return traineeService.createTrainee(userId, firstName, lastName, isActive,
                                        dateOfBirth, address, training);
  }

  public Trainee updateTrainee(Trainee trainee) {
    return traineeService.updateTrainee(trainee);
  }

  public void deleteTrainee(int userId) {
    traineeService.deleteTrainee(userId);
  }

  public Trainee selectTrainee(int userId) {
    return traineeService.selectTrainee(userId);
  }

  public Trainer addTrainer(int userId, String firstName, String lastName,
                            boolean isActive, String specialization,
                            String trainingTypeName, Training training) {
    return trainerService.createTrainer(userId, firstName, lastName, isActive,
                                        specialization, trainingTypeName,
                                        training);
  }

  public Trainer updateTrainer(Trainer trainer) {
    return trainerService.updateTrainer(trainer);
  }

  public Trainer getTrainer(int userId) {
    return trainerService.getTrainer(userId);
  }

  public Training addTraining(int traineeId, int trainerId, String trainingName,
                              String trainingType, LocalDate trainingDate,
                              int trainingDuration, String trainingTypeName) {
    return trainingService.createTraining(traineeId, trainerId, trainingName,
                                          trainingType, trainingDate,
                                          trainingDuration, trainingTypeName);
  }

  public Training getTraining(int traineeId, int trainerId) {
    return trainingService.getTraining(traineeId, trainerId);
  }
}
