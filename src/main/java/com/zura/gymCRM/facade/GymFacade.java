package com.zura.gymCRM.facade;

import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.model.Trainer;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import com.zura.gymCRM.service.TraineeService;
import com.zura.gymCRM.service.TrainerService;
import com.zura.gymCRM.service.TrainingService;
import java.time.LocalDate;
import java.util.Optional;
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
    Trainee trainee = new Trainee(userId, firstName, lastName, null, null,
                                  isActive, dateOfBirth, address, training);
    return traineeService.createTrainee(trainee);
  }

  public Trainee updateTrainee(Trainee trainee) {
    return traineeService.updateTrainee(trainee);
  }

  public void deleteTrainee(int userId) {
    traineeService.deleteTrainee(userId);
  }

  public Optional<Trainee> selectTrainee(int userId) {
    return traineeService.selectTrainee(userId);
  }

  public Trainer addTrainer(int userId, String firstName, String lastName,
                            boolean isActive, String specialization,
                            String trainingTypeName, Training training) {
    Trainer trainer = new Trainer(userId, firstName, lastName, null, null,
                                  isActive, specialization,
                                  new TrainingType(trainingTypeName), training);
    return trainerService.createTrainer(trainer);
  }

  public Trainer updateTrainer(Trainer trainer) {
    return trainerService.updateTrainer(trainer);
  }

  public Optional<Trainer> getTrainer(int userId) {
    return trainerService.getTrainer(userId);
  }

  public Training addTraining(int traineeId, int trainerId, String trainingName,
                              String trainingType, LocalDate trainingDate,
                              int trainingDuration, String trainingTypeName) {
    Training training = new Training(
        traineeId, trainerId, trainingName, trainingType, trainingDate,
        trainingDuration, new TrainingType(trainingTypeName));
    return trainingService.createTraining(training);
  }

  public Optional<Training> getTraining(int traineeId, int trainerId) {
    return trainingService.getTraining(traineeId, trainerId);
  }
}
