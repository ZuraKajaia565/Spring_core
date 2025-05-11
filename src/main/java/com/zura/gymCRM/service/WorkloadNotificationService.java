package com.zura.gymCRM.service;

import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import com.zura.gymCRM.messaging.WorkloadMessage;
import com.zura.gymCRM.messaging.WorkloadMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final WorkloadMessageProducer messageProducer;

    @Autowired
    public WorkloadNotificationService(WorkloadMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
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
            // Get year and month from training date
            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Create a message
            WorkloadMessage message = new WorkloadMessage(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getIsActive(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    training.getTrainingDuration(),
                    WorkloadMessage.MessageType.CREATE_UPDATE,
                    transactionId
            );

            // Send message
            messageProducer.sendWorkloadMessage(message);
            logger.debug("Successfully notified workload service about new training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about new training: {}", e.getMessage(), e);
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
            // Get year and month from training date
            LocalDate trainingDate = training.getTrainingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Create a message
            WorkloadMessage message = new WorkloadMessage(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getIsActive(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    training.getTrainingDuration(),
                    WorkloadMessage.MessageType.CREATE_UPDATE,
                    transactionId
            );

            // Send message
            messageProducer.sendWorkloadMessage(message);
            logger.debug("Successfully notified workload service about updated training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about updated training: {}", e.getMessage(), e);
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

            // Create a message
            WorkloadMessage message = new WorkloadMessage(
                    trainer.getUser().getUsername(),
                    trainer.getUser().getFirstName(),
                    trainer.getUser().getLastName(),
                    trainer.getUser().getIsActive(),
                    trainingDate.getYear(),
                    trainingDate.getMonthValue(),
                    0, // Set to 0 since we're deleting
                    WorkloadMessage.MessageType.DELETE,
                    transactionId
            );

            // Send message
            messageProducer.sendWorkloadMessage(message);
            logger.debug("Successfully notified workload service about deleted training");
        } catch (Exception e) {
            logger.error("Failed to notify workload service about deleted training: {}", e.getMessage(), e);
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