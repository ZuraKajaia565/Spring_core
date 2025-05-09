package com.zura.gymCRM.service;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Service for notifying the workload service about training changes
 */
@Service
public class WorkloadNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(WorkloadNotificationService.class);

    private final WorkloadServiceClient workloadServiceClient;

    @Autowired
    public WorkloadNotificationService(WorkloadServiceClient workloadServiceClient) {
        this.workloadServiceClient = workloadServiceClient;
    }

    /**
     * Notifies the workload service about a new training
     *
     * @param training The training entity
     */
    public void notifyTrainingCreated(Training training) {
        String transactionId = getOrCreateTransactionId();
        Trainer trainer = training.getTrainer();

        logger.debug("Notifying workload service about new training for trainer: {}",
                trainer.getUser().getUsername());

        try {
            // Create workload request from training
            WorkloadRequest request = new WorkloadRequest(
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getIsActive(),
                    training.getTrainingDuration()
            );

            // Get year and month from training date
            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Update the workload in the workload service
            ResponseEntity<Void> response = workloadServiceClient.updateWorkload(
                    trainer.getUser().getUsername(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    request,
                    transactionId
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WorkloadServiceException("Failed to notify workload service: "
                        + response.getStatusCode());
            }

            logger.debug("Successfully notified workload service about new training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about new training: {}",
                    e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about new training", e);
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

        logger.debug("Notifying workload service about updated training for trainer: {}",
                trainer.getUser().getUsername());

        try {
            // Create workload request from training
            WorkloadRequest request = new WorkloadRequest(
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getIsActive(),
                    training.getTrainingDuration()
            );

            // Get year and month from training date
            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Update the workload in the workload service
            ResponseEntity<Void> response = workloadServiceClient.updateWorkload(
                    trainer.getUser().getUsername(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    request,
                    transactionId
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WorkloadServiceException("Failed to notify workload service: "
                        + response.getStatusCode());
            }

            logger.debug("Successfully notified workload service about updated training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about updated training: {}",
                    e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about updated training", e);
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

        logger.debug("Notifying workload service about deleted training for trainer: {}",
                trainer.getUser().getUsername());

        try {
            // Get year and month from training date
            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Delete the workload in the workload service
            ResponseEntity<Void> response = workloadServiceClient.deleteWorkload(
                    trainer.getUser().getUsername(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    transactionId
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new WorkloadServiceException("Failed to notify workload service: "
                        + response.getStatusCode());
            }

            logger.debug("Successfully notified workload service about deleted training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about deleted training: {}",
                    e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about deleted training", e);
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