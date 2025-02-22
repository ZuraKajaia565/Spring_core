package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainerDAO;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainer;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import com.zura.gymCRM.storage.TrainerStorage;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
  private TrainerDAO trainerDAO;
  private TrainerStorage trainerStorage;

  @Autowired
  public void setTrainerDAO(TrainerDAO trainerDAO) {
    this.trainerDAO = trainerDAO;
  }

  @Autowired
  public void setTrainerStorage(TrainerStorage trainerStorage) {
    this.trainerStorage = trainerStorage;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TrainerService.class);

  public Trainer createTrainer(int userId, String firstName, String lastName,
                               boolean isActive, String specialization,
                               String trainingtypeName, Training training) {
    try {
      logger.info("Attempting to create trainer: {} {}", firstName, lastName);

      String username = generateUsername(firstName, lastName);
      String password = generateRandomPassword();
      Trainer trainer = new Trainer(
          userId, firstName, lastName, username, password, isActive,
          specialization, new TrainingType(trainingtypeName), training);

      trainerDAO.addTrainer(trainer);
      logger.info("Trainer created successfully: {}", username);

      return trainer;
    } catch (Exception e) {
      logger.error("Error creating trainer: {} {}", firstName, lastName, e);
      throw new AddException("Failed to create trainer: " + firstName + " " +
                             lastName);
    }
  }

  public Trainer updateTrainer(Trainer updatedTrainer) {
    try {
      logger.info("Attempting to update trainer: {}",
                  updatedTrainer.getUserId());

      boolean updated = trainerDAO.updateTrainer(updatedTrainer);
      if (!updated) {
        logger.warn("Trainer not found: ID {}", updatedTrainer.getUserId());
        throw new RuntimeException("Trainer with ID " +
                                   updatedTrainer.getUserId() + " not found!");
      }

      logger.info("Trainer updated successfully: ID {}",
                  updatedTrainer.getUserId());
      return updatedTrainer;
    } catch (Exception e) {
      logger.error("Error updating trainer: {}", updatedTrainer.getUserId(), e);
      throw new RuntimeException(
          "Failed to update trainer: " + updatedTrainer.getUserId(), e);
    }
  }

  public Trainer getTrainer(int userId) {
    try {
      logger.info("Fetching trainer with ID: {}", userId);
      Trainer trainer = trainerDAO.getTrainer(userId);
      if (trainer != null) {
        logger.info("Trainer found: {}", trainer.getUserName());
      } else {
        logger.warn("Trainer not found with ID: {}", userId);
      }
      return trainer;
    } catch (Exception e) {
      logger.error("Error retrieving trainer with ID: {}", userId, e);
      throw new NotFoundException("Failed to retrieve trainer with ID: " +
                                  userId);
    }
  }

  private String generateUsername(String firstName, String lastName) {
    String s = firstName + "." + lastName;
    int cnt = 0;
    for (Map.Entry<Integer, Trainer> entry :
         trainerStorage.getTrainerMap().entrySet()) {
      Trainer trainee = entry.getValue();
      if (trainee.getFirstName().equals(firstName) &&
          trainee.getLastName().equals(lastName)) {
        cnt++;
      }
    }
    if (cnt != 0) {
      s += cnt;
    }
    return s;
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
