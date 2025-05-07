package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainingRepository;
import com.zura.gymCRM.dto.WorkloadUpdateDto;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import jakarta.transaction.Transactional;

import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {
  private TrainingRepository trainingRepository;
  private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

  // New fields for workload notification
  @Autowired
  private WorkloadNotificationService workloadNotificationService;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  public void setTrainingRepository(TrainingRepository trainingRepository) {
    this.trainingRepository = trainingRepository;
  }

  @Transactional
  public Training createTraining(Training training) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Creating training for TrainingID={}", transactionId, training.getId());

    // Save the training to the repository
    Training savedTraining = trainingRepository.save(training);

    // NEW CODE: Notify workload service about the new training
    try {
      // Create workload update DTO
      WorkloadUpdateDto workloadUpdate = new WorkloadUpdateDto();
      workloadUpdate.setUsername(training.getTrainer().getUser().getUsername());
      workloadUpdate.setFirstName(training.getTrainer().getUser().getFirstName());
      workloadUpdate.setLastName(training.getTrainer().getUser().getLastName());
      workloadUpdate.setActive(training.getTrainer().getUser().getIsActive());

      // Convert java.util.Date to java.time.LocalDate
      workloadUpdate.setTrainingDate(training.getTrainingDate().toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDate());

      workloadUpdate.setTrainingDuration(training.getTrainingDuration());
      workloadUpdate.setActionType(WorkloadUpdateDto.ActionType.ADD);

      // Get UserDetails for JWT generation
      UserDetails userDetails = userDetailsService.loadUserByUsername(
              training.getTrainer().getUser().getUsername());

      // Notify workload service
      workloadNotificationService.notifyWorkloadChange(workloadUpdate, userDetails);
      logger.info("Transaction ID: {} - Workload service notified about new training", transactionId);
    } catch (Exception e) {
      // Log the error but don't fail the transaction
      logger.error("Transaction ID: {} - Failed to notify workload service: {}",
              transactionId, e.getMessage(), e);
    }

    logger.info("Transaction ID: {} Training created successfully for TrainingID={}", transactionId, training.getId());
    return savedTraining;
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

  // NEW METHOD: Delete training with workload notification
  @Transactional
  public void deleteTraining(Long id) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} - Deleting training with ID: {}", transactionId, id);

    Optional<Training> trainingOpt = trainingRepository.findById(id);
    if (trainingOpt.isPresent()) {
      Training training = trainingOpt.get();

      // Notify workload service before deleting
      try {
        WorkloadUpdateDto workloadUpdate = new WorkloadUpdateDto();
        workloadUpdate.setUsername(training.getTrainer().getUser().getUsername());
        workloadUpdate.setFirstName(training.getTrainer().getUser().getFirstName());
        workloadUpdate.setLastName(training.getTrainer().getUser().getLastName());
        workloadUpdate.setActive(training.getTrainer().getUser().getIsActive());

        // Convert java.util.Date to java.time.LocalDate
        workloadUpdate.setTrainingDate(training.getTrainingDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());

        workloadUpdate.setTrainingDuration(training.getTrainingDuration());
        workloadUpdate.setActionType(WorkloadUpdateDto.ActionType.DELETE);

        // Get UserDetails for JWT generation
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                training.getTrainer().getUser().getUsername());

        // Notify workload service
        workloadNotificationService.notifyWorkloadChange(workloadUpdate, userDetails);
        logger.info("Transaction ID: {} - Workload service notified about deleted training", transactionId);
      } catch (Exception e) {
        logger.error("Transaction ID: {} - Failed to notify workload service: {}",
                transactionId, e.getMessage(), e);
        // Don't propagate this exception to avoid affecting the main transaction
      }

      trainingRepository.delete(training);
      logger.info("Transaction ID: {} - Training deleted successfully", transactionId);
    } else {
      logger.warn("Transaction ID: {} - Training not found with ID: {}", transactionId, id);
      throw new NotFoundException("Training not found with ID: " + id);
    }
  }
}