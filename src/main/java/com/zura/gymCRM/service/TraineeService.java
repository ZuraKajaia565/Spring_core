package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TraineeDAO;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainee;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
  private TraineeDAO traineeDAO;

  @Autowired
  public void setTraineeDAO(TraineeDAO traineeDAO) {
    this.traineeDAO = traineeDAO;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TraineeService.class);

  public Trainee createTrainee(Trainee trainee) {
    try {
      logger.info("Attempting to create trainee: {} {}", trainee.getFirstName(),
                  trainee.getLastName());

      trainee.setUserName(traineeDAO.generateUsername(trainee.getFirstName(),
                                                      trainee.getLastName()));
      trainee.setPassword(generateRandomPassword());
      traineeDAO.addTrainee(trainee);
      logger.info("Trainee created successfully: {}", trainee.getUsername());

      return trainee;
    } catch (Exception e) {
      logger.error("Error while creating trainee: {} {}",
                   trainee.getFirstName(), trainee.getLastName(), e);
      throw new AddException(
          "Failed to create trainee: " + trainee.getFirstName() + " " +
          trainee.getLastName());
    }
  }

  public Trainee updateTrainee(Trainee trainee) {
    try {
      logger.info("Attempting to update trainee: {}", trainee.getUsername());

      if (traineeDAO.updateTrainee(trainee)) {
        logger.info("Trainee updated successfully: {}", trainee.getUsername());
        return trainee;
      } else {
        logger.warn("Trainee not found: {}", trainee.getUsername());
        throw new RuntimeException("Trainee not found: " +
                                   trainee.getUsername());
      }
    } catch (Exception e) {
      logger.error("Error while updating trainee: {}", trainee.getUsername(),
                   e);
      throw new RuntimeException(
          "Failed to update trainee: " + trainee.getUsername(), e);
    }
  }

  public void deleteTrainee(int userId) { traineeDAO.deleteTrainee(userId); }

  public Optional<Trainee> selectTrainee(int userId) {
    try {
      logger.info("Fetching trainee with ID: {}", userId);
      Optional<Trainee> trainee =
          Optional.ofNullable(traineeDAO.getTrainee(userId));

      trainee.ifPresentOrElse(
          t
          -> logger.info("Trainee found: {}", t.getUsername()),
          () -> logger.warn("Trainee not found with ID: {}", userId));

      return trainee;
    } catch (Exception e) {
      logger.error("Error while selecting trainee with ID: {}", userId, e);
      throw new NotFoundException("Failed to retrieve trainee with ID: " +
                                  userId);
    }
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
