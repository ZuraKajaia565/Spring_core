package com.zura.gymCRM.facade;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.service.TraineeService;
import com.zura.gymCRM.service.TrainerService;
import com.zura.gymCRM.service.TrainingService;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
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

  @Transactional
  public Trainee addTrainee(String firstName, String lastName, boolean isActive,
                            Date dateOfBirth, String address) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(null);
    user.setPassword(null);
    user.setIsActive(isActive);

    Trainee trainee = new Trainee();
    trainee.setUser(user);
    trainee.setDateOfBirth(dateOfBirth);
    trainee.setAddress(address);
    return traineeService.createTrainee(trainee);
  }

  @Transactional
  public Trainee updateTrainee(Trainee trainee) {
    if (traineeService.selectTraineeByUsername(
            trainee.getUser().getUsername()) != null) {
      return traineeService.updateTrainee(trainee);
    } else {
      throw new NotFoundException("Trainee with this {} id is not found" +
                                  trainee.getUser().getId());
    }
  }

  @Transactional
  public Trainee activateTrainee(String username) {
    return traineeService.activateTrainee(username);
  }

  @Transactional
  public Trainee deactivateTrainee(String username) {
    return traineeService.deactivateTrainee(username);
  }

  @Transactional
  public void deleteTraineeByUsername(String username) {
    if (traineeService.selectTraineeByUsername(username) != null) {

      traineeService.deleteTraineeByUsername(username);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  public List<Training>
  getTraineeTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String trainerName, String trainingType) {
    if (traineeService.selectTraineeByUsername(username) != null) {

      return traineeService.getTraineeTrainingsByCriteria(
          username, fromDate, toDate, trainerName, trainingType);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  public List<Trainer> getUnassignedTrainersForTrainee(String username) {
    if (traineeService.selectTraineeByUsername(username) != null) {
      return traineeService.getUnassignedTrainersForTrainee(username);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public void changeTraineePassword(String username, String newPassword) {
    if (traineeService.selectTraineeByUsername(username) != null &&
        newPassword.length() == 10) {

      traineeService.changePassword(username, newPassword);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  public Trainee selectTraineeByusername(String username) {
    return traineeService.selectTraineeByUsername(username);
  }

  @Transactional
  public Trainee updateTraineeTrainerRelationship(String traineeUsername,
                                                  String trainerUsername,
                                                  boolean assign) {
    return traineeService.assignOrRemoveTrainer(traineeUsername,
                                                trainerUsername, assign);
  }

  @Transactional
  public Trainer addTrainer(String firstName, String lastName, boolean isActive,
                            TrainingType trainingType) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(null);
    user.setPassword(null);
    user.setIsActive(isActive);

    Trainer trainer = new Trainer();
    trainer.setUser(user);
    trainer.setSpecialization(trainingType);
    return trainerService.createTrainer(trainer);
  }

  @Transactional
  public Trainer updateTrainer(Trainer trainer) {
    if (trainerService.findTrainerByUsername(trainer.getUser().getUsername()) !=
        null) {

      return trainerService.updateTrainer(trainer);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  trainer.getUser().getUsername());
    }
  }

  public List<Training>
  getTrainerTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String trainerName, String trainingType) {
    if (trainerService.findTrainerByUsername(username) != null) {

      return trainerService.getTrainerTrainingsByCriteria(
          username, fromDate, toDate, trainerName, trainingType);
    } else {
      throw new NotFoundException("Trainer with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public void changeTrainerPassword(String username, String newPassword) {
    if (trainerService.findTrainerByUsername(username) != null &&
        newPassword.length() == 10) {

      trainerService.changePassword(username, newPassword);
    } else {
      throw new NotFoundException("Trainer with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public Trainer activateTrainer(String username) {
    return trainerService.activateTrainer(username);
  }

  @Transactional
  public Trainer deactivateTrainer(String username) {
    return trainerService.deactivateTrainer(username);
  }

  public Trainer selectTrainerByUsername(String username) {
    if (trainerService.findTrainerByUsername(username) != null) {
      return trainerService.findTrainerByUsername(username);
    } else {
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public Training addTraining(Trainee trainee, Trainer trainer,
                              String trainingName, TrainingType trainingType,
                              Date trainingDate, int trainingDuration) {
    Training training = new Training();
    training.setTrainee(trainee);
    training.setTrainer(trainer);
    training.setTrainingName(trainingName);
    training.setTrainingType(trainingType);
    training.setTrainingDate(trainingDate);
    training.setTrainingDuration(trainingDuration);
    return trainingService.createTraining(training);
  }

  public Optional<Training> getTraining(Long trainingId) {
    return trainingService.getTraining(trainingId);
  }
}
