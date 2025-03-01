package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainingRepository;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    trainingRepository.save(training);

    logger.info("Training created successfully for TrainingID={}",
                training.getId());
    return training;
  }

  public Optional<Training> getTraining(Long trainingId) {

    logger.info("Fetching training for TrainingID={}", trainingId);

    Optional<Training> training = trainingRepository.findById(trainingId);

    if (training != null) {
      logger.info("Training found: {}", trainingId);
    } else {
      logger.warn("No training found for TrainingID={}", trainingId);
    }

    return training;
  }
}
