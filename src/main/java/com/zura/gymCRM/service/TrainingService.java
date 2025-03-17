package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainingRepository;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {
  private TrainingRepository trainingRepository;
  private static final Logger logger =
      LoggerFactory.getLogger(TrainingService.class);

  @Autowired
  public void setTrainingRepository(TrainingRepository trainingRepository) {
    this.trainingRepository = trainingRepository;
  }

  @Transactional
  public Training createTraining(Training training) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Creating training for TrainingID={}", transactionId, training.getId());

    trainingRepository.save(training);

    logger.info("Transaction ID: {} Training created successfully for TrainingID={}", transactionId, training.getId());
    return training;
  }

  public Optional<Training> getTraining(Long trainingId) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching training for TrainingID={}", transactionId, trainingId);

    Optional<Training> training = trainingRepository.findById(trainingId);

    if (training.isPresent()) {
      logger.info("Transaction ID: {} Training found: {}", transactionId, trainingId);
    } else {
      logger.warn("Transaction ID: {} No training found for TrainingID={}", transactionId, trainingId);
    }

    return training;
  }
}
