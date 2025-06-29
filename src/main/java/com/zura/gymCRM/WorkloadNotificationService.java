package com.zura.gymCRM;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import com.zura.gymCRM.messaging.WorkloadMessage;
import com.zura.gymCRM.messaging.WorkloadMessageProducer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Enhanced service for notifying the workload service about training changes
 * Now supports both direct API calls via Feign and message queueing via
 * ActiveMQ (when available)
 */
@Service
public class WorkloadNotificationService {
  private static final Logger logger =
      LoggerFactory.getLogger(WorkloadNotificationService.class);

  private final WorkloadMessageProducer messageProducer; // Optional - only available when ActiveMQ is enabled
  private final WorkloadServiceClient workloadClient;

  @Autowired
  public WorkloadNotificationService(@Autowired(required = false) WorkloadMessageProducer messageProducer,
                                     WorkloadServiceClient workloadClient) {
    this.messageProducer = messageProducer;
    this.workloadClient = workloadClient;
    
    if (messageProducer == null) {
      logger.info("WorkloadMessageProducer not available - ActiveMQ is disabled. Using direct API calls only.");
    }
  }

  /**
   * Notifies the workload service about a new training using both direct API
   * and message queue for reliability (if ActiveMQ is enabled)
   *
   * @param training The training entity
   */
  public void notifyTrainingCreated(Training training) {
    String transactionId = getOrCreateTransactionId();
    Trainer trainer = training.getTrainer();

    logger.info("Notifying workload service about new training for trainer: {}",
                trainer.getUser().getUsername());

    try {
      // Get year and month from training date
      LocalDate trainingDate = training.getTrainingDate()
                                   .toInstant()
                                   .atZone(ZoneId.systemDefault())
                                   .toLocalDate();

      int year = trainingDate.getYear();
      int month = trainingDate.getMonthValue();
      int duration = training.getTrainingDuration();
      String username = trainer.getUser().getUsername();

      // 1. Try direct API call first using Feign client
      try {
        // Create request for direct API call
        WorkloadRequest workloadRequest = new WorkloadRequest(
            trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
            trainer.getUser().getIsActive(), duration);

        ResponseEntity<Void> response = workloadClient.updateWorkload(
            username, year, month, workloadRequest, transactionId);

        if (response.getStatusCode().is2xxSuccessful()) {
          logger.info("Successfully updated workload via direct API call");
        } else {
          // If direct API fails, fall back to message queue (if available)
          if (messageProducer != null) {
            logger.warn("Failed to update workload via API, falling back to message queue");
            WorkloadMessage message = new WorkloadMessage(
                username, trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
                year, month, duration, WorkloadMessage.MessageType.CREATE_UPDATE,
                transactionId);
            messageProducer.sendWorkloadMessage(message);
          } else {
            logger.warn("Failed to update workload via API and message queue is not available (ActiveMQ disabled)");
            throw new WorkloadServiceException("Failed to update workload via API and message queue is not available");
          }
        }
      } catch (Exception e) {
        // If direct API call fails, fall back to message queue (if available)
        if (messageProducer != null) {
          logger.warn("Failed to connect to workload service API: {}. Falling back to message queue", e.getMessage());
          WorkloadMessage message = new WorkloadMessage(
              username, trainer.getUser().getFirstName(),
              trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
              year, month, duration, WorkloadMessage.MessageType.CREATE_UPDATE,
              transactionId);
          messageProducer.sendWorkloadMessage(message);
        } else {
          logger.error("Failed to connect to workload service API and message queue is not available (ActiveMQ disabled): {}", e.getMessage());
          throw new WorkloadServiceException("Failed to connect to workload service API and message queue is not available", e);
        }
      }

      logger.info("Successfully notified workload service about new training");
    } catch (Exception e) {
      logger.error("Failed to notify workload service about new training: {}",
                   e.getMessage(), e);
      throw new WorkloadServiceException(
          "Failed to notify workload service about new training", e);
    }
  }

  /**
   * Notifies the workload service about an updated training
   *
   * @param training The updated training entity
   */
  public void notifyTrainingUpdated(Training training) {
    String transactionId = getOrCreateTransactionId();
    Trainer trainer = training.getTrainer();

    logger.info(
        "Notifying workload service about updated training for trainer: {}",
        trainer.getUser().getUsername());

    try {
      // Get year and month from training date
      LocalDate trainingDate = training.getTrainingDate()
                                   .toInstant()
                                   .atZone(ZoneId.systemDefault())
                                   .toLocalDate();

      int year = trainingDate.getYear();
      int month = trainingDate.getMonthValue();
      int duration = training.getTrainingDuration();
      String username = trainer.getUser().getUsername();

      // Try direct API call first
      try {
        WorkloadRequest workloadRequest = new WorkloadRequest(
            trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
            trainer.getUser().getIsActive(), duration);

        ResponseEntity<Void> response = workloadClient.updateWorkload(
            username, year, month, workloadRequest, transactionId);

        if (response.getStatusCode().is2xxSuccessful()) {
          logger.info("Successfully updated workload via direct API call");
        } else {
          // If direct API fails, fall back to message queue (if available)
          if (messageProducer != null) {
            logger.warn("Failed to update workload via API, falling back to message queue");
            WorkloadMessage message = new WorkloadMessage(
                username, trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
                year, month, duration, WorkloadMessage.MessageType.CREATE_UPDATE,
                transactionId);
            messageProducer.sendWorkloadMessage(message);
          } else {
            logger.warn("Failed to update workload via API and message queue is not available (ActiveMQ disabled)");
            throw new WorkloadServiceException("Failed to update workload via API and message queue is not available");
          }
        }
      } catch (Exception e) {
        // If direct API call fails, fall back to message queue (if available)
        if (messageProducer != null) {
          logger.warn("Failed to connect to workload service API: {}. Falling back to message queue", e.getMessage());
          WorkloadMessage message = new WorkloadMessage(
              username, trainer.getUser().getFirstName(),
              trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
              year, month, duration, WorkloadMessage.MessageType.CREATE_UPDATE,
              transactionId);
          messageProducer.sendWorkloadMessage(message);
        } else {
          logger.error("Failed to connect to workload service API and message queue is not available (ActiveMQ disabled): {}", e.getMessage());
          throw new WorkloadServiceException("Failed to connect to workload service API and message queue is not available", e);
        }
      }

      logger.info(
          "Successfully notified workload service about updated training");
    } catch (Exception e) {
      logger.error(
          "Failed to notify workload service about updated training: {}",
          e.getMessage(), e);
      throw new WorkloadServiceException(
          "Failed to notify workload service about updated training", e);
    }
  }

  /**
   * Notifies the workload service about a deleted training
   *
   * @param training The training entity that was deleted
   */
  public void notifyTrainingDeleted(Training training) {
    String transactionId = getOrCreateTransactionId();
    Trainer trainer = training.getTrainer();

    logger.info(
        "Notifying workload service about deleted training for trainer: {}",
        trainer.getUser().getUsername());

    try {
      // Get year and month from training date
      LocalDate trainingDate = training.getTrainingDate()
                                   .toInstant()
                                   .atZone(ZoneId.systemDefault())
                                   .toLocalDate();

      int year = trainingDate.getYear();
      int month = trainingDate.getMonthValue();
      String username = trainer.getUser().getUsername();

      // Try direct API call first
      try {
        ResponseEntity<Void> response = workloadClient.deleteWorkload(username, year, month, transactionId);

        if (response.getStatusCode().is2xxSuccessful()) {
          logger.info("Successfully deleted workload via direct API call");
        } else {
          // If direct API fails, fall back to message queue (if available)
          if (messageProducer != null) {
            logger.warn("Failed to delete workload via API, falling back to message queue");
            WorkloadMessage message = new WorkloadMessage(
                username, trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
                year, month, 0, WorkloadMessage.MessageType.DELETE,
                transactionId);
            messageProducer.sendWorkloadMessage(message);
          } else {
            logger.warn("Failed to delete workload via API and message queue is not available (ActiveMQ disabled)");
            throw new WorkloadServiceException("Failed to delete workload via API and message queue is not available");
          }
        }
      } catch (Exception e) {
        // If direct API call fails, fall back to message queue (if available)
        if (messageProducer != null) {
          logger.warn("Failed to connect to workload service API: {}. Falling back to message queue", e.getMessage());
          WorkloadMessage message = new WorkloadMessage(
              username, trainer.getUser().getFirstName(),
              trainer.getUser().getLastName(), trainer.getUser().getIsActive(),
              year, month, 0, WorkloadMessage.MessageType.DELETE,
              transactionId);
          messageProducer.sendWorkloadMessage(message);
        } else {
          logger.error("Failed to connect to workload service API and message queue is not available (ActiveMQ disabled): {}", e.getMessage());
          throw new WorkloadServiceException("Failed to connect to workload service API and message queue is not available", e);
        }
      }

      logger.info(
          "Successfully notified workload service about deleted training");
    } catch (Exception e) {
      logger.error(
          "Failed to notify workload service about deleted training: {}",
          e.getMessage(), e);
      throw new WorkloadServiceException(
          "Failed to notify workload service about deleted training", e);
    }
  }

  /**
   * Gets the current transaction ID from MDC or creates a new one
   */
  private String getOrCreateTransactionId() {
    String transactionId = MDC.get("transactionId");
    if (transactionId == null || transactionId.isEmpty()) {
      transactionId = UUID.randomUUID().toString();
    }
    return transactionId;
  }
}
