package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainerDAO;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainer;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
  private TrainerDAO trainerDAO;

  @Autowired
  public void setTrainerDAO(TrainerDAO trainerDAO) {
    this.trainerDAO = trainerDAO;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TrainerService.class);

  public Trainer createTrainer(Trainer trainer) {
    try {
      logger.info("Attempting to create trainer: {} {}", trainer.getFirstName(),
                  trainer.getLastName());

      Map<Integer, Trainer> usermap = trainerDAO.getAllTrainers();
      trainer.setUserName(UsernameGenerator.generateUsername(
          trainer.getFirstName(), trainer.getLastName(), usermap));
      trainer.setPassword(generateRandomPassword());
      trainerDAO.addTrainer(trainer);
      logger.info("Trainer created successfully: {}", trainer.getUserName());

      return trainer;
    } catch (Exception e) {
      logger.error("Error creating trainer: {} {}", trainer.getFirstName(),
                   trainer.getLastName(), e);
      throw new AddException(
          "Failed to create trainer: " + trainer.getFirstName() + " " +
          trainer.getLastName());
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

  public Optional<Trainer> getTrainer(int userId) {
    try {
      logger.info("Fetching trainer with ID: {}", userId);
      Optional<Trainer> trainer =
          Optional.ofNullable(trainerDAO.getTrainer(userId));
      trainer.ifPresentOrElse(
          t
          -> logger.info("Trainer found: {}", t.getUserName()),
          () -> logger.warn("Trainer not found with ID: {}", userId));

      return trainer;
    } catch (Exception e) {
      logger.error("Error retrieving trainer with ID: {}", userId, e);
      throw new NotFoundException("Failed to retrieve trainer with ID: " +
                                  userId);
    }
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
