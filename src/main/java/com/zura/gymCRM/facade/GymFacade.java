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
import com.zura.gymCRM.service.TrainingTypeService;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
  private TraineeService traineeService;
  private TrainerService trainerService;
  private TrainingService trainingService;
  private TrainingTypeService trainingTypeService;

  private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

  @Autowired
  public GymFacade(TraineeService traineeService, TrainerService trainerService,
                   TrainingService trainingService,
                   TrainingTypeService trainingTypeService) {
    this.traineeService = traineeService;
    this.trainerService = trainerService;
    this.trainingService = trainingService;
    this.trainingTypeService = trainingTypeService;
  }

  @Transactional
  public Trainee addTrainee(String firstName, String lastName, boolean isActive,
                            Date dateOfBirth, String address) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Adding trainee: firstName={}, lastName={}",
            transactionId, firstName, lastName);
    try {
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
      Trainee savedTrainee = traineeService.createTrainee(trainee);
      logger.info("Transaction ID: {} - Successfully added trainee with ID={}",
              transactionId, savedTrainee.getUser().getId());
    return savedTrainee;
    }
    catch (Exception e) {
      logger.error("Transaction ID: {} - Error while adding trainee: {}", transactionId, e.getMessage(), e);
      throw e;
    }
  }

  @Transactional
  public Trainee updateTrainee(Trainee trainee) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Updating trainee: {}", transactionId, trainee.getUser().getUsername());
    if (traineeService.selectTraineeByUsername(
            trainee.getUser().getUsername()) != null) {
      logger.info("Transaction ID: {} - Attempt to update trainee: {}", transactionId, trainee.getUser().getUsername());
      return traineeService.updateTrainee(trainee);
    } else {
      logger.error("Transaction ID: {} - Trainee not found: {}", transactionId, trainee.getUser().getUsername());
      throw new NotFoundException("Trainee with this {} id is not found" +
                                  trainee.getUser().getId());
    }
  }

  @Transactional
  public List<Trainer> getTrainersListofTrainee(Trainee trainee) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Get Trainers List of trainee: {}", transactionId, trainee.getUser().getUsername());
    return traineeService.getTrainersList(trainee.getId());
  }

  @Transactional
  public Trainee activateTrainee(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Activating trainee: {}", transactionId, username);
    return traineeService.activateTrainee(username);
  }

  @Transactional
  public Trainee deactivateTrainee(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deactivating trainee: {}", transactionId, username);
    return traineeService.deactivateTrainee(username);
  }

  @Transactional
  public void deleteTraineeByUsername(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deleting trainee: {}", transactionId, username);
    if (traineeService.selectTraineeByUsername(username) != null) {
      logger.info("Transaction ID: {} - Attempting  delete trainee: {}", transactionId, username);
      traineeService.deleteTraineeByUsername(username);
    } else {
      logger.error("Transaction ID: {} - Trainee not found: {}", transactionId, username);
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  public List<Training>
  getTraineeTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String trainerName, String trainingType) {
    String transactionId = MDC.get("transactionId");
    if (traineeService.selectTraineeByUsername(username) != null) {
      logger.info("Transaction ID: {} - Get Trainings of trainee: {}", transactionId, username);
      return traineeService.getTraineeTrainingsByCriteria(
          username, fromDate, toDate, trainerName, trainingType);
    } else {
      logger.error("Transaction ID: {} - Trainee not found: {}", transactionId, username);
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public List<Trainer> getUnassignedTrainersForTrainee(String username) {
    String transactionId = MDC.get("transactionId");
    if (traineeService.selectTraineeByUsername(username) != null) {
      logger.info("Transaction ID: {} - Get Unassigned Trainers of trainee: {}", transactionId, username);
      return traineeService.getUnassignedTrainersForTrainee(username);
    } else {
      logger.error("Transaction ID: {} - Trainee not found: {}", transactionId, username);
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public void changeTraineePassword(String username, String newPassword) {
    String transactionId = MDC.get("transactionId");
    if (traineeService.selectTraineeByUsername(username) != null &&
        newPassword.length() == 10) {
      logger.info("Transaction ID: {} - change password of trainee: {}", transactionId, username);
      traineeService.changePassword(username, newPassword);
    } else {
      logger.error("Transaction ID: {} - Invalid Credentials for Trainee {}", transactionId, username);
      throw new NotFoundException("Trainee with username " + username + " is not found");
    }
  }

  public Optional<Trainee> selectTraineeByusername(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - select trainee of username: {}", transactionId, username);
    return traineeService.selectTraineeByUsername(username);
  }

  @Transactional
  public Trainee updateTraineeTrainerRelationship(String traineeUsername,
                                                  String trainerUsername,
                                                  boolean assign) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Updating relationship trainee: {} and between trainer: {}", transactionId, traineeUsername, trainerUsername);
    return traineeService.assignOrRemoveTrainer(traineeUsername,
                                                trainerUsername, assign);
  }

  @Transactional
  public Trainer addTrainer(String firstName, String lastName, boolean isActive,
                            TrainingType trainingType) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Adding trainer: firstName={}, lastName={}",
            transactionId, firstName, lastName);
try {
  User user = new User();
  user.setFirstName(firstName);
  user.setLastName(lastName);
  user.setUsername(null);
  user.setPassword(null);
  user.setIsActive(isActive);

  Trainer trainer = new Trainer();
  trainer.setUser(user);
  trainer.setSpecialization(trainingType);
  Trainer savedTrainer = trainerService.createTrainer(trainer);
  logger.info("Transaction ID: {} - Successfully added trainer with ID={}",
          transactionId, savedTrainer.getUser().getId());
  return savedTrainer;
} catch (Exception e) {
  logger.error("Transaction ID: {} - Error while adding trainer: {}", transactionId, e.getMessage(), e);
  throw e;
}
  }

  @Transactional
  public Trainer updateTrainer(Trainer trainer) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Updating trainer: {}", transactionId, trainer.getUser().getUsername());
    if (trainerService.findTrainerByUsername(trainer.getUser().getUsername()) !=
        null) {
      logger.info("Transaction ID: {} - Attempting to update trainer: {}", transactionId, trainer.getUser().getUsername());
      return trainerService.updateTrainer(trainer);
    } else {
      logger.error("Transaction ID: {} - Trainer not found: {}", transactionId, trainer.getUser().getUsername());
      throw new NotFoundException("Trainee with this {} username is not found" +
                                  trainer.getUser().getUsername());
    }
  }

  public List<Training> getTrainerTrainingsByCriteria(String username,
                                                      Date fromDate,
                                                      Date toDate,
                                                      String traineeName) {
    String transactionId = MDC.get("transactionId");
    if (trainerService.findTrainerByUsername(username) != null) {
      logger.info("Transaction ID: {} - Get trainings trainer: {}", transactionId, username);
      return trainerService.getTrainerTrainingsByCriteria(username, fromDate,
                                                          toDate, traineeName);
    } else {
      logger.error("Transaction ID: {} - Trainer not found: {}", transactionId, username);
      throw new NotFoundException("Trainer with this {} username is not found" +
                                  username);
    }
  }

  @Transactional
  public void changeTrainerPassword(String username, String newPassword) {
    String transactionId = MDC.get("transactionId");
    if (trainerService.findTrainerByUsername(username) != null &&
        newPassword.length() == 10) {
      logger.info("Transaction ID: {} - Attempting change password for trainer: {}", transactionId, username);
      trainerService.changePassword(username, newPassword);
    } else {
      logger.error("Transaction ID: {} - Trainer not found: {}", transactionId, username);
      throw new NotFoundException("Trainer with username " + username + " is not found");
    }
  }

  @Transactional
  public Trainer activateTrainer(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Activating trainer: {}", transactionId, username);
    return trainerService.activateTrainer(username);
  }

  @Transactional
  public Trainer deactivateTrainer(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deactivating trainer: {}", transactionId, username);
    return trainerService.deactivateTrainer(username);
  }

  @Transactional
  public List<Trainee> getTraineesListofTrainer(Trainer trainer) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - get Trainee list of  trainer: {}", transactionId, trainer.getUser().getUsername());
    return trainerService.getTraineesList(trainer.getId());
  }

  public Optional<Trainer> selectTrainerByUsername(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Selecting  Trainer: {}", transactionId, username);
    return trainerService.findTrainerByUsername(username);
  }

  @Transactional
  public Optional<TrainingType> selectTrainingByName(String trainingtypename) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Selecting Trainingtypename: {}", transactionId, trainingtypename);
    return trainingTypeService.findTrainingTypeByTrainingTypeName(
        trainingtypename);
  }

  @Transactional
  public Training addTraining(Trainee trainee, Trainer trainer,
                              String trainingName, TrainingType trainingType,
                              Date trainingDate, int trainingDuration) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Adding training: trainingName={}, trainee={}, trainer={}",
            transactionId, trainingName, trainee.getUser().getUsername(), trainer.getUser().getUsername());
    try {
      Training training = new Training();
      training.setTrainee(trainee);
      training.setTrainer(trainer);
      training.setTrainingName(trainingName);
      training.setTrainingType(trainingType);
      training.setTrainingDate(trainingDate);
      training.setTrainingDuration(trainingDuration);

      Training savedTraining = trainingService.createTraining(training);
      logger.info("Transaction ID: {} - Successfully added training with ID={}",
              transactionId, savedTraining.getId());
      return savedTraining;
    }
    catch (Exception e) {
      logger.error("Transaction ID: {} - Error while adding training: {}", transactionId, e.getMessage(), e);
      throw e;
    }
  }

  public Optional<Training> getTraining(Long trainingId) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Retrieving training with ID={}", transactionId, trainingId);
    Optional<Training> training = trainingService.getTraining(trainingId);
    if (training.isPresent()) {
      logger.info("Transaction ID: {} - Training found: ID={}", transactionId, trainingId);
    } else {
      logger.warn("Transaction ID: {} - Training not found: ID={}", transactionId, trainingId);
    }
    return training;
  }

  public List<TrainingType> selectAllTrainings() {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Retrieving all training types", transactionId);
    List<TrainingType> trainingTypes = trainingTypeService.findAllTrainings();
    logger.info("Transaction ID: {} - Retrieved {} training types", transactionId, trainingTypes.size());
    return trainingTypes;
  }

  public Optional<TrainingType> selectTrainingTypeByID(Long id) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Retrieving training type by ID={}", transactionId, id);
    Optional<TrainingType> trainingType = trainingTypeService.findTrainingTypeById(id);
    if (trainingType.isPresent()) {
      logger.info("Transaction ID: {} - Training type found: ID={}", transactionId, id);
    } else {
      logger.warn("Transaction ID: {} - Training type not found: ID={}", transactionId, id);
    }
    return trainingType;
  }

  public Optional<TrainingType> selectTrainingTypeByName(String name) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Retrieving training type by name={}", transactionId, name);
    Optional<TrainingType> trainingType = trainingTypeService.findTrainingTypeByTrainingTypeName(name);
    if (trainingType.isPresent()) {
      logger.info("Transaction ID: {} - Training type found: name={}", transactionId, name);
    } else {
      logger.warn("Transaction ID: {} - Training type not found: name={}", transactionId, name);
    }
    return trainingType;
  }
}
