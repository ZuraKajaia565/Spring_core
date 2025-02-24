package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainingDAO;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {
  private TrainingDAO trainingDAO;
  private static final Logger logger =
      LoggerFactory.getLogger(TrainingService.class);

  @Autowired
  public void setTrainingDAO(TrainingDAO trainingDAO) {
    this.trainingDAO = trainingDAO;
  }

  public Training createTraining(Training training) {
    try {
      logger.info("Creating training: Name={}, TraineeID={}, TrainerID={}",
                  training.getTrainingName(), training.getTraineeId(),
                  training.getTrainerId());

      trainingDAO.addTraining(training);

      logger.info(
          "Training created successfully for TraineeID={}, TrainerID={}",
          training.getTraineeId(), training.getTrainerId());
      return training;
    } catch (Exception e) {
      logger.error("Error creating training for TraineeID={}, TrainerID={}",
                   training.getTraineeId(), training.getTrainerId(), e);
      throw new AddException("Failed to create training for trainee ID " +
                             training.getTraineeId() + " and trainer ID " +
                             training.getTrainerId());
    }
  }

  public Optional<Training> getTraining(int traineeId, int trainerId) {
    try {
      logger.info("Fetching training for TraineeID={}, TrainerID={}", traineeId,
                  trainerId);

      Training training = trainingDAO.getTraining(traineeId, trainerId);

      if (training != null) {
        logger.info("Training found: {}", training.getTrainingTypeName());
      } else {
        logger.warn("No training found for TraineeID={}, TrainerID={}",
                    traineeId, trainerId);
      }

      return Optional.ofNullable(training);

    } catch (Exception e) {
      logger.error("Error fetching training for TraineeID={}, TrainerID={}",
                   traineeId, trainerId, e);
      throw new NotFoundException(
          "Failed to retrieve training for trainee ID " + traineeId +
          " and trainer ID " + trainerId);
    }
  }
}
