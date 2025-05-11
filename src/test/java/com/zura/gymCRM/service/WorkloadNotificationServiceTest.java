package com.zura.gymCRM.service;

import com.zura.gymCRM.client.WorkloadServiceClient;
import com.zura.gymCRM.dto.WorkloadRequest;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.WorkloadServiceException;
import com.zura.gymCRM.messaging.WorkloadMessage;
import com.zura.gymCRM.messaging.WorkloadMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkloadNotificationServiceTest {

    @Mock
    private WorkloadMessageProducer workloadMessageProducer;

    @InjectMocks
    private WorkloadNotificationService workloadNotificationService;

    private Training training;
    private Trainer trainer;
    private User trainerUser;
    private Trainee trainee;
    private User traineeUser;
    private final String username = "trainer1";
    private final int trainingDuration = 60;
    private final Date trainingDate = new Date();

    @BeforeEach
    void setUp() {
        // Set up test data
        trainerUser = new User();
        trainerUser.setId(1L);
        trainerUser.setUsername(username);
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setIsActive(true);

        traineeUser = new User();
        traineeUser.setId(2L);
        traineeUser.setUsername("trainee1");
        traineeUser.setFirstName("Jane");
        traineeUser.setLastName("Smith");
        traineeUser.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setSpecialization(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName("Test Training");
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
    }

    @Test
    void notifyTrainingCreated_Success() {
        // Arrange
        doNothing().when(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));

        // Act
        workloadNotificationService.notifyTrainingCreated(training);

        // Assert
        verify(workloadMessageProducer).sendWorkloadMessage(argThat(message -> {
            assertEquals(username, message.getUsername());
            assertEquals("John", message.getFirstName());
            assertEquals("Doe", message.getLastName());
            assertEquals(true, message.isActive());
            assertEquals(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getYear(), message.getYear());
            assertEquals(LocalDate.ofInstant(trainingDate.toInstant(), ZoneId.systemDefault()).getMonthValue(), message.getMonth());
            assertEquals(trainingDuration, message.getTrainingDuration());
            assertEquals(WorkloadMessage.MessageType.CREATE_UPDATE, message.getMessageType());
            assertNotNull(message.getTransactionId());
            return true;
        }));
    }

    @Test
    void notifyTrainingCreated_FailureSendingMessage_ThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingCreated(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service about new training"));
        verify(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));
    }

    @Test
    void notifyTrainingUpdated_Success() {
        // Arrange
        doNothing().when(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));

        // Act
        workloadNotificationService.notifyTrainingUpdated(training);

        // Assert
        verify(workloadMessageProducer).sendWorkloadMessage(argThat(message -> {
            assertEquals(username, message.getUsername());
            assertEquals(WorkloadMessage.MessageType.CREATE_UPDATE, message.getMessageType());
            return true;
        }));
    }

    @Test
    void notifyTrainingDeleted_Success() {
        // Arrange
        doNothing().when(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));

        // Act
        workloadNotificationService.notifyTrainingDeleted(training);

        // Assert
        verify(workloadMessageProducer).sendWorkloadMessage(argThat(message -> {
            assertEquals(username, message.getUsername());
            assertEquals(0, message.getTrainingDuration()); // Duration should be 0 when deleting
            assertEquals(WorkloadMessage.MessageType.DELETE, message.getMessageType());
            return true;
        }));
    }

    @Test
    void notifyTrainingDeleted_FailureSendingMessage_ThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Test exception")).when(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));

        // Act & Assert
        WorkloadServiceException exception = assertThrows(WorkloadServiceException.class,
                () -> workloadNotificationService.notifyTrainingDeleted(training));

        assertTrue(exception.getMessage().contains("Failed to notify workload service about deleted training"));
        verify(workloadMessageProducer).sendWorkloadMessage(any(WorkloadMessage.class));
    }
}