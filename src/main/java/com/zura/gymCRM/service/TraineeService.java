package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TraineeRepository;
import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.security.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
  private TraineeRepository traineeRepository;
  private TrainerRepository trainerRepository;

  @Autowired
  private PasswordUtil passwordUtil;

  @Autowired
  public void setTraineeRepository(TraineeRepository traineeRepository) {
    this.traineeRepository = traineeRepository;
  }

  @Autowired
  public void setTrainerRepository(TrainerRepository trainerRepository) {
    this.trainerRepository = trainerRepository;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TraineeService.class);

  @Transactional
  public Trainee createTrainee(@Valid Trainee trainee) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Creating trainee: {} {}", transactionId,
            trainee.getUser().getFirstName(), trainee.getUser().getLastName());

    try {
      List<Trainee> userlist = traineeRepository.findAll();
      trainee.getUser().setUsername(UsernameGenerator.generateUsername(
              trainee.getUser().getFirstName(), trainee.getUser().getLastName(), userlist));

      // Generate a random password
      String rawPassword = generateRandomPassword();
      logger.debug("Generated raw password: {}", rawPassword);

      // Store the encoded password in the database
      String encodedPassword = passwordUtil.encodePassword(rawPassword);
      logger.debug("Password encoded successfully");
      trainee.getUser().setPassword(encodedPassword);

      Trainee savedTrainee = traineeRepository.save(trainee);
      logger.debug("Trainee saved to database with encoded password");

      // Set the raw password for the response (not stored in DB)
      savedTrainee.getUser().setPassword(rawPassword);
      logger.debug("Raw password set in response object");

      logger.info("Transaction ID: {} - Trainee created successfully: {}",
              transactionId, trainee.getUser().getUsername());
      return savedTrainee;
    } catch (Exception e) {
      logger.error("Transaction ID: {} - Error creating trainee: {}", transactionId, e.getMessage(), e);
      throw e;
    }
  }

  @Transactional
  public void deleteTrainee(Long traineeId) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deleting trainee with ID={}", transactionId, traineeId);
    traineeRepository.deleteById(traineeId);
    logger.info("Transaction ID: {} - Trainee with ID={} deleted successfully", transactionId, traineeId);
  }

  public Optional<Trainee> selectTraineeByUsername(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Fetching trainee by username: {}", transactionId, username);
    return traineeRepository.findByUser_Username(username);
  }

  @Transactional
  public void changePassword(String username, String newPassword)
          throws EntityNotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Changing password for trainee: {}", transactionId, username);

    Optional<Trainee> traineeOpt =
            traineeRepository.findByUser_Username(username);
    if (traineeOpt.isPresent()) {
      Trainee trainee = traineeOpt.get();
      // Encode the new password before storing
      String encodedPassword = passwordUtil.encodePassword(newPassword);
      trainee.getUser().setPassword(encodedPassword);
      traineeRepository.save(trainee);
      logger.info("Transaction ID: {} - Password changed for trainee: {}", transactionId, username);
    } else {
      logger.error("Transaction ID: {} - Trainee not found: {}", transactionId, username);
      throw new EntityNotFoundException("Trainee not found");
    }
  }

  @Transactional
  public Trainee activateTrainee(String username) throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Activating trainee: {}", transactionId, username);
    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    if (trainee.getUser().getIsActive()) {
      logger.info(
          "Trainee with this username is already active, reactivating.");
    } else {
      trainee.getUser().setIsActive(true);
    }
    logger.info("Transaction ID: {} - Trainee activated: {}", transactionId, username);
    return traineeRepository.save(trainee);
  }

  @Transactional
  public Trainee deactivateTrainee(String username) throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deactivating trainee: {}", transactionId, username);

    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    if (!trainee.getUser().getIsActive()) {
      logger.info(
          "Trainee with this username is already inactive, reactivating.{}",
          username);

    } else {
      trainee.getUser().setIsActive(false);
    }
    logger.info("Transaction ID: {} - Trainee deactivated: {}", transactionId, username);
    return traineeRepository.save(trainee);
  }

  @Transactional
  public void deleteTraineeByUsername(String username)
      throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Attempting to delete trainee: {}", transactionId, username);

    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    logger.info("Trainee with Username: {} found. Proceeding with deletion.",
                username);

    traineeRepository.deleteByUser_Username(username);

    logger.info("Transaction ID: {} - Trainee deleted: {}", transactionId, username);
  }

  @Transactional
  public List<Training>
  getTraineeTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String trainerName, String trainingType)
      throws NotFoundException {
    logger.info("Fetching trainings for trainee: {}", username);

    Optional<Trainee> traineeOptional = traineeRepository.findByUser_Username(username);
    if (traineeOptional.isEmpty()) {
      logger.error("Trainee not found: {}", username);
      throw new NotFoundException("Not found: " + username);
    }

    List<Training> trainings = traineeRepository.findTrainingsByCriteria(username, fromDate, toDate, trainerName, trainingType);
    logger.info("Found {} trainings for trainee: {}", trainings.size(), username);
    return trainings;
  }

  @Transactional
  public List<Trainer> getTrainersList(Long id) {
    logger.info("Fetching trainers for trainee ID={}", id);
    List<Trainer> trainers = traineeRepository.findTrainers(id);
    logger.info("Found {} trainers for trainee ID={}", trainers.size(), id);
    return trainers;
  }

  @Transactional
  public List<Trainer> getUnassignedTrainersForTrainee(String username)
      throws NotFoundException {
    logger.info("Fetching unassigned trainers for trainee: {}", username);

    Optional<Trainee> traineeOptional = traineeRepository.findByUser_Username(username);
    if (traineeOptional.isEmpty()) {
      logger.error("Trainee not found: {}", username);
      throw new NotFoundException("Not found: " + username);
    }

    Trainee trainee = traineeOptional.get();
    List<Trainer> unassignedTrainers = trainerRepository.findTrainersNotAssignedToTrainee(trainee.getId());
    logger.info("Found {} unassigned trainers for trainee: {}", unassignedTrainers.size(), username);
    return unassignedTrainers;
  }

  @Transactional
  public Trainee updateTrainee(@Valid Trainee updatedTrainee)
      throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    Long id = updatedTrainee.getId();
    logger.info("Transaction ID: {} - Updating trainee with ID={}", transactionId, id);

    Trainee trainee = traineeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found: " + id));

    trainee.getUser().setFirstName(updatedTrainee.getUser().getFirstName());
    trainee.getUser().setLastName(updatedTrainee.getUser().getLastName());
    trainee.getUser().setUsername(updatedTrainee.getUser().getUsername());
    trainee.getUser().setPassword(updatedTrainee.getUser().getPassword());
    trainee.getUser().setIsActive(updatedTrainee.getUser().getIsActive());
    trainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
    trainee.setAddress(updatedTrainee.getAddress());

    Trainee updated = traineeRepository.save(trainee);
    logger.info("Transaction ID: {} - Trainee updated successfully: ID={}", transactionId, id);
    return updated;
  }

  @Transactional
  public Trainee assignOrRemoveTrainer(String traineeUsername,
                                       String trainerUsername, boolean assign) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - {} trainer {} to trainee {}", transactionId,
            assign ? "Assigning" : "Removing", trainerUsername, traineeUsername);

    Trainee trainee = traineeRepository.findByUser_Username(traineeUsername)
            .orElseThrow(() -> new NotFoundException("Trainee not found: " + traineeUsername));

    Trainer trainer = trainerRepository.findByUser_Username(trainerUsername)
            .orElseThrow(() -> new NotFoundException("Trainer not found: " + trainerUsername));

    if (assign) {
      if (trainee.getTrainers().contains(trainer)) {
        logger.warn("Transaction ID: {} - Trainer {} is already assigned to trainee {}",
                transactionId, trainerUsername, traineeUsername);
        throw new IllegalArgumentException("Trainer is already assigned to this trainee.");
      }
      trainee.getTrainers().add(trainer);
      logger.info("Transaction ID: {} - Trainer {} assigned to trainee {}", transactionId, trainerUsername, traineeUsername);
    } else {
      if (!trainee.getTrainers().contains(trainer)) {
        logger.warn("Transaction ID: {} - Trainer {} is not assigned to trainee {}",
                transactionId, trainerUsername, traineeUsername);
        throw new IllegalArgumentException("Trainer is not assigned to this trainee.");
      }
      trainee.getTrainers().remove(trainer);
      logger.info("Transaction ID: {} - Trainer {} removed from trainee {}", transactionId, trainerUsername, traineeUsername);
    }

    return traineeRepository.save(trainee);
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
