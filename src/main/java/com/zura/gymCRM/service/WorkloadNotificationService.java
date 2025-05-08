package com.zura.gymCRM.service;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Service responsible for notifying the workload service about training changes
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

        try {
            logger.info("Notifying workload service about new training for trainer: {}",
                    training.getTrainer().getUser().getUsername());

            WorkloadRequest request = createWorkloadRequest(training);

            workloadServiceClient.createTrainerWorkload(
                    training.getTrainer().getUser().getUsername(),
                    request,
                    transactionId);

            logger.info("Successfully notified workload service about new training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about new training: {}", e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about new training", e);
        }
    }

    /**
     * Notifies the workload service about a training update
     *
     * @param training The training entity
     */
    public void notifyTrainingUpdated(Training training) {
        String transactionId = getOrCreateTransactionId();

        try {
            logger.info("Notifying workload service about updated training for trainer: {}",
                    training.getTrainer().getUser().getUsername());

            WorkloadRequest request = createWorkloadRequest(training);

            workloadServiceClient.updateTrainerWorkload(
                    training.getTrainer().getUser().getUsername(),
                    request,
                    transactionId);

            logger.info("Successfully notified workload service about updated training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about updated training: {}", e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about updated training", e);
        }
    }

    /**
     * Notifies the workload service about a deleted training
     *
     * @param training The training entity
     */
    public void notifyTrainingDeleted(Training training) {
        String transactionId = getOrCreateTransactionId();

        try {
            logger.info("Notifying workload service about deleted training for trainer: {}",
                    training.getTrainer().getUser().getUsername());

            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            workloadServiceClient.deleteTrainerWorkload(
                    training.getTrainer().getUser().getUsername(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    transactionId);

            logger.info("Successfully notified workload service about deleted training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about deleted training: {}", e.getMessage(), e);
            throw new WorkloadServiceException("Failed to notify workload service about deleted training", e);
        }
    }

    /**
     * Creates a workload request from a training entity
     */
    private WorkloadRequest createWorkloadRequest(Training training) {
        Trainer trainer = training.getTrainer();

        return new WorkloadRequest(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getIsActive(),
                training.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                training.getTrainingDuration()
        );
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