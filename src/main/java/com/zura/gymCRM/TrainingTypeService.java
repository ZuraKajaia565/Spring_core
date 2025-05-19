package com.zura.gymCRM;

import com.zura.gymCRM.dao.TrainingTypeRepository;
import com.zura.gymCRM.entities.TrainingType;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeService {
  @Autowired TrainingTypeRepository trainingTypeRepository;

  private static final Logger logger =
          LoggerFactory.getLogger(TrainingService.class);

  public Optional<TrainingType> findTrainingTypeById(Long id) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching TrainingType with ID={}", transactionId, id);

    Optional<TrainingType> trainingType = trainingTypeRepository.findById(id);

    if (trainingType.isPresent()) {
      logger.info("Transaction ID: {} TrainingType found with ID={}", transactionId, id);
    } else {
      logger.warn("Transaction ID: {} No TrainingType found with ID={}", transactionId, id);
    }

    return trainingType;
  }

  public Optional<TrainingType>
  findTrainingTypeByTrainingTypeName(String trainingtypename) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching TrainingType with name={}", transactionId, trainingtypename);

    Optional<TrainingType> trainingType = trainingTypeRepository.findByTrainingTypeName(trainingtypename);

    if (trainingType.isPresent()) {
      logger.info("Transaction ID: {} TrainingType found with name={}", transactionId, trainingtypename);
    } else {
      logger.warn("Transaction ID: {} No TrainingType found with name={}", transactionId, trainingtypename);
    }

    return trainingType;
  }

  public List<TrainingType> findAllTrainings() {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching all TrainingTypes", transactionId);

    List<TrainingType> trainingTypes = trainingTypeRepository.findAll();

    logger.info("Transaction ID: {} Fetched {} TrainingTypes", transactionId, trainingTypes.size());

    return trainingTypes;
  }
}
